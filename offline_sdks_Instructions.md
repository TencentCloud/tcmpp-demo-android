The mini program Offline sdk consists of the following parts:

- repo：annotation processor can not directly rely on the jar package, so it can only be provided through the local maven form, the dependency is as follows:

  ```groovy
  allprojects {
      repositories {
          maven {url "${rootProject.projectDir}/offline_sdks/tcmpp-local-repo/"}
      }
  }

  apply plugin: 'kotlin-kapt'
  kapt 'com.tencent.tmf.android:mini_annotation_processor:1.5.2'
  ```

- tcmpp-mini-core：Core library
- tcmpp-tencent-map：Tencent map

  ```groovy
  implementation fileTree(include: ['*.jar', '*.aar'], dir: "${project.rootDir}/offline_sdks/tcmpp-mini-core")
  implementation fileTree(include: ['*.jar', '*.aar'], dir: "${project.rootDir}/offline_sdks/tcmpp-tencent-map")
  ```

- other sdks

  ```groovy
  implementation "com.google.android.material:material:1.3.0-alpha03"
  implementation 'androidx.core:core-ktx:1.6.0'
  //gosn
  implementation 'com.google.code.gson:gson:2.8.6'
  // ok-http
  implementation "com.squareup.okhttp3:okhttp:3.12.13"
  ```

