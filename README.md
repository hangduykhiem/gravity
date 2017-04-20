### Android MVP Core Library

This library helps you to establish a good practice to use the Model View Presenter (MVP) pattern in your project following a [Clean Architecture approach](https://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/). This library includes basic setup for Dagger2 and Butterknife dependency injection, isolating the basic logic on your fragments and activities. A good architectural skeleton will help your application to grow consistently without adding complexities.

Besides the base architecture guidelines, it includes many utility methods that were found useful for several projects. Feel free to contribute by adding your own in the appropriate utility class. Read [Contributing](#contributing) section. 

### Why use Android MVP Core Library

The library helps you using MVP along a clean architecture approach. MVP pattern separates the presentation layer from the logic, letting you focusing purely on UI implementation delegating the business logic to presenters. [Here](https://antonioleiva.com/mvp-android/) you can find a good explanation of the MVP pattern:

![alt tag](https://informatechcr.files.wordpress.com/2013/03/mvp-diagram.png)

In addition to MVP, presenters can hold as well PresenterModule to delegate logic handling to those. This will make your presenters logic smaller and enhance re-usability of code.

Besides that, this base library will provide you a good skeleton to use RxJava2 as a base for all your data loading operations. Provides several utility methods to notify on domain layer to apply correct schedulers for each operation or presenters will include helpers to unsubscribe whenever their attach view is destroyed. This library also eases the use of Retrofit2 for accesing network operations adding some factory methods to access easily REST APIs.

If you are looking for a base library to help you following consistently a clean architecture approach, this can be a good starting point.

### Installation

At this moment, you need to clone the repository and run a gradlew build yourself, adding later the generated AAR into your own gradle dependencies. In near future we will add the library to maven so it should be easy for you to add it as dependency into your own projects.

- Clone this repository
- Run gradle build command on project's root folder `./gradlew assembleRelease`
- Move the generated AAR folder into your project's lib folder. The library is located in `./Core/RealmDAO/build/outputs/aar/` 
- Add Core as dependency into your gradle file.
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

On the other hand, the library make use of different dependencies to inject views and dependencies into Fragments, add utility base view holder and other utilities on recyclerViews, make use of RxJava2, etc.:

```
compile "com.android.support:appcompat-v7:$SUPPORT_LIBRARY_VERSION"
compile "com.android.support:recyclerview-v7:$SUPPORT_LIBRARY_VERSION"
compile "com.android.support:design:$SUPPORT_LIBRARY_VERSION"
compile "com.jakewharton:butterknife:$BUTTERKNIFE_VERSION"
apt "com.jakewharton:butterknife-compiler:$BUTTERKNIFE_VERSION"
compile "io.reactivex.rxjava2:rxjava:$RXJAVA2_VERSION"
compile "io.reactivex.rxjava2:rxandroid:$RXANDROID2_VERSION"
annotationProcessor "com.google.dagger:dagger-compiler:$DAGGER2"
compile "com.google.dagger:dagger:$DAGGER2"
provided "javax.annotation:jsr250-api:1.0"
compile "com.jakewharton.timber:timber:$TIMBER"
compile "com.squareup.retrofit2:retrofit:$RETROFIT"
compile "com.squareup.retrofit2:converter-gson:$RETROFIT"
compile "com.squareup.retrofit2:adapter-rxjava2:$RETROFIT"
compile "com.squareup.okhttp3:logging-interceptor:$OKHTTP"
```

### Sample app

This repository includes a sample weather application to demo some of the functionalities of the library. Refer to `examples` folder to get it. On the wiki page of this repository, you can find larger explanations on how to use it using the easy example app as reference.

### Contributing

Feel free to open issues and send pull requests to this project. It's currently used in couple Zalando internal projects, but it is possible that does not cover all the use cases for every single project.

When contributing, please, ensure that you [follow Google Java coding guidelines](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) and your project passes the java checkstyle task. There is a gradle task to verify it, just run in the project root `./gradlew check` task.

### TODO
- We will keep adding utility methods while we identify needs to provide them to common apps.

### Contact
This software was originally written by Gravity team in Zalando SE. Please, check MAINTAINERS.md file to contact directly a developer in case you have any questions.

Bug reports and feature requests are more likely to be addressed if posted as issues here on GitHub.


## License

The MIT License (MIT) Copyright © 2016 Zalando SE, https://tech.zalando.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
