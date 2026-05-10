plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-security"))

    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.retry)

    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonAnnotations)

    implementation(libs.jackson3.databind)
    implementation(libs.jackson3.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
}
