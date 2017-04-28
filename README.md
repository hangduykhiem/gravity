### Gravity Library

If you're looking for a base library to help you consistently follow the [clean architecture approach](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) 
formulated by Uncle Bob (Robert C. Martin), **Gravity library** is here to help. Created and used 
in production by [Zalando](https://tech.zalando.com/)'s Android team, this library enables you to 
quickly create a skeleton structure for your new Android app in a clean architecture way, while 
following the [Model View Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) (MVP) pattern. 
It's out-of-the-box usable for anyone.

**Gravity library** provides scalable architecture for any Android application. The MVP abstraction 
using clean architecture principle reduces the time require to setup your Android project. The 
library includes extensible interface which can be easily adaptable to your Android project. The
library provides radical agility to your Android application. Using this library with feature based 
approach, you can easily add or remove feature from your application in an agile manner. This 
library is an attempt to abstract out Android specific common patterns and utilities into single 
package. 

### Why Use Gravity Library

A good architectural skeleton will help your application to scale without additional complexities. Besides the clean architecture approach, it includes many structure utilities which can be useful for your new Android app. The **Gravity library** help you in following ways:

- a foundation for using [RxJava2](https://github.com/ReactiveX/RxJava) as the basis for your data-loading operations. 

- an easy way to use [Retrofit2](https://square.github.io/retrofit/) to access network operations, including factory methods for easily accessing your REST APIs.

- basic setup for [Dagger 2](https://github.com/google/dagger) and [Butterknife](https://jakewharton.github.io/butterknife) dependency injection, isolating the basic logic from your fragments and activities.

- abstract classes to wrap logic into data layer, domain layer and presenter layer. 

- utility methods to notify on domain layer to apply correct schedulers for each operation.

- provides base presenter which includes helpers to subscribe and unsubscribe from fragment or activity lifecyle.

- interfaces to hide details for quick setup of view holders and adapters. 

Feel free to contribute by enriching the **Gravity library** with your own utilities by adding them to the appropriate utility class. 
See our [Contributing](#contributing) section for more instructions.

### Quick Note on MVP
The [MVP pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) separates presentation layer from logic, allowing you to focus purely on UI implementation and delegate business logic to presenters. 
A visual representation for MVP as follows:

![alt tag](https://informatechcr.files.wordpress.com/2013/03/mvp-diagram.png)

### Installation

The **Gravity library** dependencies are defined in build.gradle file under root directory. The library is compatible with Java version 7 or higher.

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

The library uses different android libraries to provide skeleton structure for your Android app. Following dependencies are included in the **Gravity library**.

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

To see this library in action, view the sample weather application in our [`examples` folder](https://github.bus.zalan.do/gravity/android-core/tree/master/examples). 
More details provided at our [Wiki](https://github.bus.zalan.do/gravity/android-core/wiki/How-to-create-project), which uses the example app as reference.

### TODO

- Write more API documentation for the library
- Handle rotation change to enable presenters to work property in the application.
- Add more utility methods
- Add more sample apps

### Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in our [Issues Tracker](../../issues). For urgent help, contact [the maintainers](MAINTAINERS.md) directly.

### Getting Involved/Contributing

Check the [contribution guidelines](CONTRIBUTING.md) for details.

### License

The MIT License (MIT) Copyright © 2016 [Zalando SE](https://tech.zalando.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
