<h1>
    Yieldprobe SDK Android Sample Application
</h1>

<p>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg"></a>
    <a href="http://github.com/yieldlab/yieldprobe-sdk-android-sampleapp/releases/" title="Version"><img src="https://img.shields.io/badge/version-1.0.0-blue"></a>
</p>

This repository contains:
* The [**Yieldprobe Sample Application**](/src/) referencing the SDK from JCenter.

For the SDK source look into the other [SDK repository](https://github.com/yieldlab/yieldprobe-sdk-android).

Additional documentation:
* [**Sample Application Guide**](#guidesample).
* [**Integration Guide**](#guideintegration) for the SDK into your application.

<a name="guidesample"></a>
# Sample Application Guide

**Table of Contents**
1. [Actions](#guidesample1)
2. [Screenshots](#guidesample2)

<a name="guidesample1"></a>
## 1. Actions
The **Yieldprobe Sample Application** calls the API from Kotlin as well as Java in two separate activities. It does extensive error handling.

You can do the following actions inside the application:

* **Initialization**
    * Check for **Google Play Services** availability. They are needed for IDFA (ID for Advertiser) and Geolocation readout. It will check at runtime for enabled services. See the screenshots below.
    * Initialize the SDK (use checkboxes to set Geolocation and PersonalizedAds).
    * Check the initialization status of SDK.
* **Configuration**
    * Set configuration (use checkboxes to set Geolocation and PersonalizedAds).
    * Get the current configuration.
    * Get device metadata.
* **Probe requests and targeting**
    * Select three predefined test adslots or provide your own ones and do a probe request over the EventBus or Futures API (works only on devices with API level 24 and above).
    * Do three probe requests in parallel (predefined adslots).
    * View the last targeting response.
* **Advertisement targeting**
    * Provide targeting to the **Adition SDK** and display an advertisement.
* **Permissions and logging**
    * Check and request the location permissions.
    * See a log of the Yieldprobe SDK.

<a name="guidesample2"></a>
## 2. Screenshots

<p float="left" align="center">
  <img src="/docs/screenshot_1.png" width="260" />
  <img src="/docs/screenshot_2.png" width="260" /> 
  <img src="/docs/screenshot_3.png" width="260" />
</p> 
<p float="left" align="center"> 
  <img src="/docs/screenshot_4.png" width="260" />
  <img src="/docs/screenshot_5.png" width="260" /> 
  <img src="/docs/screenshot_6.png" width="260" />
</p>

<a name="guideintegration"></a>
# Integration Guide

**Table of Contents**

1. [Introduction](#guideintegration1)
2. [Dependencies and Manifest](#guideintegration2)
3. [Initialization of SDK](#guideintegration3)
4. [Configuration Updates](#guideintegration4)
5. [Probe Request to get Targeting](#guideintegration5)
    1. [CompletableFutures API](#guideintegration51)
    2. [EventBus API](#guideintegration52)
6. [Show Advertisements with Adition SDK](#guideintegration6)
7. [Compilation and ProGuard](#guideintegration7)
8. [Logging and Exceptions](#guideintegration8)

<a name="guideintegration1"></a>
## 1. Introduction

This section describes the integration process of the Yieldlab Yieldprobe SDK for Android. Read through this guide to integrate the SDK into your application. You can always take a look into the complete sample application called **Yieldprobe Sample Application** to see the SDK usage from Kotlin or Java.

The code snippets are taken out of the files [AndroidManifest.xml](/src/app/src/main/AndroidManifest.xml), [build.gradle](/src/app/build.gradle), [ActivitySDKKotlin.kt](/src/app/src/main/java/com/yieldlab/yieldprobe_sample_application/ActivitySDKKotlin.kt) and [ActivityAd.kt](/src/app/src/main/java/com/yieldlab/yieldprobe_sample_application/ActivityAd.kt). The code is shown in Kotlin only. There is always the equivalent snippet in [ActivitySDKJava.java](/src/app/src/main/java/com/yieldlab/yieldprobe_sample_application/ActivitySDKJava.java).

For complete documentation of all exposed functions and data types see the generated [SDK documentation](https://yieldlab.github.io/yieldprobe-sdk-android/).

<a name="guideintegration2"></a>
## 2. Dependencies and Manifest

The library is available from JCenter. Update the dependencies in your [build.gradle](/src/app/build.gradle) file. The SDK was verified with the library versions below.

**Your minSDK version can not be lower than API level 16. API levels under 16 are not supported by the SDK**.


```gradle
compileSdkVersion 29
defaultConfig {
    minSdkVersion 16
    targetSdkVersion 29
}


dependencies {
    // needed for SDK to work
    implementation 'org.greenrobot:eventbus:3.1.1'
    implementation 'com.google.android.gms:play-services-location:17.0.0'
    implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
    implementation 'com.squareup.okhttp3:okhttp:4.2.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    // the library itself from JCenter
    implementation 'com.yieldlab.yieldprobe:yieldprobe:1.0.0'
}
```

The SDK needs certain permissions to be added in your [AndroidManifest.xml](/src/app/src/main/AndroidManifest.xml). It is very likely your application already uses these.
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

The location permissions needs a runtime confirmation by the user. 

See the sample code in how to request these permission. The relevant functions are `checkPermissionsAndRequest()` and `onRequestPermissionsResult()`.

<a name="guideintegration3"></a>
## 3. Initialization of SDK

First, you should check for **Google Play Services** availability before initializing the SDK.

```kotlin
// check for Play Services. Do not show a popup for user if not enabled.
val available = Yieldprobe.isGooglePlayServicesAvailable(this, false)
```

The SDK is configured over a `Configuration` object. See the data class definition below.
```kotlin
data class Configuration(
    var mGeolocation: Boolean = false,
    var mUsePersonalizedAds: Boolean = true,
    var mRequestTimeoutInMs: Long = Yieldprobe.DEFAULT_HTTP_CONNECTION_TIMEOUT_IN_MS,
    var mAppName: String? = null,
    var mBundleName: String? = null,
    var mStoreURL: String? = null,
    var mExtraTargeting: Map<String, String?>? = null
)
```

The SDK has to be initialized **once** with `Yieldprobe.initialize()` for the lifetime of your application. Make sure to pass the **ApplicationContext**, not the **Activity / Fragment Context**.

Kotlin call with default parameter naming in configuration object.
```kotlin
try {
    val configurationDefault: Configuration =
        Configuration(
            true, 	// Geolocation enabled
            true, 	// PersonalizedAds enabled
        )
    Yieldprobe.initialize(applicationContext, this, configurationDefault)
} catch (e: Exception) {
    e.printStackTrace()
}
```

<a name="guideintegration4"></a>
## 4. Configuration Updates

You can update the configuration after initialization during runtime with `Yieldprobe.configure()`.

```kotlin
try {
    val configurationNew: Configuration =
        Configuration(
            false, 	// Geolocation disabled
            false, 	// PersonalizedAds disabled
        )
    Yieldprobe.configure(applicationContext, this, configurationNew)
} catch (e: Exception) {
    e.printStackTrace()
}
```

<a name="guideintegration5"></a>
## 5. Probe Request to get Targeting

To get targeting for adslots call the `Yieldprobe.probe()` or `Yieldprobe.probeWithEvents()` functions with a single `Int` or a `Set<Int>`.

```kotlin
var singleAdslot: Int = 123456
var setOfAdslots: Set<Int> = setOf(123456, 234567, 345678)
```

The API is asynchronous. It will respond after the request is completed with a single `Bid` or a `HashMap<Int, Bid>`.

```kotlin
data class Bid (
    var timestamp: String? = null,
    var id: String? = null,
    var customTargeting : MutableMap<String, Any?> = mutableMapOf<String, Any?>()
)
```

* The API implements a connection timeout for the HTTP client connection time. The default value is set to 10.000 ms from `Yieldprobe.DEFAULT_HTTP_CONNECTION_TIMEOUT_IN_MS`.
* The API supports requests in parallel. You can issue multiple requests and will be notified for each with a separate response.
* **Please be aware of the implications of different API levels of `minSDK` for the API to choose from. There are two API options.**

<a name="guideintegration51"></a>
### 5.i CompletableFutures API
You can use the [CompletableFutures](https://developer.android.com/reference/java/util/concurrent/CompletableFuture) API if your `minSDK` is equal or above API level 24.

A sample call in Kotlin.
```kotlin
// A sample call with a set
var setAdslots = SetOf(1233, 2334, 45343)
Yieldprobe.probe(setAdslots).thenApply {
    // var it contains the response HashMap
}.exceptionally { e ->
    e.printStackTrace()
}
```

<a name="guideintegration52"></a>
### 5.ii EventBus API
Otherwise, **you have** to use the [EventBus](https://github.com/greenrobot/EventBus) API. It most cases you want to use this API because it is downwards compatible with API level 16. 

You also have to register to the Events. EventBus setup inside of Activity or Fragment lifecycle functions.

```kotlin
public override fun onStart() {
    super.onStart()
    EventBus.getDefault().register(this)
}

public override fun onStop() {
    super.onStop()
    EventBus.getDefault().unregister(this)
}
```

Subscription to Events

```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
fun onMessageEvent(event: EventProbeSuccess) {
    // do something with the response in event.getBids()
}

@Subscribe(threadMode = ThreadMode.MAIN)
fun onMessageEvent(event: EventProbeFailure) {
    // do something with the message in event.getMessage()
}
```

The EventBus call

```kotlin
try {
    val setAdslots = SetOf(1233, 2334, 45343)
    Yieldprobe.probeWithEvents(setAdslots)
    // SDK will answer with EventProbeSuccess or EventProbeFailure
} catch (e: Exception) {
    e.printStackTrace()
    addToLog(e.toString())
}
```

<a name="guideintegration6"></a>
## 6. Show Advertisements with Adition SDK
To show an advertisement look into the file [ActivityAd.kt](/src/app/src/main/java/com/yieldlab/yieldprobe_sample_application/ActivityAd.kt) and the function `setupAditionAdView()` how to set up the `AditionView` from **Adition SDK** with targeting.

<a name="guideintegration7"></a>
## 7. Compilation and ProGuard

If you use ProGuard include these statements.

```
-keep class Yieldprobe.*
-dontwarn okio.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
```

<a name="guideintegration8"></a>
## 8. Logging and Exceptions
You can receive logging messages from the SDK over EventBus.

```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
fun onMessageEvent(event: EventProbeLog) {
    // read out event.getMessage()
}
```

The SDK reports wrong function calls (e.g. wrong order, wrong arguments, network errors) over exceptions to the user.

