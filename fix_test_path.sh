mv ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilMockTest.java ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java
mv ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/TwoLevelCacheMockTest.java ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/TwoLevelCacheTest.java
mv ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisRateLimiterMockTest.java ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisRateLimiterTest.java
sed -i 's/RedisUtilMockTest/RedisUtilTest/g' ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java
sed -i 's/TwoLevelCacheMockTest/TwoLevelCacheTest/g' ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/TwoLevelCacheTest.java
sed -i 's/RedisRateLimiterMockTest/RedisRateLimiterTest/g' ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisRateLimiterTest.java
