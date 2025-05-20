import os
import sys
import findspark

findspark.init()  # 자동으로 Spark 환경을 찾아 초기화

# Windows에서 Hadoop 환경 변수 오류 해결
if os.name == 'nt':  # Windows 환경 확인
    os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.elasticsearch:elasticsearch-spark-30_2.12:8.8.0 pyspark-shell'
    # Hadoop 환경 변수 무시 설정
    os.environ['PYSPARK_PYTHON'] = sys.executable
    os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable

from pyspark.sql import SparkSession
from pyspark.sql.functions import udf, col, lit
from pyspark.sql.types import StringType, ArrayType, FloatType, StructType, StructField
from scipy.sparse import csr_matrix
from sklearn.decomposition import TruncatedSVD
from elasticsearch import Elasticsearch
from datetime import datetime
import pandas as pd
import time
import logging
import json

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("elasticsearch")


def update_es_with_spark(user_item_matrix):
    # Spark 세션 생성 - Windows 환경에서 Hadoop 없이 로컬 모드로 실행
    spark = SparkSession.builder \
        .appName("ElasticsearchVectorLoader") \
        .config("spark.jars.packages", "org.elasticsearch:elasticsearch-spark-30_2.12:8.8.0") \
        .config("spark.driver.memory", "4g") \
        .config("spark.executor.memory", "4g") \
        .config("spark.hadoop.fs.defaultFS", "file:///") \
        .config("spark.hadoop.io.compression.codecs", "org.apache.hadoop.io.compress.SnappyCodec") \
        .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer") \
        .master("spark://spark-master:7077") \
        .getOrCreate()

    # 벡터 생성
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
        basic_auth=("elastic", "changeme"),
        request_timeout=300,
        retry_on_timeout=True,
        max_retries=3
    )

    # 인덱스 생성
    create_vector_indices(es, n_components)

    # 현재 시간
    current_time = datetime.now().isoformat()

    # Pandas DataFrame 생성 (User 벡터)
    user_df = pd.DataFrame({
        'id': user_idx_list,
        'vector': list(User_svd),
        'last_updated': current_time
    })

    # Pandas DataFrame 생성 (Item 벡터)
    item_df = pd.DataFrame({
        'id': item_idx_list,
        'vector': list(Item_svd),
        'last_updated': current_time
    })

    # Spark DataFrame으로 변환
    user_spark_df = convert_to_spark_df(spark, user_df, "user")
    item_spark_df = convert_to_spark_df(spark, item_df, "item")

    # Elasticsearch에 저장
    es_write_with_spark(user_spark_df, "user-based")
    es_write_with_spark(item_spark_df, "item-based")

    print("Vector update completed with PySpark")

    # Spark 세션 종료
    spark.stop()


def convert_to_spark_df(spark, pandas_df, entity_type):
    """Pandas DataFrame을 Spark DataFrame으로 변환"""
    # 벡터 컬럼을 문자열로 변환 (JSON 형식)
    pandas_df['vector_json'] = pandas_df['vector'].apply(lambda x: json.dumps(x.tolist()))

    # Spark DataFrame 스키마 정의
    schema = StructType([
        StructField("id", StringType(), False),
        StructField("vector_json", StringType(), False),
        StructField("last_updated", StringType(), False)
    ])

    # Spark DataFrame 생성
    spark_df = spark.createDataFrame(
        pandas_df[['id', 'vector_json', 'last_updated']],
        schema=schema
    )

    # 벡터 문자열을 배열로 변환하는 UDF 정의
    vector_to_array_udf = udf(lambda x: json.loads(x), ArrayType(FloatType()))

    # UDF 적용하여 벡터 변환
    result_df = spark_df.withColumn("vector", vector_to_array_udf(col("vector_json"))) \
        .drop("vector_json")

    return result_df


def es_write_with_spark(df, index_name):
    """Spark DataFrame을 Elasticsearch에 저장"""
    print(f"Writing {df.count()} records to Elasticsearch index '{index_name}'")

    start_time = time.time()
    # Elasticsearch에 데이터 쓰기 (Spark Elasticsearch connector 사용)
    df.write \
        .format("org.elasticsearch.spark.sql") \
        .option("es.nodes", "elasticsearch-client") \
        .option("es.port", "9200") \
        .option("es.net.http.auth.user", "elastic") \
        .option("es.net.http.auth.pass", "changeme") \
        .option("es.mapping.id", "id") \
        .option("es.write.operation", "upsert") \
        .option("es.batch.size.entries", "1000") \
        .option("es.batch.write.retry.count", "3") \
        .option("es.batch.write.retry.wait", "10") \
        .option("es.nodes.wan.only", "true") \
        .mode("append") \
        .save(index_name)

    print(f"Finished writing to Elasticsearch index '{index_name}'")

    end_time = time.time()
    logger.info(f"write_with_spark in {end_time - start_time:.2f} seconds")


def create_vector_indices(es, n_components):
    """Elasticsearch 벡터 인덱스 생성"""
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

    # 인덱스 설정
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

# 예시 사용법:
# user_item_matrix = pd.DataFrame(...) # 사용자-아이템 매트릭스
# update_es_with_spark(user_item_matrix)