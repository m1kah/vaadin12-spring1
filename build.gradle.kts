
plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    // Vaadin
    implementation("com.vaadin:vaadin-bom:12.0.0")
    implementation("com.vaadin:vaadin-core:12.0.0")
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:1.5.18.RELEASE")
    // Immutables, just for examples
    annotationProcessor("org.immutables:value:2.7.3")
    compileOnly("org.immutables:value:2.7.3:annotations")
}
