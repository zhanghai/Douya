# Hacking

You need a valid pair of API key and secret to make requests to Douban API.

## Get your API key and secret

Currently Douban has [closed API key application for individuals](https://developers.douban.com/apikey/), so you have to get your own from somewhere else.

Of course I'm not recommending you to [decompile](http://www.javadecompilers.com/apk) [the official app](http://www.douban.com/doubanapp/redirect?download=Android) for this.

## Fill in the credentials

Once you've got the credentials, you need to fill them into the constant fields in [ApiCredential.java](app/src/main/java/me/zhanghai/android/douya/network/api/ApiCredential.java).

## (Optional) Ignore the change in git

```bash
git update-index --assume-unchanged app/src/main/java/me/zhanghai/android/douya/network/api/ApiCredential.java
```

## Compile your own APK

Open this project in Android Studio, and you are now ready to build your own!
