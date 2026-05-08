plugins {
    id("buildlogic.java-common-conventions")
    id("java-library")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    api("org.springframework:spring-web")
    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonAnnotations)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jakarta.persistence.api)
    api(libs.spring.data.jpa)
    api("org.hibernate.orm:hibernate-core")
    implementation(libs.spring.cloud.starter.openfeign)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
}
