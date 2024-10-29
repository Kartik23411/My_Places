package com.example.myplaces.viewModel

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.util.Calendar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddPlacesViewModel:ViewModel() {
    fun showDatePicker(context: Context, onDateSelected:(String) -> Unit){
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                val selected = "$dayOfMonth/${month+1}/$year"
                onDateSelected(selected)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri {
        var file = context.getDir("Pics", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
//    return Uri.parse(file.absolutePath)
        return Uri.fromFile(file)
    }



}
