
class LocationHandler(private val activity: Activity, val locationResultListener: LocationResultListener) {
    init {
        initLocationVariables()
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private fun initLocationVariables() {
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest
            .create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(0)
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        initLocationCallBack()
    }

    private fun initLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResultListener.getLocation(locationResult!!.lastLocation)
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }



    private fun promptUserToEnableLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        LocationServices
            .getSettingsClient(activity)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener { getLastKnownLocation() }
            .addOnFailureListener { e ->

                when ((e as ResolvableApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        e.startResolutionForResult(activity, 10)
                    } catch (exception: IntentSender.SendIntentException) {
                        exception.printStackTrace()
                    }

                }
            }
    }

    fun getUserLocation() {
        if (!isGooglePlayServicesAvailable(activity)) {
            return
        }

        if (!isLocationEnabled()) {
            promptUserToEnableLocation()
            return
        }
        getLastKnownLocation()
    }

    @SuppressWarnings("MissingPermission")
    private fun getLastKnownLocation(){
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val location = it.result
            if (location == null){
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }else{
                locationResultListener.getLocation(location)
            }
        }
    }

    private fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show()
            }
            return false
        }
        return true
    }
}
