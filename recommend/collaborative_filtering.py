# collaborative_filtering.py
import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import logging
# 로깅 설정
logger = logging.getLogger(__name__)

def calculate_item_similarity(user_item_matrix, product_idx=1):
    """
    아이템 기반 협업 필터링 유사도 계산
    """
    # 입력 아이템과 다른 모든 아이템 간
    item_similarity = cosine_similarity(user_item_matrix.T.loc[product_idx].values.reshape(1, -1), user_item_matrix.T)
    item_similarity_df = pd.DataFrame(
        item_similarity.T,
        index=user_item_matrix.T.index,
        columns=[product_idx]
    )
    # 모든 아이템 간 (전치행렬 사용)
    # item_similarity = cosine_similarity(user_item_matrix.T)
    # item_similarity_df = pd.DataFrame(
    #     item_similarity,
    #     index=user_item_matrix.columns,
    #     columns=user_item_matrix.columns
    # )
    return item_similarity_df


def calculate_user_similarity(user_item_matrix, user_idx=1):
    """
    사용자 기반 협업 필터링 유사도 계산
    """
    # 입력 사용자와 다른 모든 사용자 간
    user_similarity = cosine_similarity(user_item_matrix.loc[user_idx].values.reshape(1, -1), user_item_matrix)
    user_similarity_df = pd.DataFrame(
        user_similarity.T,
        index=user_item_matrix.index,
        columns=[user_idx]
    )
    # 모든 사용자 간
    # user_similarity = cosine_similarity(user_item_matrix)
    # user_similarity_df = pd.DataFrame(
    #     user_similarity,
    #     index=user_item_matrix.index,
    #     columns=user_item_matrix.index
    # )
    return user_similarity_df


def item_based_recommend(item_id, item_similarity_df, user_item_matrix, top_n=5):
    """
    아이템 기반 협업 필터링 추천 함수
    """
    # 아이템 ID가 존재하는지 확인
    if item_id not in item_similarity_df.index:
        return []

    # 해당 아이템과 다른 아이템 간의 유사도
    item_similarities = item_similarity_df[item_id].copy()
    # item_similarities = item_similarity_df.loc[item_id]

    # 자기 자신 제외하고 유사도 높은 상위 N개 아이템 추천
    similar_items = (
        item_similarities
        .drop(item_id)  # 자기 자신 제외
        .sort_values(ascending=False)  # 유사도 내림차순 정렬
        .head(top_n)  # 상위 N개 선택
    )

    # 추천 아이템 목록 생성
    recommendations = []
    for item, similarity in similar_items.items():
        recommendations.append({
            "product_idx": int(item),
            "similarity": float(similarity)
        })

    return recommendations


def user_based_recommend(user_id, user_similarity_df, user_item_matrix, top_n=5):
    """
    사용자 기반 협업 필터링 추천 함수
    """
    # 사용자 ID가 존재하는지 확인
    if user_id not in user_similarity_df.index:
        return []

    # 해당 사용자와 다른 사용자 간의 유사도
    user_similarities = user_similarity_df[user_id].copy()
    # user_similarities = user_similarity_df.loc[user_id]

    # 자기 자신 제외하고 유사도 높은 상위 사용자들 선택
    similar_users = (
        user_similarities
        .drop(user_id)  # 자기 자신 제외
        .sort_values(ascending=False)  # 유사도 내림차순 정렬
    )

    # 해당 사용자가 평가한 아이템 목록 (평점이 있는 아이템)
    user_rated_items = user_item_matrix.loc[user_id]
    user_rated_items = user_rated_items[user_rated_items > 0].index.tolist()

    # 추천할 아이템 목록
    recommendations = []

    # 각 유사 사용자별로 처리
    for similar_user, similarity in similar_users.items():
        # 이미 충분한 추천 항목을 찾았으면 중단
        if len(recommendations) >= top_n:
            break

        # 유사 사용자의 평점 정보
        similar_user_ratings = user_item_matrix.loc[similar_user]

        # 평점이 있는 아이템 중에서 대상 사용자가 평가하지 않은 아이템만 선택
        potential_items = similar_user_ratings[~similar_user_ratings.index.isin(user_rated_items)]
        potential_items = potential_items[potential_items > 0]  # 평점이 있는 항목만

        # 평점이 가장 높은 아이템 선택
        if not potential_items.empty:
            best_item = potential_items.idxmax()  # 가장 높은 평점의 아이템
            best_rating = potential_items[best_item]

            # 이미 추천 목록에 있는지 확인
            item_already_recommended = any(rec["product_idx"] == best_item for rec in recommendations)

            # 추천 목록에 없으면 추가
            if not item_already_recommended:
                recommendations.append({
                    "product_idx": int(best_item),
                    "rating": float(best_rating),
                    "recommended_by": int(similar_user),
                    "user_similarity": float(similarity)
                })

    # top_n개까지만 반환
    return recommendations[:top_n]