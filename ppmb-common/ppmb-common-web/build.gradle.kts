plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(libs.commons.text)
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-security"))
    implementation(libs.spring.boot.starter.web)
    implementation("org.aspectj:aspectjweaver:1.9.22")
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonAnnotations)
    implementation(libs.spotless.plugin.gradle)
    implementation(libs.graalvm.native.plugin)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.feign.jackson)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.validation)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
