Twitter for Galaxy Gear
===============================

This project aims to compatible the [Twitter for Android Wear](https://github.com/saulmm/android_wear_twitter) project to also be used with smartwatchs as the _Samsung Galaxy Gear S_ based on _Tizen_, the new operating system for mobile devices from Samsung.

In the course of this harmonization I'm trying modularize the code already developed in [Twitter for Android Wear](https://github.com/saulmm/android_wear_twitter) to be reused with _Tizen_ or other operating systems for smart watches, I am focusing the project on the pattern [Model View Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter).

## Twitter for Galaxy Gear (0.1)

The release [0.1](https://github.com/saulmm/twitter-for-galaxy-gear/releases) shows how to receive tweets as notifications on a device with _Tizen_, for testing I used a _Samsung Galaxy Gear S_ as the testing device. In this scenario the connected device to the Gear, for testing a Samsung Galaxy S4 (remember, Tizen by now only works with a Samsung device), is who proactively  request the tweets, the tweets are received in the same device, and sends a notification per tweet, I am aware that this is not the desired scenario, but has been implemented as a proof of concept to test the Samsung [RichNotification SDK](http://developer.samsung.com/resources/rich-notification)

![](https://1e69a2a414c68eb9dd14c32f61f9866a4cf4d9f2.googledrive.com/host/0B62SZ3WRM2R2eUE4aHdmTnVnMGc)

## Twitter for Galaxy Gear (0.2)

The release [0.2](https://github.com/saulmm/twitter-for-galaxy-gear/releases)  shows how to build an standalone app with AccesorySDK, I think that it's much more comfortable than use RichNotifications, the development is almost identical to the client for [android wear](https://github.com/saulmm/android_wear_twitter), in the mobile device, there would be a running service and the user when pretended to consult their Timeline, open their Tizen application and send a request to their mobile device, this will query to Twitter from the mobile devices and the Timeline will be sent back to the wearable, and this do the draw properly.

![](https://googledrive.com/host/0B62SZ3WRM2R2bExhYkpacldGZEU)

## Twitter for Galaxy Gear (0.3)

The release [0.3](https://github.com/saulmm/twitter-for-galaxy-gear/releases), implements two new features, retweet & create a favorite, the user from their Galaxy Gear based on Tizen can now from their timeline mark a tweet as favorited or retweet a tweet. The whole protocol of the application has been change in the last commits, the applications is almost done, there is already a lot of 'refactorization' work to do :)

![](https://googledrive.com/host/0B62SZ3WRM2R2dFVlRmUwZEVfZUk)

## Twitter for Galaxy Gear (0.4)

The release [0.4](https://github.com/saulmm/twitter-for-galaxy-gear/releases) Is the _pre-release_ version, in this version the UI was improved, the protocol of communication with the service running on android is better thant the previous version, now the user can directly get informed about how many retweets and favorites has a tweet with a glance.

![](https://googledrive.com/host/0B62SZ3WRM2R2bkRVel90OGtGbFE)
