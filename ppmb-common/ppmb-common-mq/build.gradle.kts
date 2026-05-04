plugins {
    id("buildlogic.java-common-conventions")
}
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(project(":ppmb-common:ppmb-common-api"))

    implementation(libs.findLibrary("spring-boot-starter-amqp").get())
    implementation(libs.findLibrary("spring-retry").get())

    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jacksonCore").get())
    implementation(libs.findLibrary("jacksonAnnotations").get())

    implementation(libs.findLibrary("jackson3-databind").get())
    implementation(libs.findLibrary("jackson3-core").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
}
