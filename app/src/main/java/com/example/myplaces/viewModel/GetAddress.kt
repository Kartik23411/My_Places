package com.example.myplaces.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import java.util.Locale
import kotlin.math.atan

class GetAddress(context: Context, private val latitude:Double, private val longitude:Double): AsyncTask<Void, String, String>() {

    private val geocoder:Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var maddressListener:AddressListener

    override fun doInBackground(vararg params: Void?): String {
        try{
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude,1)

            if (addressList != null && addressList.isNotEmpty()){
                val address: Address = addressList[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex){
                    sb.append(address.getAddressLine(i)).append(" ")
                }
                sb.deleteCharAt(sb.length-1)
                return sb.toString()
            }

        } catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }

    override fun onPostExecute(result: String?) {
        if (result == null ){
            maddressListener.onError()
        }else{
            maddressListener.onAddressFound(result)
        }
        super.onPostExecute(result)
    }

    fun setAdressListener(addressListener: AddressListener){
        maddressListener = addressListener
    }

    fun getAddress(){
        execute()
    }

    interface AddressListener{
        fun onAddressFound(address:String?)
        fun onError()
    }
}