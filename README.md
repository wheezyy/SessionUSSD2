# cordova-plugin-VoIpUSSD
A cordova plugin version of VoIpUSSD: https://github.com/romellfudi/VoIpUSSD

## Installation

```
cordova plugin add https://github.com/chogis/SessionUSSD2.git
```


## Configuration
### Copy *AndroidManifest.xml* file or use sections labelled  <!-- USE HERE  -->

* Add service (see sample file in list):
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
* Add below dependencies (see AndroidManifest.xml sample file):
```
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
### Copy  *ussd_service.xml* to *res/xml/* folder

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
