# Code Contribution Guidelines

Welcome to the Code Contribution Guidelines for BigBone!

This document provides important information for developers which are interested in contributing to BigBone. First of 
all, thank you for taking an interest in the further development of this project. We want to make it as easy as possible
for you to contribute to the project. For this reason we have created this guide.

## Getting Started

In this section we will help you set up your machine for working on the project and also give you some ideas for finding
tasks and/or issues you could contribute to.

### Preparing Your Machine

Before you can contribute work to the project, you need to set up your machine. For BigBone, this simply means, you should 
[clone the code repository](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) 
to a directory of your choice and then run a `gradlew check` from within the root directory of the project to see if 
everything builds correctly and the tests run fine. We use [Gradle](https://gradle.org/) to build the project. Don't 
worry if you do not have it installed on your machine, as it will be downloaded automatically during the first build.

Please also note that the project requires at least Java 17 in order to build successfully.

### Project Structure Overview

The project comprises five primary Gradle modules:

- **bigbone:** This module contains the core code for interacting with the Mastodon REST API, including tests.
- **bigbone-rx:** This module serves as a thin layer built on top of the `bigbone` module. It exposes methods available 
  in the `bigbone` module as RxJava3 types, allowing the use of this library within an RxJava context.
- **buildSrc:** This section contains Gradle plugin code written in Groovy. Generally, you won't need to make changes 
  here, as it's exclusively used for the build process. However, in specific cases, such as introducing a new 
  dependency, you might need to make adjustments.
- **sample-java:** This module contains Java sample code that demonstrates the usage of BigBone.
- **sample-kotlin:** Similar to sample-java, this module provides sample code written in Kotlin.

Additionally, there are other folders within the project, including:

- **config:** This folder houses tooling configurations for code analysis tools like PMD, Checkstyle, and Detekt.
- **docker:** Here, you can find Docker Compose configurations for setting up a functional Mastodon instance. This 
  instance is intended for running integration tests on a developer's local machine. While it essentially works, it may
  require further refinement to operate seamlessly.
- **docs:** Here we host the documentation that is published on https://bigbone.social.
- **gradle:** This section contains the Gradle version catalog and wrapper configurations for the build process.
- **.github:** Within this folder, you'll find GitHub workflow configurations and settings related to Dependabot.

### Find Something to Work On

Now as everything is set up nicely on your local machine, it is time to find something for you to work on. Here are some
ideas for you:

- **Good First Issues**: Check our [issue tracker](https://github.com/pattafeufeu/bigbone/issues) for issues labeled 
  `good first issue`. These are usually good tasks to get started and get into the project.
- **API Coverage**: Visit our [API Coverage](https://bigbone.social/api-coverage/) page and look for entries highlighted
  in orange. These are endpoints that are not yet fully implemented. Most of the time there is a parameter missing that 
  needs to be implemented. Alternatively you can also look for endpoints marked in red. These are endpoints that are not
  yet supported by BigBone.
- **Improve Sample Code**: BigBone provides sample code in two of the modules (`sample-java` and `sample-kotlin`). You 
  could contribute a new sample to the project which adds value for library users.
- **Improve Test Coverage**: Our test coverage is not as good as it should be. There is quite some code which is not 
  covered by tests yet. You could add value by adding new tests to our library.
- **Improve Documentation**: We host the project's documentation on https://bigbone.social. The source for this
  documentation is located in the `/docs` folder. Try to improve the documentation by creating a pull request.

Of course, we're also open for other types of contributions. Plus: Every contribution counts, even if it is a small one.

Make sure you let us know that you are working on an issue. Ideally, there's an issue in our issue tracker already which
you can simply assign to yourself. If not, feel free to open a new issue.

### Create a Feature Branch

Go to your local directory into which you have cloned our repository. You should be on the `master` branch. Create a new
feature branch for your work. You can use the following command for this:

```bash
$ git checkout -b feature/my-awesome-contribution
```

You can now work on your contribution, congrats! 

### When You Get Stuck

Whenever you get stuck while trying to contribute to the project, you can reach out to BigBone developers. Please use 
one of the channels listed below as they provide maximum transparency for the project and it allows us to keep a good 
historical record of communication. 

- **Slack**: BigBone developers use [Slack](https://github.com/pattafeufeu/bigbone/discussions/151) to coordinate work 
  between developers. We would appreciate it, if you join our `#bigbone-development` channel. Also, it allows you to 
  reach individual developers directly.
- **GitHub Issues**: If you are working on an existing GitHub issue, feel free to discuss your problems on that issue 
  itself.
- **GitHub Discussions**: If you would like to discuss a topic, feel free to open a new discussion thread.

BigBone is developed and maintained by volunteers in their spare time. Therefore, we cannot guarantee response times or 
the like. We do not offer professional support for this library. Support is always best effort and sometimes it can take
longer because nobody has time. This is just how it is. Thank you for your understanding.

## Submit Your First Contribution

Congratulations! Your first contribution is ready for submission. But before you do that, we would like to ask you to 
check a few things before you move forward. This is to ensure that your changes can be integrated into our project 
without problems.

- When you open a new pull request, we automatically provide a pull request template text that you can fill out/modify. 
  It contains checklists to go through in order to make sure you do not forget important things. Please make use of it.
- Give your pull request a meaningful name as we use this in our release notes (e.g. "Add support for favourites API")
- Please compact the changes you submit into a single commit (you can squash commits locally using git). We try to keep 
  a clean timeline on the `master` branch.
- Your pull request should target our `master` branch.

Once you have opened the pull request, we will review your changes. Please be patient and make sure, you have project 
notifications turned on, ensuring you get notified when someone adds a comment to your contribution.  

## Report an Issue

We use GitHub issues to track issues. Whenever you find an issue in our library, whether it is a problem in the code or 
in the documentation, please [open a new issue](https://github.com/pattafeufeu/bigbone/issues). Make sure you provide as
much information as possible. It should be possible for us to reproduce the issue easily. Great issue reports have:

- A quick summary and/or background
- Steps to reproduce
- Give sample code if you can.
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

## Code of Conduct

We also have a [Code of Conduct](CODE_OF_CONDUCT.md) and ask you to follow it in all your interactions with the project.

## License

Any contributions you make will be under the MIT Software License. Feel free to contact the maintainers if that's a concern.
