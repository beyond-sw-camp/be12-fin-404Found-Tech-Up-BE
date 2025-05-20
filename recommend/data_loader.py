# data_loader.py
import os
import pandas as pd
import numpy as np

def load_product_data():
    """
    제품 데이터를 로드하는 함수
    """
    # 데이터 로드
    gpu_df = pd.read_csv("danawa_gpu_products.csv")
    ssd_df = pd.read_csv("danawa_ssd_products.csv")
    ram_df = pd.read_csv("danawa_ram_products.csv")
    hdd_df = pd.read_csv("danawa_hdd_products.csv")
    cpu_df = pd.read_csv("danawa_cpu_products.csv")

    # 각 데이터프레임에 카테고리 열 추가
    gpu_df["category"] = "GPU"
    ssd_df["category"] = "SSD"
    ram_df["category"] = "RAM"
    hdd_df["category"] = "HDD"
    cpu_df["category"] = "CPU"

    # 데이터프레임 사전 생성
    data_dict = {
        "GPU": gpu_df,
        "SSD": ssd_df,
        "RAM": ram_df,
        "HDD": hdd_df,
        "CPU": cpu_df
    }

    # 각 데이터프레임 전처리
    for category, df in data_dict.items():
        # 결측값이 50% 이상인 열 삭제
        threshold = len(df) * 0.5
        df.dropna(thresh=threshold, axis=1, inplace=True)

        # 나머지 결측값을 최빈값으로 대체
        for col in df.columns:
            if df[col].isna().sum() > 0:
                if df[col].dtype == np.number:
                    # 수치형 데이터는 최빈값으로 대체
                    mode_value = df[col].mode()[0]
                    df[col].fillna(mode_value, inplace=True)
                else:
                    # 문자열 데이터는 최빈값으로 대체
                    mode_value = df[col].mode()[0]
                    df[col].fillna(mode_value, inplace=True)

    return data_dict


def load_rating_data():
    """
    평점 데이터를 로드하는 함수
    """
    # 평점 데이터 로드
    if os.path.exists("rating.csv"):
        rating_df = pd.read_csv("rating.csv")
        return rating_df
    else:
        # 파일이 없는 경우 예외 처리
        print("rating.csv 파일을 찾을 수 없습니다. 샘플 데이터를 사용합니다.")
        sample_df = pd.read_csv("rating_sample.csv")
        return sample_df


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


def get_product_category(product_name, data_dict):
    """
    제품명으로 카테고리를 찾는 함수
    """
    for category, df in data_dict.items():
        if product_name in df["Name"].values:
            return category
    return None