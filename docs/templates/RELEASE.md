# Prepare

- [ ] Open a new shell
- [ ] Navigate to the root of this project: `cd /path/to/local/ilo/clone`
- [ ] Switch to the 'develop' branch: `git checkout develop`
- [ ] Make sure you have all upstream changes: `git pull`

# Environment Check

- [ ] Make sure your `$JAVA_HOME` points to at least JDK 11: `javac -version`
- [ ] Make sure you have at least `native-image` >= 19.3.1 installed: `native-image --version`

# Release Branch

- [ ] Export an environment variable: `export ILO_NEW_VERSION=<NEW_VERSION>`
- [ ] Create a new release branch: `git checkout -b release/${ILO_NEW_VERSION}`
- [ ] Push all changes to codeberg: `git push origin release/${ILO_NEW_VERSION}`
- [ ] Open a pull request on codeberg that merges `release/<NEW_VERSION>` into `master`
- [ ] Use this template as pull request

# Documentation

- [ ] add entry in [changelog](../../CHANGELOG.asciidoc)
  Use the following template:
  ```
  == link:https://codeberg.org/metio.wtf/ilo/compare/release/<PREVIOUS_VERSION>...release/<NEW_VERSION>[<NEW_VERSION>]
  
  - link:https://codeberg.org/metio.wtf/ilo/milestone/<MILESTONE_ID>?q=&type=all&state=closed&labels=1735&assignee=0[new features]
  - link:https://codeberg.org/metio.wtf/ilo/milestone/<MILESTONE_ID>?q=&type=all&state=closed&labels=1734&assignee=0[fixed bugs]
  - link:https://codeberg.org/metio.wtf/ilo/milestone/<MILESTONE_ID>?q=&type=all&state=closed&labels=1736&assignee=0[other work done]
  ```
- [ ] Update version numbers in [installation instructions](../topics/INSTALL.asciidoc)

# Build

- [ ] Add all changes: `git add -A`
- [ ] Commit all changes: `git commit -S -m "version ${ILO_NEW_VERSION}"`
- [ ] Build the project: `./mvnw verify -Dskip.graal=false -Drevisoin=${ILO_NEW_VERSION}`
- [ ] Sign build artifacts: `minisign -Sm ./target/ilo-${ILO_NEW_VERSION}-java11.zip ./target/ilo-${ILO_NEW_VERSION}-native-unix.zip`

# Publish

- [ ] Push all changes to codeberg: `git push origin release/${ILO_NEW_VERSION}`
- [ ] Do a final review of everything and merge the changes into master. Keep the `release/<NEW_VERSION>` branch around.
- [ ] Switch to the `master` branch: `git checkout master`
- [ ] Pull all upstream changes: `git pull`
- [ ] Update mirror repositories: `git push mirrors master`

# Announce

- [ ] Create a new release on codeberg, use the changelog entries for this release as description, upload the distribution archives.
- [ ] Create a new release on github.com, use the changelog entries for this release as description, upload the distribution archives
- [ ] Create a new release on bintray, use the changelog entries for this release as description, upload the distribution archives
- [ ] Update the matrix chat room title with a link to the new release
- [ ] Send an announcement to the mailing list
