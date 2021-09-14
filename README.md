# cordova-plugin-VoIpUSSD
A cordova plugin version of VoIpUSSD: https://github.com/romellfudi/VoIpUSSD

## Installation

```
cordova plugin add https://github.com/chogis/SessionUSSD2.git
```


## Configuration
### On *AndroidManifest.xml* file
* Add service:
```
    <service
        android:name="com.ramymokako.plugin.ussd.android.USSDService"
        android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
        <intent-filter>
            <action android:name="android.accessibilityservice.AccessibilityService" />
        </intent-filter>
        <meta-data
            android:name="android.accessibilityservice"
            android:resource="@xml/ussd_service" />
    </service>

```
* Add below dependencies:
```
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
### On *res/xml/* folder
* Create empty **ussd_service.xml** file and insert bellow:
```
<?xml version="1.0" encoding="utf-8"?>
<!--
  ~ Copyright (c) 2020. BoostTag E.I.R.L. Romell D.Z.
  ~ All rights reserved
  ~ porfile.romellfudi.com
  -->

<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes
        ="typeWindowStateChanged"
    android:packageNames="com.android.phone"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault"
    android:canRetrieveWindowContent="true"
    android:description="@string/accessibility_service_description"
    android:notificationTimeout="0"/>
    <!--|typeViewTextChanged-->
```
## Usage
```
window.plugins.voIpUSSD.show('*105#', function (data) {
   console.log('USSD Success: ' + data);
}, function (err) {
   console.log('USSD Error: ' + err);
});
```
## Authors

* **Chogis Cho** - *Initial work* - [Romell Domínguez](https://github.com/romellfudi/VoIpUSSD/#by-romell-dominguez)
