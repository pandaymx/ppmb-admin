plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common:ppmb-common-web"))
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-security"))
    implementation(project(":ppmb-common:ppmb-common-doc"))
    implementation(project(":ppmb-api:ppmb-api-system"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.cloud.starter.openfeign)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
    mainClass.set("top.ppmblszdp.auth.AuthApplication")
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("top.ppmblszdp.auth.AuthApplication")
        }
    }
}
