# Introduction

The Tencent Cloud Mini Program Platform (Tencent Cloud Mini Program Platform, TCMPP) integrates Tencent's technical capabilities in mini program development, testing, release, operation, and mini program container technology. It provides a one-stop, full lifecycle mini program technology platform for corporate customers. The core services include developer tools IDE, preview and debugging App, Android container SDK, iOS container SDK, unified enterprise operation end, and open platform end, etc. These services help enterprises and institutions to create mini program services at low cost and high efficiency.

The `tcmpp-demo-android` includes example code for integrating TCMPP mini program containers, making it convenient for developers to reference.

# Usage Instructions

## Replace Application Configuration

The configuration file built into the demo is bound to the official test application. Before you can preview your personal application's mini programs, you need to follow these steps to replace the configuration:

1. Log in to the [Tencent Cloud Mini Program Platform console](https://console.cloud.tencent.com/tcmpp) and download the host application configuration file to replace the `tcmpp-android-configurations.json` file in the `app/main/assets` path of the project.
2. Modify the project package name to match the configuration in the console. If the package name is inconsistent, it will not pass the verification when running.

After completing these steps, you can run and experience the basic functions of the container.

## Replace Built-in Mini Program List

The demo comes with a set of example mini programs. Since the default built-in mini programs are bound to the official test application, they will not open after you replace them with your personal application configuration. You can modify the built-in mini program list by changing the configuration information:

- Configuration file path: `app/src/main/assets/default_mini_apps.json`.
- Configuration file format: The outermost layer is an array, with each array object representing a built-in mini program.

  ```
  [
      {
          "appId": "mp225lc9che0ve9o", // Mini Program ID
          "name": "Official Example",  // Mini Program Name
          "iconUrl": "https://xxx.png" // Mini Program Logo URL
      }
  ]
  ```

Note: Built-in mini programs must have a binding relationship with the host application and must have a published official version.

---