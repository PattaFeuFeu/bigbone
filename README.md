# BigBone

![Build](https://github.com/andregasser/bigbone/actions/workflows/build.yml/badge.svg)
[![codecov](https://codecov.io/gh/andregasser/bigbone/branch/master/graph/badge.svg?token=3AFHQQH547)](https://codecov.io/gh/andregasser/bigbone)
![Latest Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://s01.oss.sonatype.org/content/repositories/snapshots/social/bigbone/bigbone/maven-metadata.xml&label=Latest%20Snapshot&color=blue&query=.//versioning/latest)
[![Slack](https://img.shields.io/badge/Slack-Join%20the%20discussion-brightgreen?logo=slack&color=%2344BDDF)](https://bigboneworkspace.slack.com/)

**BigBone** is a [Mastodon](https://docs.joinmastodon.org/) client library for Java and Kotlin.

# Table of Contents
* [Why BigBone](#why-bigbone)
* [Project Team](#project-team)
* [Core Functionality](#core-functionality)
* [Implementation Status](#implementation-status)
* [Versioning](#versioning)
* [Adding BigBone to Your Project](#adding-bigbone-to-your-project)
* [Using BigBone](#using-bigbone)
* [Support](#support)
* [Security](#security)
* [Troubleshooting](#troubleshooting)
* [BigBone Contributors](#bigbone-contributors)
* [Contribution](#contribution)
* [Projects Using BigBone](#projects-using-bigbone)
* [Previous Work](#previous-work)
* [License](#license)

# Why BigBone

BigBone is a fork of [Mastodon4J](https://github.com/sys1yagi/mastodon4j), a Mastodon client library for Java and Kotlin
that was published by Toshihiro Yagi. The goal of Mastodon4J was to provide an easy-to-use library for interacting with 
the [Mastodon social media network](https://joinmastodon.org/) from Java and Kotlin code. Unfortunately, it became 
abandoned and has not seen any updates since 2018. 

Since Elon Musk's Twitter acquisition in 2022, Mastodon has gained tremendous popularity. A project that is so well 
received by the community deserves to have up-to-date and maintained client libraries. Because of this, we brought the 
Mastodon4J project back to life in November 2022. That's when this project, the BigBone client library for Java and 
the BigBone client library for Java and Kotlin, was born.

The name **BigBone** mostly has symbolic character. We opted for it as a nod to the awe-inspiring Mastodons of the Pleistocene era,
characterised by their robust and colossal bones. We envision this library serving as a foundational _skeleton_ for your Mastodon-related projects.
Fun fact: There’s also [Big Bone Lick State Park in Kentucky](https://parks.ky.gov/union/parks/historic/big-bone-lick-state-historic-site),
a site where American Mastodons were excavated.
May the BigBone library be as enduring and impactful as its namesake, providing strength and structure to your Mastodon endeavours.

# Project Team

The project is currently taken care of by the following people:

- [André Gasser](https://github.com/andregasser/)
- [Andreas Bartels](https://github.com/bocops)
- [Patrick Geselbracht](https://github.com/PattaFeuFeu)

A big "Thank you" to everyone for the time they invest in this project. Without your help we would not be where we are 
right now. 

# Core Functionality

With a library like BigBone, you can build tools that allow you to
- act on statuses on your timelines (home, local, federated).
- post new statuses or edit existing ones (including media uploads)
- favourite and bookmark statuses
- manage lists
- post polls or vote on them
- schedule statuses
- send direct messages to other people
- manage filters
- follow/unfollow hashtags
- administer your Mastodon instance
- plus lots of other stuff!

# Implementation Status

**We did not release an official version on Maven Central yet**, but there's a `2.0.0-SNAPSHOT` which you can use to 
play around / experiment with. Just please be aware that with every new snapshot version, there can be breaking changes 
along the lines. There will be "darker places" in the library, where stuff will not work as expected. If you find 
issues, please [file an issue](https://github.com/andregasser/bigbone/issues).  

BigBone does not yet implement the full API of Mastodon. Actually, there is still **a lot to do**. For details on the 
current API coverage please check out our [API Coverage](https://bigbone.social/api-coverage/) page.

# Versioning

BigBone uses [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html).

# Adding BigBone to Your Project

## Structure

BigBone consists of two main modules:

1. `bigbone` which exposes the Mastodon API endpoints as handy methods usable via a `MastodonClient`
2. `bigbone-rx` which adds a thin RxJava3 layer around the `bigbone` module

### `bigbone-rx` for RxJava3

To find out more about the RxJava3 module—especially about default error handling—, please check
the [`bigbone-rx` README](bigbone-rx/README.md).


## Gradle (Groovy DSL)

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Gradle project (using Groovy DSL):

Repository:

```groovy
repositories {
    maven {
        url "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    }
}
```

Dependencies:

```groovy
dependencies {
    implementation "social.bigbone:bigbone:2.0.0-SNAPSHOT"
    // Optional, if you want to use the BigBone RxJava3 wrappers
    implementation "social.bigbone:bigbone-rx:2.0.0-SNAPSHOT"
}
```

## Gradle (Kotlin DSL)

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Gradle project (using Kotlin DSL):

Repository:

```groovy
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

Dependencies:

```groovy
dependencies {
    implementation("social.bigbone:bigbone:2.0.0-SNAPSHOT")
    // Optional, if you want to use the BigBone RxJava3 wrappers
    implementation("social.bigbone:bigbone-rx:2.0.0-SNAPSHOT")
}
```

## Maven

Instructions for adding BigBone `2.0.0-SNAPSHOT` to your Maven project:

Repository:

```xml
<repositories>
    <repository>
        <id>maven-central-snapshots</id>
        <name>Maven Central Snapshot Repository</name>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
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
    <groupId>social.bigbone</groupId>
    <artifactId>bigbone</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>

        <!-- Optional, if you want to use the BigBone RxJava3 wrappers -->
<dependency>
    <groupId>social.bigbone</groupId>
    <artifactId>bigbone-rx</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

# Using BigBone

For an easy introduction to using the library, we recommended that you take a look at the `sample-*` modules of this 
project. These modules provide examples for Java and Kotlin:

- `sample-java` for Java example code
- `sample-kotlin` for Kotlin example code

You can run them on your machine. Usually, they require some program arguments to be set before launch. Please
have a look at the code for details.

We also provide some guidance in [USAGE.md](USAGE.md).

# Support

Please check out [SUPPORT.md](SUPPORT.md) for our preferred ways of supporting you.

# Security

Please check out [SECURITY.md](SECURITY.md) for more details on how we handle security issues.

# Troubleshooting

In this section we describe problems that have been reported to us by users of the library. Please 
read this section before you start working with it, as the tips given in this section might 
save you some time.

## Request Not Sent / No Data Returned

Make sure you call `.execute()` on the BigBone library method you want to use. Here's an example:

```kotlin
val client = MastodonClient.Builder(instance).build()
val statuses = client.timelines.getTagTimeline(hashtag, TimelineMethods.StatusOrigin.LOCAL_AND_REMOTE).execute()
```

The same applies if you're using BigBone in a Java project.

## Android Studio: BigBone Classes Not Found

An Android Studio user reported, that he was not able to use BigBone library in Android Studio, as classes were not
recognized by the IDE (marked red instead). In this particular case, the fix was to switch/update to Android Studio Giraffe.
See this issue for more details: https://github.com/andregasser/bigbone/issues/280

# Contribution

Contributions are welcome! There's always something to be done and we try to keep an up-to-date list of issues that can be 
worked on. Please have a look at our [Code Contribution Guidelines](CONTRIBUTING.md) for more details.  

# Projects Using BigBone

Below we would like to mention projects that use the BigBone library.

- [Apus](https://github.com/McPringle/apus): A social wall for conferences and other events created by
  [McPringle](https://fosstodon.org/@McPringle). It can display the conference agenda from various sources together
  with Mastodon posts containing a configurable hashtag. Its purpose is to be displayed at the event on projector and TVs
  to show the attendees the next sessions and to boost posting on Mastodon.
- [MastodonContentMover](https://mastodoncontentmover.github.io/): MastodonContentMover is a command-line tool created by 
  [Tokyo Outsider](https://mas.to/@tokyo_0) that downloads your posts from one Mastodon instance, saving them as a set of 
  files on your computer, and then re-posts them on any other Mastodon instance. Its purpose is to allow Mastodon users to 
  move content they value when migrating from one instance to another, which is not currently possible within Mastodon itself.

Do you have a project that you would like to have listed here? Then please contact us.

# Previous Work

Our personal thanks goes to Toshihiro and his contributors back then for building [Mastodon4J](https://github.com/sys1yagi/mastodon4j). 
The library they have built in the past serves as a basis for our own development efforts. ❤️ Thank you! ❤️

# License

BigBone is published under the MIT license. For more information on rights and obligations related to the MIT license, we would like to refer to
[this page](https://fossa.com/blog/open-source-licenses-101-mit-license/) here. The license file can be found in the [LICENSE](LICENSE) file.
