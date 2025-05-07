import asyncio
import logging
from aiokafka import AIOKafkaConsumer
from aiokafka.admin import AIOKafkaAdminClient

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Kafka 설정
KAFKA_BOOTSTRAP_SERVERS = "kafka-0.kafka-headless.default.svc.cluster.local:9092,kafka-1.kafka-headless.default.svc.cluster.local:9092"
# KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
KAFKA_TOPIC = "review-created-notifications"
KAFKA_GROUP_ID = "review-notification-group"

# 전역 변수로 recommender 참조 저장
_recommender_instance = None

def set_recommender(recommender):
    """
    전역 recommender 참조를 설정하는 함수
    """
    global _recommender_instance
    _recommender_instance = recommender
    logger.info("Recommender instance set in Kafka module")

async def consume_kafka_messages(recommender=None):
    """
    Kafka Consumer를 초기화하고 메시지를 수신하는 비동기 함수.
    연결 실패 시 재시도하며, 메시지를 로깅.
    """

    global _recommender_instance

    while True:
        try:
            consumer = AIOKafkaConsumer(
                KAFKA_TOPIC,
                bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
                group_id=KAFKA_GROUP_ID,
                auto_offset_reset="latest",
                enable_auto_commit=True
            )
            await consumer.start()
            logger.info(f"Kafka consumer started, joined group: {KAFKA_GROUP_ID}")
            try:
                async for msg in consumer:
                    message = msg.value.decode("utf-8")
                    logger.info(
                        f"Received notification: {message}, "
                        f"Partition: {msg.partition}, Offset: {msg.offset}"
                    )

                    # 추가 처리 로직 (예: DB 저장, 푸시 알림 전송)
                    # 협업 필터링 데이터 초기화
                    if _recommender_instance:
                        logger.info("Received Kafka message, reinitializing collaborative filtering data...")
                        try:
                            # CPU-bound 작업이므로 별도 스레드에서 실행
                            loop = asyncio.get_running_loop()
                            await loop.run_in_executor(
                                None,
                                _recommender_instance.reinitialize_collaborative_filtering
                            )
                            logger.info("Collaborative filtering data reinitialized successfully!")
                        except Exception as e:
                            logger.error(f"Failed to reinitialize collaborative filtering: {e}")
                    else:
                        logger.warning(
                            "No recommender instance available. Skip reinitializing collaborative filtering.")

            except Exception as e:
                logger.error(f"Error processing message: {e}")
        except Exception as e:
            logger.error(f"Consumer error: {e}, retrying in 5 seconds")
            await asyncio.sleep(5)
        finally:
            logger.info("Stopping Kafka consumer")
            await consumer.stop()

async def check_topic():
    """
    Kafka 토픽 존재 여부를 확인하는 비동기 함수.
    """
    try:
        admin = AIOKafkaAdminClient(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)
        await admin.start()
        topics = await admin.list_topics()
        logger.info(f"Available topics: {topics}")
        if KAFKA_TOPIC not in topics:
            logger.error(f"Topic {KAFKA_TOPIC} does not exist")
        else:
            logger.info(f"Topic {KAFKA_TOPIC} exists")
        await admin.close()
    except Exception as e:
        logger.error(f"Error checking topic: {e}")