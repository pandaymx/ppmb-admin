plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.testcontainers.bom))

    implementation(project(":ppmb-common:ppmb-common-api"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.jacksonDatabind)
    implementation(libs.jackson3.databind)
    implementation(libs.jackson3.core)

    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.caffeine)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.mockito.core)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}
