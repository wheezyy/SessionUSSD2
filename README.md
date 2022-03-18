# cordova-plugin-SessionUSSD2
A cordova plugin to handle USSD menus options.


## Installation

```
cordova plugin add https://github.com/chogis/SessionUSSD2.git
```


## Configuration
### Copy *AndroidManifest.xml* file or use sections labelled

```
<!-- USE HERE  -->
        <service android:name="com.ramymokako.plugin.ussd.android.USSDService" android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data android:name="android.accessibilityservice" android:resource="@xml/ussd_service" />
        </service>
<!-- END HERE -->

<!-- USE HERE  -->
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!-- END HERE -->

```

### Copy  *ussd_service.xml* to *res/xml/* folder

## Usage
```
const ussdMenuString = "*105#,1,2,3,4"
try {
  const menuResponses = await window.plugins.voIpUSSD.show(ussdMenuString)
} catch (error) {
  console.log(error)
}
```
## Authors

* **Chogis Cho**
* [Romell Domínguez](https://github.com/romellfudi/VoIpUSSD/) - *VoIpUSSD*
* [Ramy Mokako](https://github.com/rmxakalogistik/cordova-plugin-VoIpUSSD) - *VoIpUSSD cordova plugin*

