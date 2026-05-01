plugins {
    // 只声明插件，不在这里 apply
    alias(libs.plugins.spring.boot) apply false
}

// 提取公共属性，但不使用 subprojects 注入
allprojects {
    group = "top.ppmblszdp" // 建议换成你自己的域名
    version = "0.1.0"

    repositories {
        mavenCentral()
        // 建议加上阿里云镜像加速
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}