plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.java-database-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common:ppmb-common-web"))
    implementation(project(":ppmb-api:ppmb-api-system"))
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-doc"))
    implementation(project(":ppmb-common:ppmb-common-security"))
    implementation(project(":ppmb-common:ppmb-common-mq"))
    implementation(libs.spring.boot.starter.amqp)
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
    testImplementation(libs.mockito.inline)

    testCompileOnly(libs.lombok)

    testAnnotationProcessor(libs.lombok)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
    mainClass.set("top.ppmblszdp.system.SystemApplication")
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("top.ppmblszdp.system.SystemApplication")
        }
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs(
        "--enable-preview",
        "-XX:+EnableDynamicAgentLoading",
        "-Djdk.instrument.traceUsage=false"
    )
    systemProperty("java.awt.headless", "true")
    useJUnitPlatform()
}
