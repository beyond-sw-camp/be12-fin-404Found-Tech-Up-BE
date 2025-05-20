# 기존 ver
#FROM python:3.10-slim

# spark 적용 ver
FROM python:3.10-bullseye

# Java 설치
RUN apt-get update && apt-get install -y openjdk-11-jdk && apt-get install -y procps

# JAVA_HOME 설정
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

WORKDIR /recommend

# 필요한 파일 복사
COPY *.py /recommend/
COPY *.csv /recommend/
COPY requirements.txt /recommend/


# 의존성 설치
RUN pip install --no-cache-dir -r requirements.txt

# 8000번 포트 노출
EXPOSE 8000

# 서버 시작
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]