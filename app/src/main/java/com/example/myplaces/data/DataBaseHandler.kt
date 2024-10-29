package com.example.myplaces.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHandler(context:Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "MyPlacesDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MY_PLACE = "MyPlacesTable"

        // all columns name
        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_MY_PLACES_TABLE = ("CREATE TABLE " + TABLE_MY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)"
        )
        db?.execSQL(CREATE_MY_PLACES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_MY_PLACE")
        onCreate(db)
    }

    fun addMyPlaces(myPlace: MyPlace):Long{
        val db = this.writableDatabase

        val contentvalues= ContentValues()
        contentvalues.put(KEY_NAME, myPlace.name)
        contentvalues.put(KEY_IMAGE, myPlace.image)
        contentvalues.put(KEY_DESCRIPTION, myPlace.description)
        contentvalues.put(KEY_DATE, myPlace.date)
        contentvalues.put(KEY_LOCATION, myPlace.location)
        contentvalues.put(KEY_LATITUDE, myPlace.latitude)
        contentvalues.put(KEY_LONGITUDE, myPlace.longitude)

        // inserting row
        val result = db.insert(TABLE_MY_PLACE, null, contentvalues)
        //db.close() // closing db connection
        return result
    }

    @SuppressLint("Range")
    fun getMyPlacesList():ArrayList<MyPlace>{
        val myPlacesList = ArrayList<MyPlace>()
        val selectQuery = "SELECT * FROM $TABLE_MY_PLACE"
        val db = this.readableDatabase

        try {
            val cursor:Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()){
                do {
                    val place = MyPlace(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME))?: "",
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)) ?: "",
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)) ?: "",
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)) ?: "",
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION))?: "",
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )
                    myPlacesList.add(place)
                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e:SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }finally {
            db.close()
        }
        return myPlacesList
    }

    @SuppressLint("Range")
    fun getMyPlaceById(id:Int):MyPlace?{
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_MY_PLACE WHERE $KEY_ID = $id"
        var myPlace:MyPlace? = null

        try {
            val cursor:Cursor = db.rawQuery(selectQuery, null)

            if (cursor != null && cursor.moveToFirst()){
                myPlace = MyPlace(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME))?: "",
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)) ?: "",
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)) ?: "",
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)) ?: "",
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION))?: "",
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                )
                cursor.close()
            }
        }catch (e:SQLiteException){
            e.printStackTrace()
        }finally {
            db.close()
        }
        return myPlace
    }

    fun deletePlace(id:Int){
        val db = this.writableDatabase

        db.delete(TABLE_MY_PLACE, "$KEY_ID = ?", arrayOf(id.toString()))

        db.close()
    }
}