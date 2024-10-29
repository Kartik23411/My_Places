package com.example.myplaces.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myplaces.R
import com.example.myplaces.viewModel.HomeScreenViewModel

@Composable
fun DetailsScreen(
    id:Int,
    viewModel: HomeScreenViewModel = viewModel(),
    onMapClicked:(Int)-> Unit,
    onNavButtonClick:()->Unit
){

    val context = LocalContext.current
    val place by viewModel.place.observeAsState()
    LaunchedEffect(id) {
        viewModel.getPlaceById(context, id)
    }

    place?.let {
    Scaffold (
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().background(Color(0xFFF5F5F5)),
        topBar = {
                CustomTopBar(
                    title = it.name,
                    true,
                ){onNavButtonClick()}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}

            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        }
    ){pd->
        Column (modifier = Modifier.padding(pd).fillMaxSize()){
            val imageUri = Uri.parse(it.image)
            AsyncImage(
                contentScale = ContentScale.Fit,
                model = imageUri, // Uri object
                contentDescription = "profile",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Black),
                placeholder = painterResource(R.drawable.istockphoto_1021471354_612x612), // placeholder
                error = painterResource(R.drawable.istockphoto_1021471354_612x612) // error image
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = it.description,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                onMapClicked(it.id)
            }) {
                Text("Show on Map")
            }

        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun Previewo(){
    val viewModel:HomeScreenViewModel = viewModel()
//    DetailsScreen(12, viewModel)
}