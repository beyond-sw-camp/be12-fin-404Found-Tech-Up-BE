from scipy.sparse import csr_matrix
from sklearn.decomposition import TruncatedSVD
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk
from datetime import datetime
import time
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("elasticsearch")

def update_es(user_item_matrix):
    user_idx_list = user_item_matrix.index
    item_idx_list = user_item_matrix.columns

    user_matrix_sparse = csr_matrix(user_item_matrix.values)
    item_matrix_sparse = csr_matrix(user_item_matrix.T.values)

    n_components = 128  # 차원 수 (하이퍼파라미터, 튜닝 가능)
    user_svd = TruncatedSVD(n_components=n_components, random_state=42)
    item_svd = TruncatedSVD(n_components=n_components, random_state=42)
    User_svd = user_svd.fit_transform(user_matrix_sparse)
    Item_svd = item_svd.fit_transform(item_matrix_sparse)

    # 엘라스틱서치 연결
    es = Elasticsearch(
        # ["http://192.0.40.207:9200"],  # 실제 엘라스틱서치 주소로 변경
        ["http://elasticsearch-client:9200"],
        # 인증이 필요한 경우 아래 주석을 해제하고 정보 입력
        basic_auth=("elastic", "changeme"),
        request_timeout=300,  # 타임아웃 30초
        retry_on_timeout=True,  # 타임아웃 시 재시도
        max_retries=3
        # ca_certs="/path/to/ca.crt",  # TLS/SSL 인증서가 필요한 경우
    )

    # 인덱스 생성
    create_vector_indices(es, n_components)

    # 벌크 API로 데이터 적재
    bulk_store_user_vectors(es, user_idx_list, User_svd)
    bulk_store_item_vectors(es, item_idx_list, Item_svd)

    print("Vector update completed")


def create_vector_indices(es, n_components):
    # 벡터 인덱스 매핑 설정
    vector_mapping = {
        "properties": {
            "vector": {
                "type": "dense_vector",
                "dims": n_components,
                "index": True,
                "similarity": "cosine",
                "index_options": {
                    "type": "hnsw",  # HNSW 알고리즘 사용
                    "m": 16,  # 최대 연결 수 (기본값)
                    "ef_construction": 200  # 인덱스 생성 시 효율성 파라미터
                }
            },
            "last_updated": {
                "type": "date"
            }
        }
    }

    # 인덱스 설정 (KNN 관련 설정 제거)
    vector_settings = {
        "index": {
            "number_of_shards": 3,
            "number_of_replicas": 1
        }
    }

    # 인덱스 목록
    indices = ["user-based", "item-based"]

    for index_name in indices:
        # 인덱스가 존재하면 삭제 후 다시 생성
        if es.indices.exists(index=index_name):
            print(f"Deleting existing index: {index_name}")
            es.indices.delete(index=index_name)

        # 인덱스 생성
        print(f"Creating index: {index_name}")
        es.indices.create(
            index=index_name,
            mappings=vector_mapping,
            settings=vector_settings
        )
        print(f"Finished Creating index: {index_name}")


def bulk_store_user_vectors(es, user_idx_list, User_svd):
    start_time = time.time()

    def generate_user_actions():
        for i, user_id in enumerate(user_idx_list):
            yield {
                "_index": "user-based",
                "_id": str(user_id),
                "_source": {
                    "vector": User_svd[i].tolist(),
                    "last_updated": datetime.now().isoformat()
                }
            }

    # 벌크 업로드 실행
    success, failed = bulk(es, generate_user_actions(), chunk_size=1000, request_timeout=120)

    end_time = time.time()
    print(f"Bulk indexed {success} user vectors in {end_time - start_time:.2f} seconds")
    if failed:
        print(f"Failed to index {len(failed)} documents")


def bulk_store_item_vectors(es, item_idx_list, Item_svd):
    start_time = time.time()

    def generate_item_actions():
        for i, item_id in enumerate(item_idx_list):
            yield {
                "_index": "item-based",
                "_id": str(item_id),
                "_source": {
                    "vector": Item_svd[i].tolist(),
                    "last_updated": datetime.now().isoformat()
                }
            }

    # 벌크 업로드 실행
    success, failed = bulk(es, generate_item_actions(), chunk_size=1000, request_timeout=120)

    end_time = time.time()
    print(f"Bulk indexed {success} item vectors in {end_time - start_time:.2f} seconds")
    if failed:
        print(f"Failed to index {len(failed)} documents")