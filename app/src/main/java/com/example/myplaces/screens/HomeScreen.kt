package com.example.myplaces.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myplaces.R
import com.example.myplaces.viewModel.HomeScreenViewModel
import com.example.myplaces.data.MyPlace

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    onFabClick:()->Unit,
    onClick: (Int) -> Unit
){
    val context = LocalContext.current

    val places by viewModel.placesList.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.getPlacesFromDb(context)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CustomTopBar(
                title = "My Places",
                isIconRequire = false,
            ){}
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onFabClick()}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        LazyColumn (
            contentPadding = PaddingValues(2.dp),
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            if (places.isNotEmpty()) {
                items(places){place->
                    HomeScreenItem(place, onClick = { onClick(place.id) })
                }
            }else{
                item{ Text(text = "No Places are added ", modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
fun HomeScreenItem(place:MyPlace, onClick: (Int) -> Unit){
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(4.dp)
            .clickable { onClick(place.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column (
                modifier = Modifier
            ){
                // Convert the image URI string back to a Uri object
                val imageUri = Uri.parse(place.image) // place.image is the URI string

                // Load image from URI using AsyncImage
                AsyncImage(
                    contentScale = ContentScale.Crop,
                    model = imageUri, // Uri object
                    contentDescription = "profile",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(60.dp)
                        .background(Color.Black),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground), // placeholder
                    error = painterResource(R.drawable.ic_launcher_foreground) // error image
                )
            }
            Spacer(Modifier.width(16.dp))
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(0.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ){
                Text(
                    text = place.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = place.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Composable
fun CustomTopBar(title:String, isIconRequire:Boolean, onClick:() -> Unit){
    Row(
        modifier = Modifier
            .height(65.dp)
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        if (isIconRequire){
            IconButton(onClick = { onClick() }) {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        tint = Color.Black,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigation Icon"
                    )
            }
        }
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = title,
            fontSize = 20.sp,
            maxLines = 1,
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Preview(showBackground = true)
@Composable
fun Preview(){
//    CustomTopBar("Kartik", true, {})
//    HomeScreen({},{})
}