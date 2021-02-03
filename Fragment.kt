//top annotation
@RuntimePermissions
class SplashFragmemt:: BaseContainerFragment<FragmentSplashFragmemtBinding>(),
    LocationResultListener{
    
    

//above onCreateView
      private val locationHandler: LocationHandler by lazy {
        LocationHandler(requireActivity(), this)
    }


//in-between onViewCreate
 fetchLocationWithPermissionCheck()
    
    }
    
    //outside onCreateView Permission method annotations
     @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun fetchLocation() {
        locationHandler.getUserLocation()
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun showRationaleForLocation(request: PermissionRequest) {
        request.proceed()
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onDenied() {

    }

    @OnNeverAskAgain(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onNeverAskAgain() {
        requireActivity().explain()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
    
    //getLocation InterfaceResult
      override fun getLocation(location: Location) {
        Timber.e(location.latitude.toString())
        Timber.e(location.longitude.toString())
        
        }
    
