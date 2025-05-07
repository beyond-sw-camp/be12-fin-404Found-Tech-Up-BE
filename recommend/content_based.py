# content_based.py
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler, OneHotEncoder
from sklearn.metrics.pairwise import cosine_similarity

def feature_engineering(data_dict):
    """
    특성 엔지니어링 수행 함수
    """
    processed_data = {}

    for category, df in data_dict.items():
        # Name과 url 컬럼 분리 (나중에 참조용)
        product_info = df[["name", "product_idx", category.lower() + "_idx"]].copy()

        # 전처리할 데이터 선택 (Name, url 제외)
        features_df = df.drop(["name", "product_idx", category.lower() + "_idx"], axis=1)

        # 수치형 변수와 범주형 변수 분리
        numeric_features = []
        categorical_features = []

        for col in features_df.columns:
            if features_df[col].dtype == np.number or features_df[col].dtype == 'float64' or features_df[
                col].dtype == 'int64':
                # 숫자로 변환 가능한 문자열 확인
                try:
                    features_df[col] = pd.to_numeric(
                        features_df[col].astype(str).str.replace(',', '').str.replace('MHz', '').str.replace('W',
                                                                                                             '').str.replace(
                            'mm', '').str.replace('g', ''))
                    numeric_features.append(col)
                except:
                    categorical_features.append(col)
            else:
                categorical_features.append(col)

        # 데이터프레임 분리
        numeric_df = features_df[numeric_features].copy()
        categorical_df = features_df[categorical_features].copy()

        # 수치형 변수 스케일링 (MinMaxScaler)
        if not numeric_df.empty:
            scaler = MinMaxScaler()
            numeric_scaled = scaler.fit_transform(numeric_df)
            numeric_scaled_df = pd.DataFrame(numeric_scaled, columns=numeric_features, index=numeric_df.index)
        else:
            numeric_scaled_df = pd.DataFrame(index=features_df.index)

        # 범주형 변수 원핫인코딩
        encoded_dfs = []
        if not categorical_df.empty:
            for col in categorical_features:
                # '○'와 같은 특수 문자를 'Yes'로 변환하여 원핫인코딩 준비
                categorical_df[col] = categorical_df[col].apply(lambda x: 'Yes' if x == '○' else x)

                # 고유값 확인
                unique_values = categorical_df[col].dropna().unique()

                # 원핫인코딩
                encoder = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
                encoded = encoder.fit_transform(categorical_df[[col]])

                # 인코딩된 데이터프레임 생성
                encoded_col_names = [f"{col}_{val}" for val in encoder.get_feature_names_out([col])]
                encoded_df = pd.DataFrame(encoded, columns=encoded_col_names, index=categorical_df.index)

                encoded_dfs.append(encoded_df)

        # 모든 인코딩된 데이터프레임 병합
        if encoded_dfs:
            encoded_full_df = pd.concat(encoded_dfs, axis=1)
        else:
            encoded_full_df = pd.DataFrame(index=features_df.index)

        # 수치형과 범주형 병합
        processed_df = pd.concat([numeric_scaled_df, encoded_full_df], axis=1)

        # 최종 처리된 데이터와 원본 제품 정보 저장
        processed_data[category] = {
            "features": processed_df,
            "product_info": product_info
        }

    return processed_data


def calculate_content_similarity(processed_data):
    """
    콘텐츠 기반 유사도 계산 함수
    """
    similarity_dict = {}

    for category, data in processed_data.items():
        features = data["features"]

        # NaN 값을 0으로 대체
        features = features.fillna(0)

        # 코사인 유사도 계산
        similarity_matrix = cosine_similarity(features)

        # 유사도 행렬을 데이터프레임으로 변환
        similarity_df = pd.DataFrame(
            similarity_matrix,
            index=data["product_info"]["product_idx"],
            columns=data["product_info"]["product_idx"]
        )

        similarity_dict[category] = similarity_df

    return similarity_dict


def recommend_content_based(product_idx, category, similarity_df, data_dict, top_n=3):
    """
    콘텐츠 기반 추천 수행 함수
    """
    # 입력된 제품과 다른 제품과의 유사도
    product_similarities = similarity_df.loc[product_idx]

    # 자기 자신을 제외하고 유사도가 높은 top_n개 제품 선택
    similar_products = (
        product_similarities
        .drop(product_idx)  # 자기 자신 제외
        .sort_values(ascending=False)  # 유사도 내림차순 정렬
        .head(top_n)  # 상위 n개 선택
    )

    # 결과 생성
    result = []
    for idx, similarity in similar_products.items():
        # 해당 제품 정보 찾기
        product_info = data_dict[category].loc[data_dict[category]["product_idx"] == idx].iloc[0]

        result.append({
            "product_idx": idx,
            "similarity": float(similarity),
            "price": product_info.get("Price", "정보 없음"),
            "url": product_info.get("url", "정보 없음"),
            "category": category
        })

    return result