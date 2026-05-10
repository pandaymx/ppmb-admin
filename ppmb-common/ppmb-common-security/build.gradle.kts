plugins {
    id("buildlogic.java-common-conventions")
    id("java-library")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    api(project(":ppmb-common:ppmb-common-api"))

    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.web)
    api("org.aspectj:aspectjweaver:1.9.21.1")
    api("org.springframework:spring-aop")

    implementation(libs.jacksonDatabind)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}
