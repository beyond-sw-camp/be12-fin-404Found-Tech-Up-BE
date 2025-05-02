# main.py
from fastapi import FastAPI, HTTPException
import uvicorn
from recommender import Recommender
from models import (
    ProductRequest,
    ItemBasedRequest,
    UserBasedRequest,
    ProductResponse,
    CollaborativeResponse
)

# FastAPI 앱 생성
app = FastAPI(
    title="제품 추천 API",
    description="콘텐츠 기반 및 협업 필터링을 사용한 제품 추천 시스템",
    version="1.0.0"
)

# 추천 시스템 초기화
recommender = Recommender()


@app.get("/")
async def root():
    """API 루트 엔드포인트"""
    return {
        "message": "제품 추천 API에 오신 것을 환영합니다.",
        "documentation": "/docs에서 API 문서를 확인하세요."
    }


@app.post("/recommend", response_model=ProductResponse)
async def recommend_products(request: ProductRequest):
    """콘텐츠 기반 추천 엔드포인트"""
    product_name = request.product_name
    result_num = request.result_num

    recommendations, error = recommender.get_content_based_recommendations(product_name, result_num)

    if error:
        raise HTTPException(status_code=404, detail=error)

    return ProductResponse(similar_products=recommendations)


@app.post("/recommend/item-based", response_model=CollaborativeResponse)
async def recommend_item_based(request: ItemBasedRequest):
    """아이템 기반 협업 필터링 추천 엔드포인트"""
    product_idx = request.product_idx
    result_num = request.result_num

    recommendations, error = recommender.get_item_based_recommendations(product_idx, result_num)

    if error:
        raise HTTPException(status_code=404, detail=error)

    return CollaborativeResponse(recommended_products=recommendations)


@app.post("/recommend/user-based", response_model=CollaborativeResponse)
async def recommend_user_based(request: UserBasedRequest):
    """사용자 기반 협업 필터링 추천 엔드포인트"""
    user_idx = request.user_idx
    result_num = request.result_num

    recommendations, error = recommender.get_user_based_recommendations(user_idx, result_num)

    if error:
        raise HTTPException(status_code=404, detail=error)

    return CollaborativeResponse(recommended_products=recommendations)


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)