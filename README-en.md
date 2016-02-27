# Douya

[本文中文版 (Chinese version)](README.md)

> Douban, Yet Another.

Yet another Material Design Android app for [Douban](https://www.douban.com).

## Some Features

- Material Design
- [Start time optimization](https://github.com/DreaminginCodeZH/MaterialColdStart)
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
- Volley with custom extensions for network requests.
- Gson for data model.
- Glide for image loading.
- DiskLRUCache with custom extensions for caching.
- EventBus for state synchronization across Activities.

### UI

- Material Design implemented with AppCompat, Design, CardView and RecyclerView from support library and some customization.
- Animation implemented with shared element transition on Lollipop and above.

## Libraries created for this project

- [MaterialColdStart](https://github.com/DreaminginCodeZH/MaterialColdStart)，800+ Stars
- [MaterialProgressBar](https://github.com/DreaminginCodeZH/MaterialProgressBar)，500+ Stars
- [CustomTabsHelper](https://github.com/DreaminginCodeZH/CustomTabsHelper)，200+ Stars
- [MaterialEditText](https://github.com/DreaminginCodeZH/MaterialEditText)
- [SystemUiHelper](https://github.com/DreaminginCodeZH/SystemUiHelper)

## Third party libraries

- [PhotoView](https://github.com/chrisbanes/PhotoView)
- [Glide](https://github.com/bumptech/glide)
- [Gson](https://github.com/google/gson)
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache/)
- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP/)
- [Volley](https://github.com/mcxiaoke/android-volley)
- [EventBus](https://github.com/greenrobot/EventBus)
- [CustomTabsHelper](https://github.com/DreaminginCodeZH/CustomTabsHelper)
- [MaterialEditText](https://github.com/DreaminginCodeZH/MaterialEditText)
- [MaterialProgressBar](https://github.com/DreaminginCodeZH/MaterialProgressBar)
- [SystemUiHelper](https://github.com/DreaminginCodeZH/SystemUiHelper)
- [MaterialColdStart](https://github.com/DreaminginCodeZH/MaterialColdStart)

## Using

Due to the closing up of [API key application for individual developers](http://developers.douban.com/apikey/), this app is not likely to be available to public in APK form. So I decided to make it open source and allow people to do more [hacking](http://www.catb.org/jargon/html/H/hacker.html) on it.

You can refer to [HACKING.md](HACKING.md) for providing API credentials and running the app.

Please don't install APKs from untrusted sources, so that you won't leak your username and password.
