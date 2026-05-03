plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.java-database-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common-web"))
    implementation(project(":ppmb-common-api"))
    implementation(project(":ppmb-common-doc"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.mapstruct.core)

    compileOnly(libs.lombok)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok.mapstruct.binding)
    annotationProcessor(libs.mapstruct.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jacksonDatabind)

    testCompileOnly(libs.lombok)

    testAnnotationProcessor(libs.lombok)
}
