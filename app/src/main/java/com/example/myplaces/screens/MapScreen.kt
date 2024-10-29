package com.example.myplaces.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myplaces.viewModel.HomeScreenViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Map(
    id:Int,
    viewModel: HomeScreenViewModel = viewModel()
) {

    val context = LocalContext.current
    val place by viewModel.place.observeAsState()
    LaunchedEffect(id) {
        viewModel.getPlaceById(context, id)
    }

    place?.let {

    // map variables
    val userLocation = remember {
        mutableStateOf(LatLng(it.latitude, it.longitude))
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 14f)
    }
    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings(
            compassEnabled = true,
            indoorLevelPickerEnabled = true,
            myLocationButtonEnabled = true,
            mapToolbarEnabled = true,
            zoomGesturesEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            scrollGesturesEnabledDuringRotateOrZoom = true,
            tiltGesturesEnabled = true,
        ))
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(
            mapType = MapType.NORMAL,
        ))
    }
    val isMapLoaded = remember {
        mutableStateOf(false)
    }
    var showinfoWindow by remember {
        mutableStateOf(true)
    }

    GoogleMap(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings,
        onMapLoaded = { isMapLoaded.value = true}
    ) {
        MarkerInfoWindow(
            state = MarkerState(userLocation.value),
            draggable = true,
            onClick = {
                if (showinfoWindow) {
                    MarkerState(userLocation.value).showInfoWindow()
                }
                else {
                    MarkerState(userLocation.value).hideInfoWindow()
                }
                showinfoWindow = !showinfoWindow
                return@MarkerInfoWindow false
            },
            flat = true,
            title = "Your Place"
        ){
            Column {
                Text("Hello")
                Text("Hello")
            }
        }
    }

//    if (isMapLoaded.value == false){
//        Box(
//            modifier = Modifier,
//            contentAlignment = Alignment.Center
//        ){
//            AnimatedVisibility(!isMapLoaded.value) {
//                CircularProgressIndicator(modifier = Modifier.wrapContentSize(), trackColor = Color.Green)
//            }
//        }
//    }
}
}

@Preview(showBackground = true)
@Composable
fun pra() {
//    Map({})
}