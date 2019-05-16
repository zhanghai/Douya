# Douya

[本文中文版 (Chinese version)](README.md)

> Douban, Yet Another.

Yet another Material Design Android app for [Douban](https://www.douban.com).

![Travis CI](https://travis-ci.org/zhanghai/Douya.svg)

<!--<a href="https://play.google.com/store/apps/details?id=me.zhanghai.android.douya" target="_blank"><img alt="Google Play" height="90" src="https://play.google.com/intl/en_US/badges/images/generic/en_badge_web_generic.png"/></a>-->

Download: [Douya Latest Release Build](https://github.com/zhanghai/Douya/releases/latest)

[Douya CI Builds](https://github.com/zhanghai/DouyaCiBuilds)

[Douya API Key](https://github.com/zhanghai/DouyaApiKey)

## Some Features

- Material Design
- [Start time optimization](https://github.com/zhanghai/MaterialColdStart)
- Lollipop shared element transition
- Screen rotation support.
- Staggered grid layout for tablet.
- Custom Tabs support.

## Preview

Images:

<p><img src="screenshot/00-main.png" width="32%" />
<img src="screenshot/01-immersive.jpg" width="32%" />
<img src="screenshot/02-gallery.jpg" width="32%" />
<img src="screenshot/03-broadcast-menu.png" width="32%" />
<img src="screenshot/04-broadcast-activity.png" width="32%" />
<img src="screenshot/05-send-comment.png" width="32%" />
<img src="screenshot/06-comment-action.png" width="32%" />
<img src="screenshot/07-settings.png" width="32%" />
<img src="screenshot/08-licenses.png" width="32%" />
<img src="screenshot/09-tablet-portrait.jpg" width="34.7%" />
<img src="screenshot/10-tablet-landscape.png" width="61.7%" /></p>

Video:

- [Vimeo](https://vimeo.com/156952508)
- [Youku](http://v.youku.com/v_show/id_XMTQyMDE5ODk0MA==.html)

## Implementation

### Data

Most data are fetched from network while some of them are cached for offline.

- Account system based on  Android `AccountManager`.
- Retrofit with custom extensions for network requests.
- Gson for data model.
- Glide for image loading.
- DiskLRUCache with custom extensions for caching.
- EventBus for state synchronization across Activities.

### UI

- Material Design implemented with AppCompat, Design, CardView and RecyclerView from support library and some customization.
- Animation implemented with shared element transition on Lollipop and above.

## Libraries created for this project

- [MaterialColdStart](https://github.com/zhanghai/MaterialColdStart)，800+ Stars
- [MaterialProgressBar](https://github.com/zhanghai/MaterialProgressBar)，500+ Stars
- [CustomTabsHelper](https://github.com/zhanghai/CustomTabsHelper)，200+ Stars
- [MaterialEditText](https://github.com/zhanghai/MaterialEditText)
- [SystemUiHelper](https://github.com/zhanghai/SystemUiHelper)

## Third party libraries

- [PhotoView](https://github.com/chrisbanes/PhotoView)
- [Glide](https://github.com/bumptech/glide)
- [Gson](https://github.com/google/gson)
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache/)
- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP/)
- [Retrofit](https://github.com/square/retrofit)
- [EventBus](https://github.com/greenrobot/EventBus)
- [CustomTabsHelper](https://github.com/zhanghai/CustomTabsHelper)
- [EffortlessPermissions](https://github.com/zhanghai/EffortlessPermissions)
- [MaterialEditText](https://github.com/zhanghai/MaterialEditText)
- [MaterialProgressBar](https://github.com/zhanghai/MaterialProgressBar)
- [SystemUiHelper](https://github.com/zhanghai/SystemUiHelper)
- [MaterialColdStart](https://github.com/zhanghai/MaterialColdStart)

## Building

You can download the APK file from [releases](https://github.com/zhanghai/Douya/releases) of this project.

For building this project yourself:

1. Create `signing.properties`:

```ini
storeFile=YOUR_STORE_FILE
storePassword=
keyAlias=
keyPassword=
```

2. Execute `./gradlew build`。

## Using

After installation, please install [Douya API Key](https://github.com/zhanghai/DouyaApiKey) to set up API key for this app.

Please don't install APKs from untrusted sources, so that you won't leak your username and password.

## License

    Copyright 2015 Zhang Hai

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
