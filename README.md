### Android Core Library

If you're looking for a base library to help you consistently follow the [clean architecture approach](https://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/) formulated by SoundCloud dev [Fernando Cejas](https://twitter.com/fernando_cejas), **Core Library** is here to help. Created and used in production by [Zalando](https://tech.zalando.com/)'s Android team, this library enables you to quickly create a skeleton structure for your new Android app in a clean architecture way, while following the [Model View Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) (MVP) pattern. It's out-of-the-box usable for anyone.

[**It's still not stated how we've used this library to do things faster/better/etc. Why would anyone use this, besides the general descriptive info? Because it's the only thing out there that does what it does? What are some success metrics we could report?**]

### Why Use Android MVP Core Library

A good architectural skeleton will help your application to scale without additional complexities. Besides the base architecture guidelines [**where are the guidelines, and is this where it draws upon the clean arch approach?**], this lib offers:
- basic setup for [Dagger 2](https://github.com/google/dagger) and [Butterknife](https://jakewharton.github.io/butterknife) dependency injection, isolating the basic logic on your fragments and activities
- utility methods to notify on domain layer to apply correct schedulers for each operation or presenters will include helpers to unsubscribe whenever their attach view is destroyed [**this part, which I pulled from below, requires some rewriting for clarity**]. (Contribute your own by adding them to the appropriate utility class; see our [Contributing](#contributing) section for more instructions.) 
- a foundation for using [RxJava2](https://github.com/ReactiveX/RxJava) as the basis for your data-loading operations. 
- an easy way to use [Retrofit2](https://square.github.io/retrofit/) to access network operations, including factory methods for easily accessing your REST APIs.

### Quick Note on MVP
The MVP pattern separates presentation layer from logic, allowing you to focus purely on UI implementation and delegate business logic to [presenters](https://developer.android.com/reference/android/support/v17/leanback/widget/Presenter.html). Antonio Leiva has written up [a great explanation of the MVP pattern](https://antonioleiva.com/mvp-android/). A visual version:

![alt tag](https://informatechcr.files.wordpress.com/2013/03/mvp-diagram.png)

In addition to MVP, Presenters can hold PresenterModule [**not super-clear; can you rephrase?**] to delegate logic handling to those [**to what, exactly?**]. This will make your Presenter logic lightweight and enhance code reusability.

### Installation

[**What are the technical requirements: versions of Java, build tools, etc.?**]

We'll soon add this library to Maven so you can add it as dependency into your own projects. For now:
  
- Clone this repository
- Run a [gradle](https://gradle.org/) build command on the project's root folder, `./gradlew assembleRelease`
- Move the generated AAR folder into your project's lib folder. The library is located in `./Core/RealmDAO/build/outputs/aar/`.
- Add it as a dependency into your Gradle file:

```
compile name: 'Core-release', ext: 'aar' 
## Name "Core-release" must match the name of your file without .aar extension
```

Next, ensure you have the `libs/` folder in your application module, with the correct setup in your Gradle file added:
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

The library uses [different dependencies to inject views and dependencies] [**dependencies to inject dependencies?**] into Activities and Fragments. Add [**"a," or "the"?**] utility base view holder and other utilities on recyclerViews, make use of RxJava2, etc. [**seems we could break this sentence down a bit further into two steps, then also break down this big code chunk. What are we supposed to notice most, in the code below, and why?**]:

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

### Sample App

To see this library in action, view the sample weather application [**link to it**] in our `examples` folder [**link to this, too**]. More details provided at our Wiki [**link**], which uses the example app as reference.

### TODO

- Write more documentation [**about?**]
- Stop Presenters from dying if the rotation screen rotation changes, [**so that ...?**]
- Add more utility methods
- Add more sample apps

### Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in our [Issues Tracker](../../issues). For urgent help, contact [the maintainers](**add the link**) directly.

### Getting Involved/Contributing

Check the [contribution guidelines](CONTRIBUTING.md) for details.

### License

The MIT License (MIT) Copyright © 2016 [Zalando SE](https://tech.zalando.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
