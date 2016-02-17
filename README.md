# Android Core project

This document describes the setup instructions, tools, libraries, style and naming conventions, best practices, CI and CD practices, etc. for the project.

## GIT strategy

[Forking workflow is followed](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

- Forking the team-level repository
- Git clone your own repository
- [Configure a remote for a fork](https://help.github.com/articles/configuring-a-remote-for-a-fork/)
- Commits your changes to YOUR repos. Every commit should include the JIRA ticket code it is related with. For example, "ZMAL-XYZ: This is a commit" where XYZ is the ticket number.
- Create a pull request to merge your changes to team repos
- ONLY a reviewer can merge your changes to the team-level repository

## Creating a Pull Request

Please, follow this steps before creating a pull requests with your changes to the main repository:

- Fetch all content from upstream (master android repositories)
- Pull changes from upstream and add the changes to the branch you are gonna push for the PR.
- ENSURE that the code compiles, run the tests and executes properly.
- Push changes to your GitHub forked repository.
- Create PR.

## Coding Style

[Follow Google Java coding guidelines](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements). Column limit is 100 characters for a better readability. Take [Intellij google formatting XML](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml) and install it in Android Studio. Copy it into your config/codestyles folder in your intellij settings folder. Under Settings/Code Style select the google-styleguide as current code style for the project. Then, following changes are done to coding style in settings:

- To avoid having extra lines after field annotations
    - Preferences → Editor → Code Style → Java → Wrapping and Braces tab
    - Locate the section Field annotations
    - Check the option Do not wrap after single annotation

- To force not exceeding right margin
    - Preferences → Editor → Code Style → Java → Wrapping and Braces tab
    - Check the option "Ensure right margin is not exceeded"

All files must finish with an empty line. You can setup Android Studio to add it automatically every time the file is saved. [Check](http://stackoverflow.com/a/28660298) for instructions.

## Good Practices in Android

[Read this guide](https://github.com/futurice/android-best-practices) that briefly describes best practices, naming conventions, etc. in Android Development.

## Setup Android Studio

Nowadays Android Studio includes SDK, no need for external installation. SDK manager can be accessed from the IDE directly. To setup the project follow this steps:

- Install JDK 8 (Retrolambda library uses it)
- Install Android Studio
- Checkout code from github.
- Open Module Settings (Right click on project root in Android studio) and ensure JDK8 path is selected
- File/Open and select the folder you just checkout from repository.
- Done!

## CI

Jenkins is used as CI system. Every time jenkins detects a change in repository following actions are triggered:

- Pull changes from repository
- gradle test -> Executes all the unit tests for debug build type. Included those in the core library
- Post result (success or failure) to Fleek Android hipchat channel
