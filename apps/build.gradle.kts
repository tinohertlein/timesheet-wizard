plugins {
    id("com.github.jmongard.git-semver-plugin") version "0.12.6"
}

val ver = semver.version
allprojects {
    version = ver
}


repositories {
    mavenCentral()
}
