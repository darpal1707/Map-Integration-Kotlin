package com.example.kotlinmapintegration

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.jar.Manifest

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermissions()
        LoadPockemon()
    }
    var ACCESS_LOCATION=123
    fun checkPermissions(){

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.
                    checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESS_LOCATION)
                return
            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){

        var myLocation= MylocationListener()
        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var mythread=myThread()
        mythread.start()
        Toast.makeText(this,"User location " ,Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {

        when(requestCode){
            ACCESS_LOCATION->{

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    var location:Location?=null

    //Get user location
    inner class MylocationListener:LocationListener{

        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation:Location?=null
    inner class myThread : Thread {

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.longitude=0.0
        }

        override fun run() {
            super.run()
            while (true){
                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location

                    runOnUiThread {

                        mMap!!.clear()

                        // show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me"))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        // show pockemons

                        for(i in 0..listCars.size-1){

                            var newCar=listCars[i]

                            if(newCar.IsCatch==false){

                                val pockemonLoc = LatLng(newCar.location!!.latitude, newCar.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                    .position(pockemonLoc)
                                    .title(newCar.name!!)
                                    .icon(BitmapDescriptorFactory.fromResource(newCar.image!!)))


                                if (location!!.distanceTo(newCar.location)<2){
                                    newCar.IsCatch=true
                                    listCars[i]=newCar
                                    playerPower+=newCar.power!!
                                    Toast.makeText(applicationContext,
                                        "Your car is on the way to pick you! " + playerPower,
                                        Toast.LENGTH_LONG).show()

                                }

                            }
                        }
                    }

                    Thread.sleep(1000)

                }catch (ex:Exception){}

            }
        }
    }

    var playerPower=0.0
    var listCars=ArrayList<Car>()

    fun  LoadPockemon(){


        listCars.add(Car(R.drawable.icons8_car_24,
            "Sedan", "Can fit max 4 people", 4.0, 37.7388065, -121.928364))
        listCars.add(Car(R.drawable.icons8_car_24,
            "Pool", "Can fit upto 2 peopel", 2.0, 37.7394076, -121.9313977))
        listCars.add(Car(R.drawable.icons8_car_24,
            "SUV", "Can fit max 6 people", 6.0, 37.7415178, -121.9318122))

    }
}
