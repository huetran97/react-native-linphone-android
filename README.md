Linphone is a free VoIP and video softphone based on the SIP protocol.
# Installation

```sh
npm install react-native-linphone --save
```
# Installation (Android)
1. Make sure to specify minSDKVersion in your build.gradle file >=16 and targetSdkVersion <=27.


```gradle
android {
    compileSdkVersion 25
    buildToolsVersion "25.0.1"

    defaultConfig {
        applicationId "com.liblin"
        minSdkVersion 16
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
    }
    }
```
2. Don't forget  import org.linphone.reactLin.LinphonePackage in MainApplication. This file exists under the android folder in your react-native application directory. The path to this file is: 'android/app/src/main/java/com/your-app-name/MainApplication.java'
```java
protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new LinphonePackage()); // <-- Add this line.
}
```


