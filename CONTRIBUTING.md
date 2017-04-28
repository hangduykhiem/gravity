# Contributing

We follow a "pull requests only" approach. To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.

**DON'T** push to the master branch directly. Always use feature branches and let people discuss changes in pull requests.
Pull requests should only be merged after all discussions have been concluded and at least 2 reviewer has given their 
**approval**.

### Additional Guidelines

- **every change** needs a test
- 100% code coverage
- keep the current code style. Please follow the [Google Java coding guidelines](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) and ensure that your project passes the Java checkstyle task. There is a Gradle task to verify it: just run in the project root `./gradlew check` task.
