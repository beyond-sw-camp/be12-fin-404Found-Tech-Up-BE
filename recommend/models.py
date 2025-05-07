# models.py
from pydantic import BaseModel
from typing import List, Dict, Any

# 요청 모델 - 콘텐츠 기반 필터링
class ProductRequest(BaseModel):
    product_idx: int
    result_num: int

# 요청 모델 - 아이템 기반 협업 필터링
class ItemBasedRequest(BaseModel):
    product_idx: int
    result_num: int

# 요청 모델 - 사용자 기반 협업 필터링
class UserBasedRequest(BaseModel):
    user_idx: int
    result_num: int

# 응답 모델 - 콘텐츠 기반 필터링
class ProductResponse(BaseModel):
    similar_products: List[Dict[str, Any]]

# 응답 모델 - 협업 필터링
class CollaborativeResponse(BaseModel):
    recommended_products: List[Dict[str, Any]]