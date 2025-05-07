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
from contextlib import asynccontextmanager
import asyncio
import logging
import kafka
from kafka import consume_kafka_messages, check_topic

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 추천 시스템 인스턴스 (전역 변수로 사용)
recommender = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    FastAPI 애플리케이션의 lifespan 이벤트 핸들러.
    시작 시 Recommender 초기화 및 Kafka Consumer를 실행하고, 종료 시 정리.
    """
    global recommender

    logger.info("Starting FastAPI application")

    # 1. 추천 시스템 초기화
    recommender = Recommender()
    logger.info("Recommender system initialized")

    # 2. Kafka 모듈에 recommender 인스턴스 전달
    kafka.set_recommender(recommender)
    logger.info("Passed recommender instance to Kafka module")

    # 3. Kafka 토픽 확인
    await kafka.check_topic()

    # 4. Kafka 소비자 시작
    logger.info("Starting Kafka consumer task")
    consumer_task = asyncio.create_task(kafka.consume_kafka_messages())

    yield

    # 애플리케이션 종료 시 Kafka 소비자 종료
    logger.info("Stopping Kafka consumer task")
    consumer_task.cancel()
    try:
        await consumer_task
    except asyncio.CancelledError:
        logger.info("Kafka consumer task cancelled")


# FastAPI 앱 생성
app = FastAPI(
    title="제품 추천 API",
    description="콘텐츠 기반 및 협업 필터링을 사용한 제품 추천 시스템",
    version="1.0.0",
    lifespan=lifespan
)

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
    product_idx = request.product_idx
    result_num = request.result_num

    recommendations, error = recommender.get_content_based_recommendations(product_idx, result_num)

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

@app.get("/reinitialize")
async def reinitialize_system():
    """시스템 수동 재초기화 엔드포인트 (디버깅용)"""
    global recommender
    try:
        logger.info("Manually reinitializing recommender system...")
        recommender.reinitialize_collaborative_filtering()
        return {"message": "협업 필터링 시스템이 성공적으로 재초기화되었습니다."}
    except Exception as e:
        logger.error(f"재초기화 실패: {e}")
        raise HTTPException(status_code=500, detail=f"재초기화 실패: {str(e)}")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)