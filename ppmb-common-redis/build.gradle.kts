plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("testcontainers-bom").get()))

    implementation(project(":ppmb-common-api"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation(libs.findLibrary("spring-boot-starter-data-redis").get())
    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jacksonCore").get())
    implementation(libs.findLibrary("jacksonAnnotations").get())
    implementation(libs.findLibrary("jacksonDatatypeJsr310").get())
    implementation(libs.findLibrary("caffeine").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testImplementation(libs.findLibrary("testcontainers").get())
    testImplementation(libs.findLibrary("testcontainers-junit-jupiter").get())
    testImplementation(libs.findLibrary("spring-boot-testcontainers").get())
    testImplementation("org.mockito:mockito-core:5.11.0")

    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
}
