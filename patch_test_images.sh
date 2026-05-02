for file in ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/*Test.java; do
    awk '
    /public static GenericContainer<\?> redisContainer = new GenericContainer<>\(DockerImageName.parse/ {
        print "    @Container"
        print "    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse(\"redis:7-alpine\"))"
        print "            .withExposedPorts(6379);"
        skip = 1
        next
    }
    /            \.withExposedPorts\(6379\);/ {
        if (skip) { skip = 0; next }
    }
    { print }
    ' "$file" > "$file.tmp" && mv "$file.tmp" "$file"
done
