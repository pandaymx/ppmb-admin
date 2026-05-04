plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation("org.springframework:spring-web")
    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonAnnotations)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.cloud.starter.openfeign)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
}
