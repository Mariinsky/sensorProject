## StepTracker

The project is aiming at tracking movement, route and walking distance of user . 
In addition, user can track weather forecast for next 7days in order to plan theri training. 
User can review their route and steps of last 7 days
User interface is followed design material convention and accessibility.

#### Features:
* Track steps.
* Track location.
* Calculate walking distance and kcal burned.
* Get location weather and 7days prediction.
* Track route and display on map.
* Display general data (total steps, avarage and best record).
* Graph shows detail of latest 7days.
* Persistent and updatable user data.  

#### Data persistance:
* Shared preferences
* RoomSQL
* Internal filestorage

#### Sensors:
* Step detector
* GPS

#### Rest API:
* [Openweathermap](https://openweathermap.org/)

#### Main External libraries in use:
* [RxKotlin](https://github.com/ReactiveX/RxKotlin)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Retrofit](https://square.github.io/retrofit/)
* [Gson](https://github.com/google/gson)
* [RoomSQL](https://developer.android.com/topic/libraries/architecture/room)
* [Material](https://material.io/develop/android)
* [MPandroidChart](https://github.com/PhilJay/MPAndroidChart)
* [Glide](https://github.com/bumptech/glide)

### Running the project:
* You will need to create a file keys.kt into project folder and provide your own openweathermap api key **const val WEATHER_API_KEY = YOUR API KEY**
* You can access dev menu by tapping on top bar where you can clear the db or generate random data.



 
