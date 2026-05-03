plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.java-database-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common-web"))
    implementation(project(":ppmb-common-api"))
    implementation(project(":ppmb-common-security"))

    implementation(libs.spring.boot.starter.web)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)
    compileOnly(libs.lombok)

    implementation(libs.mapstruct.core)
    annotationProcessor(libs.lombok.mapstruct.binding)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jacksonDatabind)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}
