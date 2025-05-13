-- lua/coupon_issue.lua

-- KEYS:
--   1) userSetKey   = "set.receive.couponId.{couponId}"
--   2) stockHashKey = "hash.coupon.stock.{couponId}"
--   3) listKey      = "list.received.user.{couponId}"
-- ARGV:
--   1) userIdx

-- 1) 중복 체크
local added = redis.call('SADD', KEYS[1], ARGV[1])
if added == 0 then
  -- 이미 발급된 사용자
  local remain = tonumber(redis.call('HGET', KEYS[2], 'quantity'))
  return {0, remain}
end

-- 2) 재고 차감
local remain = tonumber(redis.call('HINCRBY', KEYS[2], 'quantity', -1))
if remain < 0 then
  -- 재고 부족, 롤백
  redis.call('HINCRBY', KEYS[2], 'quantity', 1)
  redis.call('SREM', KEYS[1], ARGV[1])
  return {-1, 0}
end

-- 3) 발급 로그
redis.call('RPUSH', KEYS[3], ARGV[1])

-- 성공
return {1, remain}
