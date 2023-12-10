package com.example.nestedrecyclerview_image_firebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nestedrecyclerview_image_firebase.adapter.AdapterFirebaseTemplate
import com.example.nestedrecyclerview_image_firebase.databinding.ActivityMainBinding
import com.example.nestedrecyclerview_image_firebase.model.FirebaseTemplate
import com.example.nestedrecyclerview_image_firebase.model.ImageTemplate
import com.example.nestedrecyclerview_image_firebase.model.imagePath
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imagePaths: List<String>



    @SuppressLint("MissingInflatedId", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)





        RxBus.listen(imagePath::class.java)
            .subscribe { imagePath ->
                Glide.with(this)
                    .load(imagePath.path)
                    .into(binding.img)
            }






        binding.rcv.layoutManager = LinearLayoutManager(this)
        getListImage()
        // get img device
        var listImg = loadAllPhoto(this)
        imagePaths = listImg.map { it.pathImage }


    }

    private fun getListImage() {
        val stoimg = FirebaseStorage.getInstance()
        val categories = listOf("category1")

        val categoryLists = mutableMapOf<String, ArrayList<ImageTemplate>>()

        categories.forEach { category ->
            val storageRef = stoimg.reference.child("Firebase_template").child(category)
            storageRef.listAll().addOnSuccessListener { result ->
                val categoryImageList = ArrayList<ImageTemplate>()

                result.items.forEach { item ->
                    val downloadTask = item.downloadUrl
                    val imgName = item.name
                    val path = item.path
                    Log.d("huhu", "getListImage: ${path}")
                    downloadTask.addOnSuccessListener { uri ->
                        val imgUri = uri.toString()
                        categoryImageList.add(ImageTemplate(imgUri, 0))
                        categoryLists[category] = categoryImageList
                        bindDataToRecyclerView(categoryLists)
                    }
                }
            }
        }


    }

    private fun bindDataToRecyclerView(categoryLists: Map<String, ArrayList<ImageTemplate>>) {
        /*val combinedList = categoryLists.map { (category, imageList) ->
            FirebaseTemplate(category, false, imageList)
        }.toMutableList()*/
        val combinedList = mutableListOf<FirebaseTemplate>()
        categoryLists.forEach { (category, imageList) ->
            combinedList.add(FirebaseTemplate("Ảnh từ Firebase", false, imageList))
            combinedList.add(FirebaseTemplate("Ảnh từ Firebase1", false, imageList))
            combinedList.add(FirebaseTemplate("Ảnh từ Firebase2", false, imageList))
        }

        combinedList.add(FirebaseTemplate("Ảnh từ thiết bị", false, imagePaths.map { ImageTemplate(it, 0) }))


        binding.progress.visibility = View.GONE
        val json = convertListToJson(combinedList) // object -> json
        var listFirebase = convertJsonToFirebaseTemplateList(json) // json -> object

        binding.rcv.adapter = AdapterFirebaseTemplate(this, listFirebase)
    }

    fun convertListToJson(combinedList: List<FirebaseTemplate>): String {
        val gson = Gson()
        return gson.toJson(combinedList)
    }

    fun convertJsonToFirebaseTemplateList(json: String): List<FirebaseTemplate> {
        val gson = Gson()
        val listType = object : TypeToken<List<FirebaseTemplate>>() {}.type
        return gson.fromJson(json, listType)
    }

    @SuppressLint("Range")
    fun loadAllPhoto(context: Context): List<BackgroundImageDevice> {
        try {
            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
            val cursorPhoto = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED
            )
            val list = ArrayList<BackgroundImageDevice>(
                cursorPhoto!!.count
            )
            if (cursorPhoto.moveToLast()) {
                do {
                    val id = cursorPhoto.getLong(cursorPhoto.getColumnIndex(projection[0]))
                    val name = cursorPhoto.getString(cursorPhoto.getColumnIndex(projection[1]))
                    val path = cursorPhoto.getString(cursorPhoto.getColumnIndex(projection[2]))
                    val file = File(path)
                    if (file.exists()) {
                        list.add(BackgroundImageDevice(path))
                    }
                } while (cursorPhoto.moveToPrevious())
            }
            cursorPhoto.close()
            return list
        } catch (e: Exception) {
            return arrayListOf()
        }
    }

    data class BackgroundImageDevice(val pathImage : String)
}
