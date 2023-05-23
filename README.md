# 简介

[腾讯云小程序平台（Tencent Cloud Mini Program Platform，TCMPP）](https://cloud.tencent.com/product/tcmpp)整合了腾讯在小程序开发、测试、发布、运营和小程序容器的技术能力，为企业客户提供一站式、覆盖全生命周期的小程序技术平台。核心服务包括开发者工具 IDE、预览调试 App、安卓端容器 SDK、iOS 端容器 SDK、统一企业运营端、开放平台端等，帮助企业机构低成本、高效率地打造小程序服务。

tcmpp-demo-android 包含了TCMPP小程序容器集成的示例代码，方便开发者参考。

# 使用说明

## 替换应用配置

Demo 内置的配置文件，是与官方测试应用绑定的，在体验预览您个人应用下的小程序前，需按如下步骤完成配置替换：

1. 登录 [腾讯云小程序平台控制台](https://console.cloud.tencent.com/tcmpp)，下载宿主应用配置文件，用来替换项目中 app/main/assets 路径下的 tcmpp-android-configurations.json 文件。
2. 修改项目包名，与控制台应用配置相同，如果包名不一致，运行时无法通过校验。

完成上述步骤，即可运行体验容器基本功能了。

## 替换内置小程序列表

Demo内置了示例小程序，因默认的内置小程序与官方测试应用绑定，所以替换为您个人应用配置之后，内置小程序将无法打开。您可以通过修改内置小程序配置信息，来修改内置小程序列表：

- 配置文件路径：app/src/main/assets/default_mini_apps.json。
- 配置文件格式：最外层为数组，每个数组对象代表一个内置小程序。

```
[ 
   {        
       "appId":"mp225lc9che0ve9o", // 小程序ID
       "name":"官方示例",  // 小程序名字
       "iconUrl":"https://xxx.png" // 小程序logo
   }
]
```

**注意**：内置小程序需与宿主应用有绑定关系，并且有已发布的正式版本。