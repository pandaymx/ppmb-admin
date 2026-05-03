-- Sliding Window or Token Bucket could be used. Here is a simple fixed window limit.
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local current = tonumber(redis.call('get', key) or "0")

if current + 1 > limit then
    return 0
else
    redis.call("INCRBY", key, "1")
    -- ARGV[2] is expire time in seconds
    if current == 0 then
        redis.call("EXPIRE", key, ARGV[2])
    end
    return 1
end
