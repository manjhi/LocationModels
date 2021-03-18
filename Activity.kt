 //bearing
    private var _radiusEarthMiles: Double = 3959.0
    private var _m2km: Double = 1.60934
    private var _toRad: Double = Math.PI / 180

    private var _centralAngle: Double = 0.0
    private var Lat: Double = 0.0
    private var Lan: Double = 0.0

    private var drivers: Marker? = null

////
 if (drivers != null) {
                            RotateTheMarker(
                                drivers,
                                preLat,
                                preLng,
                                arg1.getStringExtra("lat").toDouble(),
                                arg1.getStringExtra("lng").toDouble()
                            )
                        }


///////////////////////////
 private fun RotateTheMarker(
        drivers: Marker?,
        preLat: Double,
        preLng: Double,
        newLAT: Double,
        newLNG: Double
    ) {
        var distanceBetweenPoints = CalculationByDistance(preLat, preLng, newLAT, newLNG)

        if (distanceBetweenPoints > 5) {
            if (preLat != 0.0 && preLng != 0.0) {
                var oldPosition = LatLng(preLat, preLng)
                var newPosition = LatLng(newLAT, newLNG)

                var bearingValue = bearingBetweenLocations(oldPosition, newPosition)

                if (bearingValue != 0.0) {
                    rotateMarker(drivers, bearingValue.toFloat(), newPosition)
                }
            }
        }
    }

    private fun rotateMarker(drivers: Marker?, toRotation: Float, mOrigin: LatLng) {
        animateMarker(mMap, drivers, mOrigin, false, toRotation)
    }

    private fun animateMarker(
        map: GoogleMap,
        marker: Marker?,
        toPosition: LatLng,
        hideMarker: Boolean,
        toRotation: Float
    ) {
        if (!isMarkerRotating) {
            val handler = Handler()
            val start: Long = SystemClock.uptimeMillis()
            val proj: Projection = map.getProjection()
            val startPoint: Point = proj.toScreenLocation(marker?.getPosition())
            val startLatLng: LatLng = proj.fromScreenLocation(startPoint)
            val duration: Long = 2800
            val startRotation: Float = marker?.getRotation()!!

            val interpolator: Interpolator = LinearInterpolator()

            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed: Long = SystemClock.uptimeMillis() - start
                    val t: Float =
                        interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                    val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude

                    val t2: Float =
                        interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t2 * toRotation + (1 - t2) * startRotation
                    Log.v("RotateVal", "Start rot: $startRotation")
                    Log.v("RotateVal", "From rot: " + java.lang.String.valueOf(toRotation))
                    Log.v("RotateVal", "Rot rot: $rot")
                    // Log.v("RotateVal","Difference: "+String.valueOf(x));
                    marker.setPosition(LatLng(lat, lng))
                    marker.setRotation(if (-rot > 180) rot / 2 else rot)
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        isMarkerRotating = false
                        if (hideMarker) {
                            marker?.setVisible(false)
                        } else {
                            marker?.setVisible(true)
                        }
                    }
                }

            })
        }
    }

    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        var PI = 3.14159
        var lat1 = latLng1.latitude * PI / 180
        var long1 = latLng1.longitude * PI / 180
        var lat2 = latLng2.latitude * PI / 180
        var long2 = latLng2.longitude * PI / 180
        var dLon = (long2 - long1)
        var y = Math.sin(dLon) * Math.cos(lat2)
        var x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }

    private fun CalculationByDistance(
        preLat: Double,
        preLng: Double,
        newLAT: Double,
        newLNG: Double
    ): Int {

        var startPoint = Location("locationA")
        startPoint.setLatitude(preLat)
        startPoint.setLongitude(preLng)

        var endPoint = Location("locationB")
        endPoint.setLatitude(newLAT)
        endPoint.setLongitude(newLNG)
        var distance = startPoint.distanceTo(endPoint)
        return distance.toInt()
    }


    private fun animateCarOnMap(latLngs: java.util.ArrayList<LatLng>) {
        var builder = LatLngBounds.builder()
        for (i in 0 until latLngs.size) {
            builder.include(latLngs[i])
        }

        var bounds = builder.build()
        var mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2)
        mMap.animateCamera(mCameraUpdate)

        if (drivers != null) {
            drivers?.remove()
        }

        drivers = mMap.addMarker(
            MarkerOptions().position(latLngs[0]).flat(true).icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.navigation
                )
            )
        )
        drivers?.position = latLngs[0]
        var valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            var v = it.animatedFraction
            var lng = v * latLngs.get(1).longitude + (1 - v) * latLngs.get(0).longitude
            var lat = v * latLngs.get(1).latitude + (1 - v) * latLngs.get(0).latitude

            var newPos = LatLng(lat, lng)
            drivers?.position = newPos
            drivers?.setAnchor(0.5f, 0.5f)
            drivers?.rotation = getBearing(latLngs.get(0), newPos)

            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder().target(
                        newPos
                    ).zoom(15.0f).build()
                )
            )
        }

        valueAnimator.start()
    }

    private fun getBearing(begin: LatLng, end: LatLng): Float {
        var lat = abs(begin.latitude - end.latitude)
        var lng = abs(begin.longitude - end.longitude)

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat))).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270).toFloat()
        return -1f
    }

    private fun HasMoved(newLat: Double, newLan: Double): Boolean {
        var significantDistance = 15.0
        var currentDistance: Double = DistanceMilesSEP(Lat, Lan, newLat, newLan)
        currentDistance *= _m2km * 1000

        return if (currentDistance < significantDistance) {
            false
        } else {
            Lat = newLat
            Lan = newLan
            true
        }
    }

    private fun DistanceMilesSEP(lat: Double, lan: Double, newLat: Double, newLan: Double): Double {
        try {

            var _radLat1 = lat * _toRad
            var _radLat2 = newLat * _toRad
            var _dLat = (_radLat2 - _radLat1)
            var _dLon = (newLan - lan) * _toRad

            var _a = (_dLon) * Math.cos((_radLat1 + _radLat2) / 2)

            _centralAngle = Math.sqrt(_a * _a + _dLat * _dLat)

        } catch (e: Exception) {

        }
        return _radiusEarthMiles * _centralAngle
    }
