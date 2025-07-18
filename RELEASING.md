# Releasing

## Overview
This guide explains how we publish development snapshots and releases for BigBone. The whole process is based on
GitHub Actions and the Gradle build system. The process is split into two parts: publishing a new development snapshot
and publishing a new release. This guide is mostly relevant for project maintainers.

## Publish a New Snapshot
Whenever you want to release a new development snapshot, please follow the steps listed below. Let's assume we're about
to publish a development snapshot for the upcoming `2.0.0` version. The process is as follows:

1. Agree within the team whether a new development snapshot should be published or not.
2. Make sure that attribute `bigbone` in `/gradle/libs.version.toml` is set to `bigbone = "2.0.0-SNAPSHOT"`. If this is
   not the case, change it and commit the change to the `master` branch. Note down the commit hash of the latest commit
   in the master branch as we'll need it later. For this example, we simply assume it is `aaaaaaa`.
3. Manually invoke the GitHub `Release` workflow on the master branch. The workflow does the following:
    * Builds, signs and publishes the most recent version of the `master` branch to the Maven Central Snapshot Repository.
    * Creates a new draft of a release notes page [here](https://github.com/pattafeufeu/bigbone/releases).
4. Once the workflow has successfully finished, the new development snapshot is published. Now do the following:
    * Verify, that the binaries have been successfully uploaded to the Maven Central Snapshot Repository by visiting
      the [Maven Central repository URL for BigBone snapshots]( https://s01.oss.sonatype.org/content/repositories/snapshots/social/bigbone/bigbone/).
    * Note down the Maven Central timestamp (let's assume it is `20230530.130705`) and use it together with the commit
      hash from above (which is `aaaaaaa`) to create a tag for the snapshot on our `master` branch using
      `git tag -a v2.0.0-20230530.130705 -m v2.0.0-20230530.130705 aaaaaaa`. Push the tag to the `master` branch using
      `git push origin v2.0.0-20230530.130705`.
    * Edit the release notes page that was created during the workflow run and bring it into good shape. Use one of the
      previously created release note documents as a basis to get a feeling on how it should look like. Once you are
      happy, ask other project members for a quick review. Make sure breaking changes are part of the release notes.
      After positive feedback, publish the release notes.

## Publish a New Release
Let's assume we want to publish a new release for version `2.0.0`. A new release for this version is published to Maven
Central by performing the following steps in order:

1. Agree within the team whether a new release version should be published or not.
2. Make sure that attribute `bigbone` in `/gradle/libs.version.toml` is set to `bigbone = "2.0.0"`. If this is not the
   case, change it and commit the change to the `master` branch. Note down the commit hash of the latest commit
   in the master branch as we'll need it later. For this example, we simply assume it is `aaaaaaa`.
3. Create a new tag for commit hash `aaaaaaa` using `git tag -a v2.0.0 -m v2.0.0 aaaaaaa` and push it to the `master`
   branch using `git push origin v2.0.0`.
4. Since you've pushed a tag matching the pattern `vX.Y.Z`, the GitHub `Release` workflow is automatically executed. The
   workflow does the following:
    * Builds, signs and publishes tag `v2.0.0` to Maven Central.
    * Creates a new draft of a release notes page [here](https://github.com/pattafeufeu/bigbone/releases).
5. Once the workflow has successfully finished, please do the following:
    * Verify, that the binaries have been successfully uploaded to Maven Central by visiting the [Maven Central
      repository URL for BigBone releases](https://repo1.maven.org/maven2/social/bigbone).
    * Edit the release notes page that was created during the workflow run and bring it into good shape. Use one of the
      previously created release note documents as a basis to get a feeling on how it should look like. Once you are
      happy, ask other project members for a quick review. After positive feedback, publish the release notes.
6. Agree within the team on the new development snapshot version (for simplicity, we assume `2.1.0`). Once that is sorted
   out, open `/gradle/libs.version.toml` and set the version to `bigbone = "2.1.0-SNAPSHOT"`. Commit this change to the
   `master` branch of the repository. The repository is now prepared to work on the `2.1.0` development snapshot.
