package com.example.myplaces.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaces.data.DataBaseHandler
import com.example.myplaces.data.MyPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel:ViewModel() {

    private val _placesList = MutableLiveData<List<MyPlace>>()
    val placesList :LiveData<List<MyPlace>> get() = _placesList
    private val _place = MutableLiveData<MyPlace?>()
    val place :LiveData<MyPlace?> get() = _place

    fun getPlacesFromDb(context: Context){
        val dbHandler = DataBaseHandler(context)

        // Run database operation in the background
        viewModelScope.launch(Dispatchers.IO) {
            val places: ArrayList<MyPlace> = dbHandler.getMyPlacesList()
            _placesList.postValue(places)

        }
    }

    fun getPlaceById(context: Context, id:Int){
        val dbHandler = DataBaseHandler(context)

        viewModelScope.launch(Dispatchers.IO){
            val fetchedPlace:MyPlace? = dbHandler.getMyPlaceById(id)
            _place.postValue(fetchedPlace)
        }
    }

    fun deletePlace(context: Context, id:Int){
        val dbHandler = DataBaseHandler(context)

        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.deletePlace(id)
        }
    }
}