# recommender.py
from data_loader import (
    load_product_data,
    load_rating_data,
    create_user_item_matrix,
    get_product_category
)
from content_based import (
    feature_engineering,
    calculate_content_similarity,
    recommend_content_based
)
from collaborative_filtering import (
    item_based_recommend,
    user_based_recommend,
    calculate_user_similarity,
    calculate_item_similarity,
)
from es import (
    update_es
)
from es_spark import (
    update_es_with_spark
)
import logging
import traceback

# 로깅 설정
logger = logging.getLogger(__name__)


class Recommender:
    """추천 시스템 클래스"""

    def __init__(self):
        """추천 시스템 초기화"""
        logger.info("Initializing Recommender instance...")
        # 데이터 로드
        self.data_dict = load_product_data()

        # 콘텐츠 기반 필터링 설정
        logger.info("Setting up content-based filtering...")
        self.processed_data = feature_engineering(self.data_dict)
        self.content_similarity_dict = calculate_content_similarity(self.processed_data)

        # 협업 필터링 데이터 초기화
        logger.info("Setting up collaborative filtering...")
        self.reinitialize_collaborative_filtering()

        logger.info("Recommender system successfully initialized!")

    def reinitialize_collaborative_filtering(self):
        """협업 필터링 데이터만 재초기화하는 메서드"""
        logger.info("Starting collaborative filtering data reinitialization...")
        try:
            # 최신 평점 데이터 로드
            logger.info("Loading latest rating data...")
            self.rating_df = load_rating_data()
            # self.rating_df = load_rating_data_from_csv()
            if self.rating_df.empty:
                logger.warning("Rating data is empty - this might indicate a data loading issue")
            else:
                logger.info(f"Loaded rating data with {len(self.rating_df)} reviews")

            # 사용자-아이템 행렬 생성
            logger.info("Creating user-item matrix...")
            self.user_item_matrix = create_user_item_matrix(self.rating_df)
            logger.info(f"Created user-item matrix with shape {self.user_item_matrix.shape}")

            # # 협업 필터링 유사도 계산
            # logger.info("Calculating item similarity...")
            # self.item_similarity_df = calculate_item_similarity(self.user_item_matrix)
            # logger.info("Calculating user similarity...")
            # self.user_similarity_df = calculate_user_similarity(self.user_item_matrix)

            logger.info("Update user-item elasticsearch index...")
            update_es(self.user_item_matrix)
            # update_es_with_spark(self.user_item_matrix)

            logger.info("Collaborative filtering data successfully reinitialized!")
            return True
        except Exception as e:
            error_traceback = traceback.format_exc()
            logger.error(f"Error reinitializing collaborative filtering: {e}")
            logger.error(f"Traceback: {error_traceback}")
            raise

    def get_content_based_recommendations(self, product_idx, top_n=3):

        """콘텐츠 기반 추천"""
        # 카테고리 찾기
        category = get_product_category(product_idx, self.data_dict)

        if not category:
            return None, f"제품 '{product_idx}'을(를) 찾을 수 없습니다."

        # 해당 카테고리의 유사도 행렬
        similarity_df = self.content_similarity_dict[category]

        # 입력된 제품과 다른 제품과의 유사도
        if product_idx not in similarity_df.index:
            return None, f"제품 '{product_idx}'의 유사도 정보를 찾을 수 없습니다."

        # 추천 수행
        recommendations = recommend_content_based(
            product_idx,
            category,
            similarity_df,
            self.data_dict,
            top_n
        )

        return recommendations, None

    def get_item_based_recommendations(self, product_idx, top_n=5):
        """아이템 기반 협업 필터링 추천"""
        if product_idx not in self.user_item_matrix.columns:
            return None, f"제품 ID '{product_idx}'를 찾을 수 없습니다."

        logger.info("Calculating item similarity...")
        self.item_similarity_df = calculate_item_similarity(self.user_item_matrix, product_idx)

        recommendations = item_based_recommend(
            product_idx,
            self.item_similarity_df,
            self.user_item_matrix,
            top_n
        )

        return recommendations, None

    def get_user_based_recommendations(self, user_idx, top_n=5):
        """사용자 기반 협업 필터링 추천"""
        if user_idx not in self.user_item_matrix.index:
            return None, f"사용자 ID '{user_idx}'를 찾을 수 없습니다."

        logger.info("Calculating user similarity...")
        self.user_similarity_df = calculate_user_similarity(self.user_item_matrix, user_idx)

        recommendations = user_based_recommend(
            user_idx,
            self.user_similarity_df,
            self.user_item_matrix,
            top_n
        )

        return recommendations, None