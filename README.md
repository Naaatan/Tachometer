# Tachometer Android
Indicator like tachometer View Library for Android

## GIF

Sample Preview

<img src="https://github.com/Naaatan/Tachometer/blob/master/image/demo.gif" width="480"/>


## Setup

Step 1. Add Jitpack repository to your project **build.gradle**
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add dependency to your app **build.gradle**
```groovy
dependencies {
    implementation 'com.github.Naaatan:Tachometer:1.0.2'
}
```

## Layout

```xml
<nay.lib.Tachometer
    android:id="@+id/tachometer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:max="100"
    app:min="0"
    app:borderSize="8dp"
    app:borderColor="#4B3453"
    app:fillColor="#FF01D9C2"
    app:metricText="km/h"
    app:metricTextSize="20sp"
    app:valueTextSize="60sp"
    app:textColor="#FF01D9C2"
    app:textGap="24dp"
    app:textOverColor="#F44336"
    app:textUnderColor="#03A9F4"
    app:tick_split_major="7"
    app:tick_split_minor="5"/>
```

## Usage

```kotlin
tachometer.setMeterValue(v = value, d = 1000L) {
    // called when the animation ends
    // your code...
}
```

### Example
Look at the [sample code](https://github.com/Naaatan/Tachometer/tree/master/app)
