FROM python:3.10-slim

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