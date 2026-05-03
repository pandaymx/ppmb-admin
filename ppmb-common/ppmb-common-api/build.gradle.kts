plugins {
    id("buildlogic.java-common-conventions")
}
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("spring-cloud-dependencies").get()))
    implementation("org.springframework:spring-web")
    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jacksonCore").get())
    implementation(libs.findLibrary("jacksonAnnotations").get())
    implementation(libs.findLibrary("spring-boot-starter-validation").get())
    implementation(libs.findLibrary("jakarta-persistence-api").get())
    implementation(libs.findLibrary("spring-data-jpa").get())
    implementation(libs.findLibrary("spring-cloud-starter-openfeign").get())
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
}
