import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "dev.piotrulla"
version = "1.0.0"
val mainPackage = "$group.lifestealcore.addon"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://repo.stellardrift.ca/repository/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.reposilite.com/snapshots")
    maven("https://repo.eternalcode.pl/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
    implementation("net.kyori:adventure-text-minimessage:4.18.0-SNAPSHOT")

    implementation("com.eternalcode:eternalcode-commons-adventure:1.1.3")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")

    val okaeriConfigsVersion = "5.0.5"
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:${okaeriConfigsVersion}")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${okaeriConfigsVersion}")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:${okaeriConfigsVersion}")

    implementation("com.eternalcode:multification-bukkit:1.1.1")
    implementation("com.eternalcode:multification-okaeri:1.1.1")

    compileOnly("com.github.N0RSKA:LifestealCoreAPI:2024.01-a")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkit {
    main = "$mainPackage.LifeStealCoreAddonPlugin"
    apiVersion = "1.13"
    prefix = "LifeStealCoreAddonPlugin"
    author = "Piotrulla"
    name = "LifeStealCoreAddonPlugin"
    version = "${project.version}"
    description = "LifeStealCore addon plugin"
    depend = listOf("LifestealCore")

    commands {
        register("stealdev") {
            description = "StealDev main command"
        }
    }

    permissions {
        register("lifestealcoreaddon.reload") {
            description = "Pozwala na używanie komendy /stealdev reload"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("lifestealcoreaddon.give") {
            description = "Pozwala na używanie komendy /stealdev give <nick> <ilość>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation")
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("LifeStealCoreAddonPlugin ${project.version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "org/checkerframework/**",
        "META-INF/**",
        "javax/**",
        "panda"
    )

    minimize()

    val prefix = "$mainPackage.libs"

    listOf(
        "com.eternalcode",
        "com.github",
        "org.yaml",
        "eu.okaeri",
        "net.kyori"

    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}

tasks {
    runServer {
        minecraftVersion("1.20.4")

        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}