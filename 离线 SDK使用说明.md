小程序离线sdk包含如下几部分：

- repo：小程序注解处理器，注解处理器无法直接依赖jar包，所以只能通过本地maven形式提供，依赖方式如下：

  ```groovy
  allprojects {
      repositories {
  		//添加本地仓库
          maven {url "${rootProject.projectDir}/offline_sdks/tcmpp-local-repo/"}
      }
  }

  apply plugin: 'kotlin-kapt'
  kapt 'com.tencent.tmf.android:mini_annotation_processor:1.5.2'
  ```

- tcmpp-mini-core：小程序核心库目录
- tcmpp-tencent-map：小程序腾讯地图扩展库

  ```groovy
  implementation fileTree(include: ['*.jar', '*.aar'], dir: "${project.rootDir}/offline_sdks/tcmpp-mini-core")
  implementation fileTree(include: ['*.jar', '*.aar'], dir: "${project.rootDir}/offline_sdks/tcmpp-tencent-map")
  ```

- android sdk依赖

  ```groovy
  implementation "com.google.android.material:material:1.3.0-alpha03"
  implementation 'androidx.core:core-ktx:1.6.0'
  //gosn
  implementation 'com.google.code.gson:gson:2.8.6'
  // ok-http
  implementation "com.squareup.okhttp3:okhttp:3.12.13"
  ```

