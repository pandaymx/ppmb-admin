plugins {
    id("buildlogic.java-common-conventions")
    id("java-library")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    api(project(":ppmb-common:ppmb-common-api"))

    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.web)

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
