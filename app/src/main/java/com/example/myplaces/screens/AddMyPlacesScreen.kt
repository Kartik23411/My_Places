@file:Suppress("DEPRECATION")

package com.example.myplaces.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myplaces.R
import com.example.myplaces.viewModel.AddPlacesViewModel
import com.example.myplaces.data.DataBaseHandler
import com.example.myplaces.data.MyPlace
import com.example.myplaces.viewModel.GetAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMyPlacesScreen(
    onSave:()-> Unit,
    onNavBack:() -> Unit,
    viewModel: AddPlacesViewModel = viewModel()
){
    val context = LocalContext.current
    var title by remember{ mutableStateOf("") }
    var description by remember{ mutableStateOf("") }
    var date by remember{ mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf(0.0) }
    var latitude by remember { mutableStateOf(0.0) }
    val controller = LocalSoftwareKeyboardController.current
    var showDialog by remember { mutableStateOf(false) }

    // location auto complete and get current location
    Places.initialize(context.applicationContext, stringResource(R.string.google_map_api_key))
    val placesClient = Places.createClient(context)

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {result ->
            if (result.resultCode == Activity.RESULT_OK){
                val place = Autocomplete.getPlaceFromIntent(result.data!!)
                location = place.address ?: ""
                longitude = place.latLng!!.longitude
                latitude = place.latLng!!.latitude
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(result.data!!)
            }else {
                Log.d("Autocomplete", "Autocomplete was canceled or failed")
            }
        }
    )
    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsGranted ->
        permissionsGranted.entries.forEach { entry ->
            val permission = entry.key
            val isGranted = entry.value
            if (!isGranted) {
                Toast.makeText(context, "You have not provided the required permissions, now provide them in settings to use this feature", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkAndRequestLocationPermission(onGranted: () -> Unit) {
        val hasLocationPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasLocationPermission) {
            onGranted()
        } else {
            // Request missing permissions
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    lateinit var fusedLocationClient: FusedLocationProviderClient
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationCallback = object: LocationCallback(){
         override fun onLocationResult(locationResult: LocationResult) {  // removed override
             val lastLocation: Location? = locationResult.lastLocation
             if (lastLocation != null) {
                 latitude = lastLocation.latitude
                 longitude = lastLocation.longitude

                 val addressTask = GetAddress(context, latitude, longitude)
                 addressTask.setAdressListener(object : GetAddress.AddressListener{
                     override fun onAddressFound(address: String?) {
                         location = address!!
                     }

                     override fun onError() {
                         Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                     }
                 })
                 addressTask.getAddress()
             } else {
                 Toast.makeText(context, "null last location", Toast.LENGTH_SHORT).show()
             }
        }
    }

    fun createLocationRequest(){
        val locationRequest = LocationRequest()
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.numUpdates = 1

        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }



    // camera related permissions
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    // Function to check permissions
    fun checkAndRequestPermissions(onGranted: () -> Unit) {
        val hasCameraPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//        val hasStoragePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission) {
            onGranted()
        } else {
            // Request missing permissions
            requestPermissionLauncher.launch(permissions)
        }
    }

    // for camera and gallery
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val captureImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {bitmap: Bitmap? ->
        capturedImageBitmap = bitmap
        imageUri = viewModel.saveImageToGallery(context, capturedImageBitmap!!)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri:Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)  // Convert URI to Bitmap
            imageUri = viewModel.saveImageToGallery(context,bitmap) // Set the image URI to display
        }
    }

    BackHandler {  }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(Color.Cyan),
                navigationIcon = {
                    IconButton(onClick = {onNavBack()}) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = "Add Place")
                }
            )
        }
    ) {

       Column(modifier = Modifier.padding(it)) {

           if (showDialog){
               CustomAlertDialog(
                   onDismiss =  { showDialog = false },
                   onCameraChoose = {
                       checkAndRequestPermissions(
                           onGranted = {
                               captureImageLauncher.launch()
                           }
                       )
                       showDialog = false
                   },
                   onGalleryChoose = {
                       checkAndRequestPermissions(
                           onGranted = {
                               imagePicker.launch("image/*")
                           }
                       )
                       showDialog = false
                   }
               )
           }

            CustomTextField(
                "Title",
                title,
                onValueChange = { title = it },
                isIconRequired = false,
                onClick = {}
            )
            CustomTextField(
                "Description",
                description,
                onValueChange = { description = it },
                isIconRequired = false,
                onClick = {}
            )
            CustomTextField(
                "Date",
                date,
                onValueChange = { },
                isIconRequired = true,
                onClick = {
                    viewModel.showDatePicker(context, onDateSelected = {date = it})
                }
            )
            CustomTextField(
                "Location",
                location,
                onValueChange = { location = it },
                isIconRequired = true,
                onClick = {
                    launcher.launch(intent)
                }
            )

           OutlinedButton(
               onClick = {
                    checkAndRequestLocationPermission(onGranted =  {
                        createLocationRequest()
                    })
               },
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(8.dp)
                   .heightIn(35.dp),
               shape = RoundedCornerShape(25) ,
                colors = ButtonDefaults.buttonColors(Color.White),
                border = BorderStroke(1.dp, Color.Blue)
           ) {
                Text(text = "SELECT CURRENT LOCATION", color = Color.Black)
           }

           Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (capturedImageBitmap != null) {
                    Image(
                        modifier = Modifier
                            .height(150.dp)
                            .weight(1f),
                        bitmap = capturedImageBitmap!!.asImageBitmap(),
                        contentDescription = null
                    )
                } else if (imageUri != null) {
                    Image(
                        modifier = Modifier
                            .height(150.dp)
                            .weight(1f),
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .height(150.dp)
                            .weight(1f),
                        painter = painterResource(R.drawable.istockphoto_1021471354_612x612),
                        contentDescription = null
                    )
                }
                Button(
                    shape = RoundedCornerShape(0),
                    colors = ButtonDefaults.buttonColors(Color(0xFF9F9F9F)),
                    onClick = {
                        showDialog = true
                    }
                ) {
                    Text("Add Photo")
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(15),
                colors = ButtonDefaults.buttonColors(Color(0xFF664FA3)),
                onClick = {
                    /* TODO add the empty or null checking functionality */
                    val myPlace = MyPlace(
                        0,
                        title,
                        imageUri.toString(),
                        description,
                        date,
                        location,
                        latitude,
                        longitude
                    )
                    if (title.isNotBlank() && description.isNotBlank() && date.isNotBlank() && location.isNotBlank() && imageUri.toString().isNotBlank()) {
                        val dbHandler = DataBaseHandler(context)

                        val addMyPlace = dbHandler.addMyPlaces(myPlace)

                        if (addMyPlace > 0) {
                            Toast.makeText(context, "Added ", Toast.LENGTH_SHORT).show()
                        }
                        onSave()
                        title = ""
                        description = ""
                        date = ""
                        location = ""
                        imageUri = null
                    }else{
                        when{
                            title.isBlank() -> {Toast.makeText(context, "Please enter the title", Toast.LENGTH_SHORT).show()}
                            description.isBlank() -> {Toast.makeText(context, "Please enter the description", Toast.LENGTH_SHORT).show()}
                            date.isBlank() -> {Toast.makeText(context, "Please select the date", Toast.LENGTH_SHORT).show()}
                            location.isBlank() -> {Toast.makeText(context, "Please enter the location", Toast.LENGTH_SHORT).show()}
                            imageUri.toString().isBlank() -> {Toast.makeText(context, "Please select or click an image", Toast.LENGTH_SHORT).show()}
                        }
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun CustomTextField(
    type:String,
    value:String,
    onValueChange: (String) -> Unit,
    isIconRequired:Boolean,
    onClick:  ()->Unit
){
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .heightIn(35.dp),
        placeholder = { Text(text = type) },
        singleLine = true,
        shape = RoundedCornerShape(15),
        trailingIcon = {
            if (isIconRequired)
            TextButton(onClick = { onClick() }) {
                Text("Set")
            }
        }
    )
}

@Composable
fun CustomAlertDialog(
    onDismiss:() -> Unit,
    onCameraChoose:() -> Unit,
    onGalleryChoose:() ->Unit
){
   AlertDialog(
       containerColor = Color.White,
       shape = RoundedCornerShape(0),
       onDismissRequest = {onDismiss()},
       confirmButton = {},
       modifier = Modifier
           .padding(horizontal = 8.dp, vertical = 8.dp)
           .background(Color.White),
       text = {
           Column(
               modifier = Modifier,
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.Start
           ) {
                // camera button
               TextButton(
                   onClick = {onCameraChoose()}
               ) {
                   Text("Take a photo from the camera", color = Color.Black, fontSize = 14.sp)
               }
                // gallery picker button
                TextButton(
                    onClick = {onGalleryChoose()}
                ) {
                    Text("Select from the gallery", color = Color.Black, fontSize = 14.sp)
                }
           }
       }
   )
}


@Preview(showBackground = true)
@Composable
fun Previe(){
    val viewModel: AddPlacesViewModel = viewModel()
//    CustomAlertDialog ({ },{},{})
    AddMyPlacesScreen({},{}, viewModel)
//    CustomTextField("K") { }
}