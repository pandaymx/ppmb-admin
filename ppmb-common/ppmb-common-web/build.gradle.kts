plugins {
    id("buildlogic.java-common-conventions")
}
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("spring-cloud-dependencies").get()))
    implementation(libs.findLibrary("commons-text").get())
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(libs.findLibrary("spring-boot-starter-web").get())
    implementation(libs.findLibrary("spring-boot-starter-security").get())
    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jacksonCore").get())
    implementation(libs.findLibrary("jacksonAnnotations").get())
    implementation(libs.findLibrary("spotless-plugin-gradle").get())
    implementation(libs.findLibrary("graalvm-native-plugin").get())
    implementation(libs.findLibrary("spring-boot-starter-amqp").get())
    implementation(libs.findLibrary("spring-cloud-starter-openfeign").get())
    implementation(libs.findLibrary("jacksonDatatypeJsr310").get())
    implementation(libs.findLibrary("feign-jackson").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testImplementation(libs.findLibrary("spring-boot-starter-validation").get())
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}
