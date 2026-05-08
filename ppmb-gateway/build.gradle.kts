plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common:ppmb-common-web"))
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-security"))
    implementation(project(":ppmb-common:ppmb-common-redis"))
    implementation(libs.jjwt.api)

    implementation(libs.spring.cloud.starter.gateway.server.webmvc)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.jjwt.impl)
    testImplementation(libs.jjwt.jackson)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
