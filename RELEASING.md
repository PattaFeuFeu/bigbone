# Releasing

## Overview

We publish artefacts for BigBone to **Sonatype Central / Maven Central** using GitHub Actions and Gradle.
There are two paths: publishing a **snapshot** and publishing a **release**.

- **Snapshots** are published automatically when `master` contains a `-SNAPSHOT` version.
- **Releases** are published automatically when you push a Git tag like `v2.0.0`.

All artefacts are signed (PGP) and uploaded via the Central Publisher API.

## Overview
This guide explains how we publish development snapshots and releases for BigBone. The whole process is based on
GitHub Actions and the Gradle build system. The process is split into two parts: publishing a new development snapshot
and publishing a new release. This guide is mostly relevant for project maintainers.

### Publish a New Snapshot

Assume we're working towards `2.1.0`.

1. Agree within the team that a new snapshot is desired.
2. In `/gradle/libs.versions.toml`, set the library version to `2.1.0-SNAPSHOT` and commit to `master`.
3. Run the **Snapshot** workflow manually, specifying the ref and optional label. It will:
   - build, sign, and publish to the Central **Snapshots** repository, and
   - make the snapshot immediately consumable under the `-SNAPSHOT` coordinate.
   - create a **draft** pre-release on GitHub Releases.

**Verify:**

- In the Sonatype Central Portal (search for `io.github.pattafeufeu:bigbone`), you should see the new snapshot build

4. Edit the drafted release notes on GitHub and publish them after a quick review.

### Publish a New Release

Assume we’re releasing `2.0.0`.

1. Agree within the team that a new release is ready.
2. In `/gradle/libs.versions.toml`, set the library version to `2.0.0` and commit to `master`.
3. Create and push a Git tag: `git tag -a v2.0.0 -m v2.0.0 && git push origin v2.0.0`
4. Pushing the tag triggers the **Release** workflow. It will:
   - build, sign, and publish `v2.0.0` to Sonatype Central (auto-publish is enabled), and
   - create a **draft** release on GitHub Releases.

**Verify:**

- Once processing completes, the artefacts appear on Maven Central under:  
  `io/github/pattafeufeu/bigbone` and `io/github/pattafeufeu/bigbone-rx`.

5. Edit the drafted release notes on GitHub and publish them after a quick review.

---

### Coordinates

Use the new GitHub-verified groupId:

```groovy
dependencies {
    implementation "io.github.pattafeufeu:bigbone:<version>"
    implementation "io.github.pattafeufeu:bigbone-rx:<version>"
}
