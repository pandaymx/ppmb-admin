plugins {
    id("buildlogic.spring-cloud-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common-web"))

    implementation(libs.spring.cloud.starter.gateway.server.webmvc)

    developmentOnly(libs.spring.boot.devtools)
}
