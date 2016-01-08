# Fleek Android project

This document describes the setup instructions, tools, libraries, style and naming conventions, best practices, CI and CD practices, etc. for the Android project.

## JIRA

[Fleek Project Jira](https://techjira.zalando.net/browse/ZMAL/?selectedTab=com.atlassian.jira.jira-projects-plugin:versions-panel)

## GIT strategy

[Forking workflow is followed](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

- Forking the team-level repository
- Git clone your own repository
- [Configure a remote for a fork](https://help.github.com/articles/configuring-a-remote-for-a-fork/)
- Commits your changes to YOUR repos. Every commit should include the JIRA ticket code it is related with. For example, "ZMAL-XYZ: This is a commit" where XYZ is the ticket number.
- Create a pull request to merge your changes to team repos
- ONLY a reviewer can merge your changes to the team-level repository

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

## Building flavours

Two different [building flavours](http://tools.android.com/tech-docs/new-build-system/build-system-concepts) will be used to reduce the overhead and human errors when building releases. For each flavour there is a Debug and Release build, release is signed (then will contain obfuscated code and proguard optimisations) and debug build which is same build that is installed while debugging in Android Studio. Following important information can be change on build time:

- Customised setup constants
- MinSdkVersion
- TargetSdkVersion
- VersionCode
- VersionName
- Package name (overrides value from manifest)
- Release signing info (keystore, key alias, passwords,...).
- [Check](http://tools.android.com/tech-docs/new-build-system/build-system-concepts) for more information

For the project, following build flavours are defined:

- Default flavour: Used for local development and G.Play release build. Debug will use dev. server. Release will use production server.
- HockeyApp flavour: Used for internal distribution of packages. Debug build will use dev. server. Release will use staging server.

## Architecture guidelines

App will be divided in two modules:

- FleekAndroid module: Will contain app specific code, resources, manifest, etc.
    - Packages will be divided using [Package by feature](http://www.javapractices.com/topic/TopicAction.do?Id=205) instead layered division. Inside each feature package, layered division must be accomplished.
    - Use feature flags to disable features whenever they are not ready for production.
- Core module: Will contain common code, abstract classes, utility classes, etc. commonly used horizontally among different Android apps. Do not add project specific implementation features under this module.

To reduce complexity on UI related classes and let Fragment and Activity classes just focus in UI rendering and animations. It's important to use MVP pattern (Model View Presenter). View interacts with presenters only, which are responsible of requesting model load and notify UI for changes.

![alt tag](https://informatechcr.files.wordpress.com/2013/03/mvp-diagram.png)

To reduce complexity when dealing with Threads and avoid the callback nightmare. Use Reactive Programming when dealing with Asynchronous calls. It also eases the Automated Testings of asynchronous calls. Be reactive! Check:

 - [ReactiveX](http://reactivex.io/)
 - [RxJava](https://github.com/ReactiveX/RxJava)
 - [Grokking RxJava tutorial](http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/)
 - [Top 7 tips for RxJava on Android](http://futurice.com/blog/top-7-tips-for-rxjava-on-android)
 - [Replace AsyncTask and AsyncTaskLoader with Rx.Observable - RxJava Android Patterns](http://blog.stablekernel.com/replace-asynctask-asynctaskloader-rx-observable-rxjava-android-patterns/)

## 3rd party libraries

To make our life easier, avoid reinventing the wheel. There are many MUST to use libraries to make code cleaner, thus reduce complexity and avoid unnecessary bugs (well, all bugs are unnecessary!). These are the libraries that are used in the project:

- [GSON by Google](https://github.com/google/gson) Java library used to convert Java Objects into their JSON representation (and other way around).
- [RetroFit by Square](http://square.github.io/retrofit/) Library to turn Rest APIs into easy to use Java interfaces. It supports RxJava for asynchronous tasks.
- [EventBus by GreenRobot](http://greenrobot.github.io/EventBus/) Library to simplify communication between activities, fragments, threads, services, etc. For example, communication between background service (e.g. Push message handling) and UI.
- [Fresco by Facebook](http://frescolib.org/) Library to make image loading easy. Plenty of memory handling optimisations in place.
- [Butterknife by Jake Wharton](https://github.com/codepath/android_guides/wiki/Reducing-View-Boilerplate-with-Butterknife) View "injection2 for Android. Writes common boilerplate code using annotations to reduce the initialisation and setup of views. It uses compile-time annotations, no additional cost on run-time.
- [Dagger 2 by Google](https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2) Dependency injection library for Android. It provides a simply way to obtain references to shared instances and decoupling dependencies among classes. It uses compile-time annotations, no additional cost on run-time.
- [Realm](https://realm.io/) Easy to use database, faster than native SQLLite.
- [RetroLambda](https://github.com/orfjackal/retrolambda) Bring Java8 features like Lambda, method references and try-with-resources statements to Android. Reduces a lot the boilerplate code making it easier to read. [Use this Gradle Plugin](https://github.com/evant/gradle-retrolambda) to setup the environment.
- [RxAndroid](https://github.com/ReactiveX/RxAndroid) Android specific bindings for RxJava. This module adds the minimum classes to RxJava that make writing reactive components in Android applications easy and hassle-free.
- [IcePick](https://github.com/frankiesardo/icepick) Icepick is an Android library that eliminates the boilerplate of saving and restoring instance state.
- [Timber](https://github.com/JakeWharton/timber) This is a logger with a small, extensible API which provides utility on top of Android's normal Log class.

## Unit Testing

Use Junit4 for non Android dependant tests. [Check official documentation](http://tools.android.com/tech-docs/unit-testing-support)
Use [Robolectrict](http://robolectric.org/) to test non UI dependant tests. Tests run directly in your local machine JVM making tests fast and easy to perform. For example, model access can be easily tested with Robolectric.
Use [Espresso 2 by Google](http://developer.android.com/training/testing/ui-testing/espresso-testing.html) to perform UI testings. It provides APIs for writing UI tests to simulate user interactions.

## CI

TBD once project begins.

## Push messages

[Accengage](http://www.accengage.com/) will be used to send push messages to clients.

- [See](http://docs.accengage.com/display/GEN/Home) to download the SDK
- [Documentation](http://docs.accengage.com/display/AND/Android)

## Crash reports

Use AppDynamics to receive crash reports once a crash happens.

- [See](https://docs.appdynamics.com/display/PRO14S/Instrument+an+Android+Application#InstrumentanAndroidApplication-SetupforGradle) to add SDK to repository.
- [Documentation](https://docs.appdynamics.com/display/PRO14S/Instrument+an+Android+Application)
- [HockeyApp](http://hockeyapp.net/) crash reports will be used for internal distribution of test packages also supports crash reports. Use it for alpha distributions.

## Continues Delivery

APK testing distributions are done using [HockeyApp](http://hockeyapp.net/). Once new commit is pushed to origin/development the CI system will run the test cases, if all of them pass, it will create the APK and upload the client to the service for testing automatically.

## Signing the APK

TBD Once project is ready to be released for the first time

#### How to sign an APK

[Read this guide](https://github.com/futurice/android-best-practices#gradle-configuration) that describes how to setup Gradle in an easy way to sign APKs. It's a must to have the signing password outside gradle to avoid having it in the repository.

## UI Mockups

TBD once project begins

## Google Analytics

TBD once project begins
