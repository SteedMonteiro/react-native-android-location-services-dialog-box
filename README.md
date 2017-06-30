## React Native Android Location Services Dialog Box
<img width="274px" align="right" src="https://raw.githubusercontent.com/webyonet/react-native-android-location-services-dialog-box/master/demo.gif" />

A react-native component for turn on the dialog box from android location services

[![npm version](https://badge.fury.io/js/react-native-android-location-services-dialog-box.svg)](https://badge.fury.io/js/react-native-android-location-services-dialog-box)

### Installation

#### Mostly automatic installation (recommended)

1. `npm install react-native-android-location-services-dialog-box --save`
2. `react-native link react-native-android-location-services-dialog-box`

#### Manual Installation

##### Android

1. `npm install react-native-android-location-services-dialog-box --save`
2. Make the following additions to the given files:

**android/settings.gradle**

```
include ':react-native-android-location-services-dialog-box'
project(':react-native-android-location-services-dialog-box').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-android-location-services-dialog-box/android')
```

**android/app/build.gradle**

```
dependencies {
   ...
   compile project(':react-native-android-location-services-dialog-box')
}
```

**MainApplication.java**

On top, where imports are:
```java
import com.showlocationservicesdialogbox.LocationServicesDialogBoxPackage;
```

Under `protected List<ReactPackage> getPackages() {`:  
```java
  return Arrays.<ReactPackage>asList(
    new MainReactPackage(),
    new LocationServicesDialogBoxPackage() // <== this
  );
```

### Example

```javascript
import {
  Alert,
  Linking,
  NativeModules,
  Platform,
  AppState
} from "react-native";
import Permissions from "react-native-permissions";
import i18n from "react-native-i18n";
import LocationServicesDialogBox from "react-native-android-location-services-dialog-box";

export default class gps {
  constructor() {
    this.watchID = null;
    this.onAppStateChange = this._onAppStateChange.bind(this);
  }
  /**
     * GPS switch off
     */
  turnOffGpsWatch() {
    console.log("turnOffGpsWatch");

    if (this.watchID == null) return;
    navigator.geolocation.clearWatch(this.watchID);
  }

  _onAppStateChange(state) {
    console.log("_onAppStateChange", state);
    if (state == "active") {
      this.requestGps(this.success, this.failed);
      AppState.removeEventListener("change", this.onAppStateChange);
    }
  }

  //open location setting
  openLocationSettings(success, failed) {
    let onAppStateChange;
    this.success = success;
    this.failed = failed;
    AppState.addEventListener("change", this.onAppStateChange);

    if (Platform.OS == "ios") Linking.openURL("app-settings:");
    else if (LocationServicesDialogBox != undefined)
      LocationServicesDialogBox.openLocationSetting();
  }

  /**
     * Request GPS permission if permission not granted, works for both android/ios
     * refer to: react-native-permissions
     */
  requestGps(success, failed) {
    if (Platform.OS == "android" && LocationServicesDialogBox != undefined) {
      LocationServicesDialogBox.locationServicesIsEnable()
        .then(() => {
          this.turnOnGps(success, failed);
        })
        .catch(() => {
          this.enableGpsDialog(success, failed);
        });
    } else {
      this.turnOnGps(success, failed);
    }
  }

  /**
     * Request GPS permission if permission not granted, works for both android/ios
     * refer to: react-native-permissions
     */
  checkGps(success, failed) {
    Permissions.requestPermission("location")
      .then(response => {
        //['authorized', 'denied', 'restricted', 'undetermined']
        console.log("checkGps", response);
        if (response == "authorized") {
          this.requestGps(success, failed);
        }
      })
      .catch(error => {
        console.log("turnOnGps error", error);
        failed();
      });
  }
  /**
     * GPS switch on
     */
  turnOnGps(success, failed) {
    this.watchID = navigator.geolocation.getCurrentPosition(
      position => {
        console.log("turnOnGps position", position);
        console.log("getCurrentPosition", position.coords);
        success(position.coords);

        this.watchID = navigator.geolocation.watchPosition(position => {
          console.log("watchPosition", position);
          success(position.coords);
        });
      },
      error => {
        console.log("turnOnGps error", error);

        if (error.code === 1) {
          this.enableGpsDialog(success, failed);
        } else {
          failed();
        }
      },
      {
        //enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 10000,
        distanceFilter: 100
      }
    );
  }

  /**
     * Open Gps dialog of system settings
     * @param {null}
     */
  enableGpsDialog(success, failed) {
    Alert.alert(
      i18n.t("Turn on your location to use this service"),
      "",
      [
        { text: i18n.t("Close"), onPress: () => failed() },

        {
          text: i18n.t("Settings"),
          onPress: () => this.openLocationSettings(success, failed)
        }
      ],
      { cancelable: false }
    );
  }
}


```

### Methods

| Name                               | Return             |
|------------------------------------|--------------------|
|`locationServicesIsEnable`    | Promise            |
|`openLocationSetting`    |             |


