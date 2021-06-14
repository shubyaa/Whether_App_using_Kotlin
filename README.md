# Whether App

 This is a simple weather app which helps you to know the weather details of any location you want to using google maps.

 Here, it all starts with a simple GUI, which directly consist of weather details of location, Mumbai. You will be able to see various parameters regrding to weather details such as :-

- Pressure
- Temperature
- Wind Speed
- Timings of sunrise and sunset
- Feels Like temperature

Also, depending on the time, I have added a small animation in GUI. When there is daytime, it will show **sun rising**, and when ther is night, **moon** will be appeared.

A simple demonstration of the App is here below:

![weather app](https://user-images.githubusercontent.com/67822091/121865098-3449e080-cd1b-11eb-8d0f-b124f291c27b.gif)


For getting wether details, I have used [OpenWheatherMapApi](https://openweathermap.org/api "open"). You can learn to generate and use a new key for your project [here](https://openweathermap.org/appid).

To make this weather app more interesting, I have added `Place Picker` option so that we can get to know weather details of any desired location.

As this project is completely written in [Kotlin](https://kotlinlang.org/), there is no predefined library for Place Picker Activity, hence we have to use an external library!!

>Github is a good source of information actually !!

All thanks to [suchoX](https://github.com/suchoX/PlacePicker), I have used this git dependency in my project which made a lot things easier.

Make sure that you have your own Google-Maps Api as we are using google maps in our project. To know more about google maps, click [me](https://developers.google.com/maps/documentation/android-sdk/overview).

One thing to mention, Google Maps have search bar option to search places on google maps, for that you need to enable [places](https://developers.google.com/maps/documentation/places/android-sdk/start#maps_places_get_started-kotlin) for your api key, but it later came to know that it will not allow us to use it for free, so I have not used ny search bar option in this project.

To make use of the google maps for Place Picker Activity, just add your Google Maps Api key in your Manifest file under **meta-data** tag.

```
    <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY" />
```

After doing this, it might work for you.
This was all about a simple weather app using Kotlin.
