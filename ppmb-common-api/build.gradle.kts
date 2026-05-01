plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation("org.springframework:spring-web")
    implementation(libs.findLibrary("spring-boot-starter-validation").get())
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}
