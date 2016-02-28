# Hacking

You need a valid pair of API key and secret to make requests to Douban API.

## Get your API key and secret

Currently Douban has [closed API key application for individuals](https://developers.douban.com/apikey/), so you have to get your own from somewhere else.

Of course I'm absolutely not recommending you to get one by [decompiling](http://www.javadecompilers.com/apk) [an old enough version of the official app](https://apkpure.com/豆瓣/com.douban.frodo?version=3.0.1&grep=OkVolley.getInstance().init).

## Fill in the credentials

Once you've got the credentials, you need to provide them to Douya. Put the following files on you SD card:

- `(Your SD card path)/Douya/API_KEY`: The content of which should be an API key for Douban.
- `(Your SD card path)/Douya/API_SECRET`: The content of which should be an API secret for Douban.

The path and file names are case sensitive.

Note that if you have started the app before these files are ready, you need to force stop and restart the app to make it reload the files.

## Get the app!

Download and install the app from [the latest release](https://github.com/DreaminginCodeZH/Douya/releases/latest).
