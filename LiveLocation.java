
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    private LatLng sourceLatLng;
    private LatLng destLatLng;

    private double OldLat = 0.0, OldLng = 0.0;



public void liveNavigation(Double lat, Double lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);

        if (lat != null && lng != null) {
            if (OldLat == 0.0) {
                OldLat = lat;
                OldLng = lng;
            }
            Location targetLocation = new Location("providerlocation");//provider name is unnecessary
            targetLocation.setLatitude(lat);//your coords of course
            targetLocation.setLongitude(lng);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .anchor(Float.parseFloat(String.valueOf(0.5)), Float.valueOf(String.valueOf(0.5)))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker));
            if (providerMarker != null) {
                LatLng oldPosition = new LatLng(OldLat, OldLng);
                LatLng newPosition = new LatLng(lat, lng);
                double bearingValue = bearingBetweenLocations(oldPosition, newPosition);
                if (bearingValue != 0.0) {
                    animateMarker(mMap, new LatLng(lat, lng), providerMarker, Float.valueOf(String.valueOf(bearingValue)), false);
                }
            } else {
                providerMarker = mMap.addMarker(markerOptions);
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14f));

            OldLat = lat;
            OldLng = lng;
        }
    }




    public static double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }



    //car Motion Animation
    public static void animateMarker(GoogleMap mMap, final LatLng destination, final Marker marker, final Float toRotation, final Boolean hideMarker) {

        try {
            if (!isMarkerRotating) {

                Log.v("OnLocationChanged", "Marker Rotate: false");

                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                Projection proj = mMap.getProjection();
                Point startPoint = proj.toScreenLocation(marker.getPosition());
                final LatLng startLatLng = proj.fromScreenLocation(startPoint);
                final long duration = 2800;
                final long durationCarRotate = duration;
                final float startRotation = marker.getRotation();

                final Interpolator interpolator = new LinearInterpolator();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isMarkerRotating = true;
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = interpolator.getInterpolation((float) elapsed / duration);


                        double lng = t * destination.longitude + (1 - t) * startLatLng.longitude;
                        double lat = t * destination.latitude + (1 - t) * startLatLng.latitude;

                        float t2 = interpolator.getInterpolation((float) elapsed / durationCarRotate);
                        float rot = t2 * toRotation + (1 - t2) * startRotation;

                        marker.setPosition(new LatLng(lat, lng));

                        float angle = -rot > 180 ? rot / 2 : rot;
                        marker.setRotation(angle);
                        if (t < 1.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        } else {
                            isMarkerRotating = false;
                            if (hideMarker) {
                                marker.setVisible(false);
                            } else {
                                marker.setVisible(true);
                            }
                        }
                    }
                });
            } else {

                Log.v("OnLocationChanged", "Marker Rotate: true");

            }
        } catch (Exception e) {

        }
    }

