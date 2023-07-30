# androidx-compose-material3-pullrefresh

This is a simple wrapper for Jetpack Compose Material3's `PullRefreshIndicator`. The official implementation is not compatible with Material 3 theme.

Official documentation
see [here](https://developer.android.com/reference/kotlin/androidx/compose/material/pullrefresh/package-summary#(androidx.compose.ui.Modifier).pullRefresh(androidx.compose.material.pullrefresh.PullRefreshState,kotlin.Boolean)).

Note that you should change all package name from `androidx.compose.material.pullrefresh` into `androidx.compose.material3.pullrefresh`.

## Usage

This library only supports using composite build to import to your project because it will be archived when the official library is released.

In your root project's `settings.gradle.kts` file, add the following:

```kotlin
includeBuild("<androidx-compose-material3-pullrefresh-path>") {
    dependencySubstitution {
        substitute(module("me.omico.lux:lux-androidx-compose-material3-pullrefresh")).using(project(":library"))
    }
}
```

In your app module's `build.gradle.kts` file, add the following:

```kotlin
dependencies {
    implementation("me.omico.lux:lux-androidx-compose-material3-pullrefresh")
}
```

## License

```text
Copyright 2022 The Android Open Source Project
Copyright 2022-2023 Omico

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
