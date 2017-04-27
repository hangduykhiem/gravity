### Android Core Library

If you're looking for a base library to help you consistently follow the [clean architecture approach](https://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/) formulated by SoundCloud dev [Fernando Cejas](https://twitter.com/fernando_cejas), **Core Library** is here to help. Created and used in production by [Zalando](https://tech.zalando.com/)'s Android team, this library enables you to quickly create a skeleton structure for your new Android app in a clean architecture way, while following the [Model View Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) (MVP) pattern. It's out-of-the-box usable for anyone.

A good architectural skeleton will help your application to scale without additional complexities.

### Why Use Android MVP Core Library

Besides the base architecture guidelines [**where are the guidelines, and is this where it draws upon the clean arch approach?**], this lib offers:
- basic setup for [Dagger 2](https://github.com/google/dagger) and [Butterknife](https://jakewharton.github.io/butterknife) dependency injection, isolating the basic logic on your fragments and activities
- utility methods to notify on domain layer to apply correct schedulers for each operation or presenters will include helpers to unsubscribe whenever their attach view is destroyed [**this part, which I pulled from below, requires some rewriting for clarity**]. (Contribute your own by adding them to the appropriate utility class; see our [Contributing](#contributing) section for more instructions.) 
- a foundation for using [RxJava2](https://github.com/ReactiveX/RxJava) as the basis for your data-loading operations. 
- an easy way to use [Retrofit2](https://square.github.io/retrofit/) to access network operations, including factory methods for easily accessing your REST APIs.

### Quick Note on MVP
The MVP pattern separates presentation layer from logic, allowing you to focus purely on UI implementation and delegate business logic to [presenters](https://developer.android.com/reference/android/support/v17/leanback/widget/Presenter.html). Antonio Leiva has written up [a great explanation of the MVP pattern](https://antonioleiva.com/mvp-android/). A visual version:

![alt tag](https://informatechcr.files.wordpress.com/2013/03/mvp-diagram.png)

In addition to MVP, Presenters can hold PresenterModule [**not super-clear; can you rephrase?**] to delegate logic handling to those [**to what, exactly?**]. This will make your Presenter logic lightweight and enhance code reusability.

### Installation

We'll soon add this library to Maven so you can add it as dependency into your own projects. For now:
  
- Clone this repository
- Run a [gradle](https://gradle.org/) build command on the project's root folder, `./gradlew assembleRelease`
- Move the generated AAR folder into your project's lib folder. The library is located in `./Core/RealmDAO/build/outputs/aar/`.
- Add it as a dependency into your Gradle file:

```
compile name: 'Core-release', ext: 'aar' // Name "Core-release" must match the name of your file without .aar extension
```
- Ensure you allow have `libs/` folder in your application module and added the correct setup in your gradle file.
```
repositories {
    flatDir {
        dirs 'libs'
    }
    dependencies {
    ...
    }
}
```

On the other hand, the library make use of different dependencies to inject views and dependencies into Activitys and Fragments, add utility base view holder and other utilities on recyclerViews, make use of RxJava2, etc.:

```
  compile "com.android.support:appcompat-v7:25.3.0"
  compile "com.android.support:recyclerview-v7:25.3.0"
  compile "com.android.support:design:25.3.0"
  compile "com.jakewharton:butterknife:8.5.1"
  annotationProcessor "com.jakewharton:butterknife-compiler:8.5.1"
  compile "io.reactivex.rxjava2:rxjava:2.0.7"
  compile "io.reactivex.rxjava2:rxandroid:2.0.1"
  compile "net.orfjackal.retrolambda:retrolambda:2.5.1"
  annotationProcessor "com.google.dagger:dagger-compiler:2.10"
  compile "com.google.dagger:dagger:2.10"
  compile "com.jakewharton.timber:timber:4.5.1"
  compile "com.squareup.retrofit2:retrofit:2.2.0"
  compile "com.squareup.retrofit2:converter-gson:2.2.0"
  compile "com.squareup.retrofit2:adapter-rxjava2:2.2.0"
  compile "com.squareup.okhttp3:logging-interceptor:3.6.0"
```

### Sample app

For complete example, follows sample weather application to understand the usage of **Core library**. Refer to `examples` folder to get it. On the wiki page of this repository, you can find larger explanations on how to use it using the example app as reference.

### TODO

- Write more documentation
- Avoid presenters to die if rotation screen rotation changes
- Add more utility methods
- Add more sample apps

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

### Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For more details, check the [contribution guidelines](CONTRIBUTING.md).

When contributing, please, ensure that you [follow Google Java coding guidelines](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) and your project passes the java checkstyle task. There is a gradle task to verify it, just run in the project root `./gradlew check` task.

### Contact

This software was originally written by Team Gravity in Zalando SE. Please, check MAINTAINERS.md file to contact directly a developer in case you have any questions.

Bug reports and feature requests are more likely to be addressed if posted as issues here on GitHub.


## License

The MIT License (MIT) Copyright © 2016 Zalando SE, https://tech.zalando.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
