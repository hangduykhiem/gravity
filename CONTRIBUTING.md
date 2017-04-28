## Contributing

### Pull requests only
Please **DON'T** push directly to the master branch. Always use feature branches to give people (including the maintainers) an opportunity discuss your proposed changes.

Pull requests will be merged after all discussions have been concluded and at least two reviewers have given their approval.

### Guidelines
- **every change** needs a test
- 100% code coverage
- keep the current code style. Please follow the [Google Java coding guidelines](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) and ensure that your project passes the Java checkstyle task. There is a Gradle task to verify it: just run in the project root `./gradlew check` task.
