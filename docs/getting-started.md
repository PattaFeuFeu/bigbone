---
title: Getting Started
layout: default
nav_order: 2
---

# Getting Started

In this guide, we want to show you how you can easily integrate BigBone into your project. Simply follow the steps below.

## Adding BigBone to Your Project

BigBone consists of two main modules:

1. `bigbone` which exposes the Mastodon API endpoints as handy methods usable via a `MastodonClient`
2. `bigbone-rx` which adds a thin RxJava3 layer around the `bigbone` module

{: .note }
If your application does not require reactive functionality, you can omit the `bigbone-rx` dependency in the following 
steps. BigBone uses RxJava for implementing the reactive part. Check out [their website](https://github.com/ReactiveX/RxJava) 
to get more information.

{: .note }
The project has been switched over from @andregasser to @pattafeufeu in 2025.
We decided to not renew the domain _bigbone.social_ for cost reasons and will thus let it run out.
For that reason, we’re already switching to a GitHub-namespace-based mode now which unfortunately leads to a change
in how you need to declare the dependency (previously `social.bigbone`, now `io.github.pattafeufeu`).

### Gradle (Groovy DSL)

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Gradle project (using Groovy DSL):

Repository:

```groovy
repositories {
    maven {
        url "https://central.sonatype.com/repository/maven-snapshots/"
    }
}
```

Dependencies:

```groovy
dependencies {
    implementation "io.github.pattafeufeu:bigbone:2.0.0-SNAPSHOT"
    // Optional, if you want to use the BigBone RxJava3 wrappers
    implementation "io.github.pattafeufeu:bigbone-rx:2.0.0-SNAPSHOT"
}
```

### Gradle (Kotlin DSL)

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Gradle project (using Kotlin DSL):

Repository:

```groovy
repositories {
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
```

Dependencies:

```groovy
dependencies {
    implementation("io.github.pattafeufeu:bigbone:2.0.0-SNAPSHOT")
    // Optional, if you want to use the BigBone RxJava3 wrappers
    implementation("io.github.pattafeufeu:bigbone-rx:2.0.0-SNAPSHOT")
}
```

### Maven

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Maven project:

Repository:

```xml
<repositories>
    <repository>
        <id>maven-central-snapshots</id>
        <name>Maven Central Snapshot Repository</name>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

Dependencies:

```xml
<dependency>
    <groupId>io.github.pattafeufeu</groupId>
    <artifactId>bigbone</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>

        <!-- Optional, if you want to use the BigBone RxJava3 wrappers -->
<dependency>
<groupId>io.github.pattafeufeu</groupId>
    <artifactId>bigbone-rx</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```
