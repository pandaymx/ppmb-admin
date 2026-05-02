cat ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java | awk '
/^    LogicalExpirationWrapper<Object> invalidWrapper = new LogicalExpirationWrapper<>\(\);/ {
    print "    LogicalExpirationWrapper<Object> invalidWrapper = new LogicalExpirationWrapper<>();"
    print "    invalidWrapper.setData(\"invalid\");"
    print "    invalidWrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(60));"
    print ""
    print "    redisTemplate.opsForValue().set(key, invalidWrapper);"
    print "    Integer retrievedCastEx = redisUtil.getWithLogicalExpire(key, Integer.class, 1L, Duration.ofSeconds(2), (id) -> 5);"
    print "    Assertions.assertNull(retrievedCastEx);"
    print "  }"
    print "}"
    in_method = 1
    next
}
in_method {
    if ($0 == "}") {
        in_method = 0
    }
    next
}
{ print }
' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java.tmp && mv ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java.tmp ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java
