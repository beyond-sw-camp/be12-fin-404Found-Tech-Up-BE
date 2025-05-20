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
    calculate_item_similarity,
    calculate_user_similarity,
    item_based_recommend,
    user_based_recommend
)


class Recommender:
    """추천 시스템 클래스"""

    def __init__(self):
        """추천 시스템 초기화"""
        # 데이터 로드
        self.data_dict = load_product_data()
        self.rating_df = load_rating_data()

        # 사용자-아이템 행렬 생성
        self.user_item_matrix = create_user_item_matrix(self.rating_df)

        # 콘텐츠 기반 필터링 설정
        self.processed_data = feature_engineering(self.data_dict)
        self.content_similarity_dict = calculate_content_similarity(self.processed_data)

        # 협업 필터링 설정
        self.item_similarity_df = calculate_item_similarity(self.user_item_matrix)
        self.user_similarity_df = calculate_user_similarity(self.user_item_matrix)

        print("추천 시스템이 성공적으로 초기화되었습니다.")

    def get_content_based_recommendations(self, product_name, top_n=3):
        """콘텐츠 기반 추천"""
        # 카테고리 찾기
        category = get_product_category(product_name, self.data_dict)

        if not category:
            return None, f"제품 '{product_name}'을(를) 찾을 수 없습니다."

        # 해당 카테고리의 유사도 행렬
        similarity_df = self.content_similarity_dict[category]

        # 입력된 제품과 다른 제품과의 유사도
        if product_name not in similarity_df.index:
            return None, f"제품 '{product_name}'의 유사도 정보를 찾을 수 없습니다."

        # 추천 수행
        recommendations = recommend_content_based(
            product_name,
            category,
            similarity_df,
            self.data_dict,
            top_n
        )

        return recommendations, None

    def get_item_based_recommendations(self, product_idx, top_n=5):
        """아이템 기반 협업 필터링 추천"""
        if product_idx not in self.item_similarity_df.index:
            return None, f"제품 ID '{product_idx}'를 찾을 수 없습니다."

        recommendations = item_based_recommend(
            product_idx,
            self.item_similarity_df,
            self.user_item_matrix,
            top_n
        )

        return recommendations, None

    def get_user_based_recommendations(self, user_idx, top_n=5):
        """사용자 기반 협업 필터링 추천"""
        if user_idx not in self.user_similarity_df.index:
            return None, f"사용자 ID '{user_idx}'를 찾을 수 없습니다."

        recommendations = user_based_recommend(
            user_idx,
            self.user_similarity_df,
            self.user_item_matrix,
            top_n
        )

        return recommendations, None