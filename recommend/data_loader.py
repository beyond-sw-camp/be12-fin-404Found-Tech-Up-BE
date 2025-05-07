# data_loader.py
import os
import pandas as pd
import numpy as np
import mysql.connector
from mysql.connector import Error


def get_db_connection():
    """
    MariaDB 데이터베이스 연결을 생성하는 함수
    """
    try:
        connection = mysql.connector.connect(
            host='mariadb',  # 데이터베이스 호스트
            # host='192.0.44.44',  # 데이터베이스 호스트
            database='techup',  # 데이터베이스 이름
            user='test',  # 데이터베이스 사용자
            # user='techup',  # 데이터베이스 사용자
            password='qwer1234',  # 데이터베이스 비밀번호
            port=3306  # MariaDB 기본 포트
        )
        if connection.is_connected():
            return connection

    except Error as e:
        print(f"MariaDB 연결 오류: {e}")
        return None


def load_product_data():
    """
    MariaDB에서 제품 데이터를 로드하는 함수
    """
    connection = get_db_connection()
    # if not connection:
    #     print("데이터베이스 연결 실패. CSV 파일로 대체합니다.")
    #     return load_product_data_from_csv()

    data_dict = {}
    categories = ["GPU", "SSD", "RAM", "HDD", "CPU"]

    try:
        cursor = connection.cursor(dictionary=True)

        for category in categories:
            # 각 카테고리별 테이블에서 데이터 로드
            query = f"select p.name, p.price, p.rating, p.rating, p.brand, c.* from product p inner join {category.lower()}_spec c on p.product_idx = c.product_idx;"

            cursor.execute(query)
            records = cursor.fetchall()

            # 결과를 데이터프레임으로 변환
            df = pd.DataFrame(records)

            # 카테고리 열 추가
            df["category"] = category

            # 결측값 처리
            if not df.empty:
                # 결측값이 50% 이상인 열 삭제
                threshold = len(df) * 0.5
                df.dropna(thresh=threshold, axis=1, inplace=True)

                # 나머지 결측값을 최빈값으로 대체
                for col in df.columns:
                    if df[col].isna().sum() > 0:
                        if pd.api.types.is_numeric_dtype(df[col]):
                            # 수치형 데이터는 최빈값으로 대체
                            mode_value = df[col].mode()[0]
                            df[col].fillna(mode_value, inplace=True)
                        else:
                            # 문자열 데이터는 최빈값으로 대체
                            mode_value = df[col].mode()[0]
                            df[col].fillna(mode_value, inplace=True)

            data_dict[category] = df

    except Error as e:
        print(f"데이터 쿼리 오류: {e}")
        # print("CSV 파일로 대체합니다.")
        # return load_product_data_from_csv()

    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

    return data_dict

# def load_product_data_from_csv():
#     """
#     제품 데이터를 로드하는 함수
#     """
#     # 데이터 로드
#     gpu_df = pd.read_csv("danawa_gpu_products.csv")
#     ssd_df = pd.read_csv("danawa_ssd_products.csv")
#     ram_df = pd.read_csv("danawa_ram_products.csv")
#     hdd_df = pd.read_csv("danawa_hdd_products.csv")
#     cpu_df = pd.read_csv("danawa_cpu_products.csv")
#
#     # 각 데이터프레임에 카테고리 열 추가
#     gpu_df["category"] = "GPU"
#     ssd_df["category"] = "SSD"
#     ram_df["category"] = "RAM"
#     hdd_df["category"] = "HDD"
#     cpu_df["category"] = "CPU"
#
#     # 데이터프레임 사전 생성
#     data_dict = {
#         "GPU": gpu_df,
#         "SSD": ssd_df,
#         "RAM": ram_df,
#         "HDD": hdd_df,
#         "CPU": cpu_df
#     }
#
#     # 각 데이터프레임 전처리
#     for category, df in data_dict.items():
#         # 결측값이 50% 이상인 열 삭제
#         threshold = len(df) * 0.5
#         df.dropna(thresh=threshold, axis=1, inplace=True)
#
#         # 나머지 결측값을 최빈값으로 대체
#         for col in df.columns:
#             if df[col].isna().sum() > 0:
#                 if df[col].dtype == np.number:
#                     # 수치형 데이터는 최빈값으로 대체
#                     mode_value = df[col].mode()[0]
#                     df[col].fillna(mode_value, inplace=True)
#                 else:
#                     # 문자열 데이터는 최빈값으로 대체
#                     mode_value = df[col].mode()[0]
#                     df[col].fillna(mode_value, inplace=True)
#
#     return data_dict


def load_rating_data():
    """
    평점 데이터를 MariaDB에서 로드하는 함수
    """
    connection = get_db_connection()
    # if not connection:
    #     print("데이터베이스 연결 실패. CSV 파일로 대체합니다.")
    #     return load_rating_data_from_csv()

    try:
        cursor = connection.cursor(dictionary=True)

        # 평점 데이터 쿼리
        query = "SELECT review_idx, user_idx, product_idx, review_rating FROM review"
        cursor.execute(query)
        records = cursor.fetchall()

        # 결과를 데이터프레임으로 변환
        rating_df = pd.DataFrame(records)

    except Error as e:
        print(f"평점 데이터 쿼리 오류: {e}")
        # print("CSV 파일로 대체합니다.")
        # return load_rating_data_from_csv()

    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

    return rating_df


# def load_rating_data_from_csv():
#     """
#     CSV 파일에서 평점 데이터를 로드하는 함수 (백업 방법)
#     """
#     # 평점 데이터 로드
#     if os.path.exists("rating.csv"):
#         rating_df = pd.read_csv("rating.csv")
#         return rating_df
#     else:
#         # 파일이 없는 경우 예외 처리
#         print("rating.csv 파일을 찾을 수 없습니다. 샘플 데이터를 사용합니다.")
#         sample_df = pd.read_csv("rating_sample.csv")
#         return sample_df


def create_user_item_matrix(rating_df):
    """
    사용자-아이템 행렬을 생성하는 함수
    """
    # 사용자-아이템 행렬 생성 (유저를 행, 아이템을 열로)
    user_item_matrix = rating_df.pivot_table(
        index='user_idx',
        columns='product_idx',
        values='review_rating',
        fill_value=0
    )
    return user_item_matrix


def get_product_category(product_idx, data_dict):
    """
    제품명으로 카테고리를 찾는 함수
    """
    for category, df in data_dict.items():
        if product_idx in df["product_idx"].values:
            return category
    return None