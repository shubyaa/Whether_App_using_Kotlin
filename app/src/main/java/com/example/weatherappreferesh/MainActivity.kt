package com.example.weatherappreferesh

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(){
    var addressData: AddressData? = null
    val CITY: String = "Mumbai"
    val API: String = "4660237d1153a2f3a904d2bc16fa184b" // Use your own API key to fetch weather
    var Lat = ""
    var Lng = ""
    var lattitude = 19.0760
    var longitude = 72.8777
    lateinit var popupMenu: PopupMenu
   

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeOnTimeBackground()

       if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        menu_button.setOnClickListener {
            popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.referesh -> {
                        Toast.makeText(this@MainActivity, "Refereshing...", Toast.LENGTH_SHORT).show()
                        weatherTask().execute()
                    }
                    R.id.location -> {
                        getNewLocation()
                    }
                }
                true
            })
            popupMenu.show()
        }
        weatherTask().execute()
    }

    private fun changeOnTimeBackground() {
        val time = SimpleDateFormat("HH", Locale.ENGLISH).format(Date())
        val time1 = time.toInt()
        val twelve = 12
        val zero = 0
        val twentythree = 23
        val seven = 7
        val nineteen = 19
        Log.i("Time", time)

        if (time1 in seven until nineteen) {
            sun.animate().translationY(-1100F).duration = 1000
            day.animate().alpha(1F).duration = 1000

            moon.isVisible = false
            night.isVisible = false
        } else if (time1 in nineteen..twentythree || time1 in (zero + 1) until seven) {
            moon.animate().translationY(-1100F).duration = 1000
            night.animate().alpha(1F).duration = 1000

            sun.isVisible = false
            day.isVisible = false
        }
    }



    private fun getNewLocation() {
        val intent = PlacePicker.IntentBuilder()
                .setLatLong(lattitude,  longitude)
                .showLatLong(true)
                .setMapZoom(12.0f)
                .hideMarkerShadow(true)
                .setMarkerDrawable(R.drawable.pin01)
                .setFabColor(R.color.colorPrimaryDark)
                .setPrimaryTextColor(R.color.places_autocomplete_prediction_primary_text)
                .setSecondaryTextColor(R.color.design_default_color_on_secondary) // Change text color of full Address
                .setBottomViewColor(R.color.textColor) // Change Address View Background Color (Default: White)
                .setMapType(MapType.NORMAL)
                .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                .hideLocationButton(true)   //Hide Location Button (Default: false)
                .disableMarkerAnimation(false)   //Disable Marker Animation (Default: false)
                .build(this)

        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                lattitude = addressData?.latitude!!
                longitude = addressData?.longitude!!
                //Log.i("Address", addressData.toString())
                weatherTask().execute()

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String {
            var response:String
            if (addressData?.latitude == null || addressData?.longitude == null){
                Lat = "19.0760"
                Lng = "72.8777"
            }else{
                Lat = addressData?.latitude.toString()
                Lng = addressData?.longitude.toString()
            }
            try{
                /*
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$API").readText(
                    Charsets.UTF_8
                )

                 */
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$Lat&lon=$Lng&appid=$API&units=metric").readText(
                        Charsets.UTF_8
                )
            }catch (e: Exception){
                response = "ERROR"
            }
            return response
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")+"N/m^2"
                val humidity = main.getString("humidity")+"m^3"
                val feels = "" + main.getLong("feels_like") +"°C"

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")+" m/sec"
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.feelsLike).text = feels

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                Log.i("Error", e.toString())
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }

}


