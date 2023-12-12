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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imagePaths: List<String>
    var checkJson = false
    private val categoryLists = mutableMapOf<String, ArrayList<ImageTemplate>>()
    private val categoryListsProduct = mutableMapOf<String, ArrayList<ImageTemplate>>()
    private var jsonString: String? = null
    private var TAG = "MainActivity"
    private var listFirebasecv: List<FirebaseTemplate>? = null
    private val parentList = ArrayList<FirebaseTemplate>()

    @SuppressLint("MissingInflatedId", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        RxBus.listen(imagePath::class.java)
            .subscribe { imagePath ->
                Log.d("huhu", "onCreate: ${imagePath.path}")
                val storageRef = FirebaseStorage.getInstance().getReference().child(imagePath.path)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            Glide.with(this)
                .load(imageUrl)
                .into(binding.img)
        }

            }


        binding.rcv.layoutManager = LinearLayoutManager(this)
        getListImageDEV()
        getListImageProduct()
        // get img device
        var listImg = loadAllPhoto(this)
        imagePaths = listImg.map { it.pathImage }

        val parentList = ArrayList<FirebaseTemplate>()

        for (i in 1..30) {
            val childItems = ArrayList<ImageTemplate>()
            childItems.add(ImageTemplate(R.drawable.c, 0))
            childItems.add(ImageTemplate(R.drawable.csharp, 0))
            childItems.add(ImageTemplate(R.drawable.java, 0))
            childItems.add(ImageTemplate(R.drawable.cplusplus, 0))
            childItems.add(ImageTemplate(R.drawable.c, 0))
            childItems.add(ImageTemplate(R.drawable.csharp, 0))
            childItems.add(ImageTemplate(R.drawable.java, 0))
            childItems.add(ImageTemplate(R.drawable.cplusplus, 0))
            childItems.add(ImageTemplate(R.drawable.c, 0))
            childItems.add(ImageTemplate(R.drawable.csharp, 0))
            childItems.add(ImageTemplate(R.drawable.java, 0))
            childItems.add(ImageTemplate(R.drawable.cplusplus, 0))
            childItems.add(ImageTemplate(R.drawable.c, 0))
            childItems.add(ImageTemplate(R.drawable.csharp, 0))
            childItems.add(ImageTemplate(R.drawable.java, 0))
            childItems.add(ImageTemplate(R.drawable.cplusplus, 0))
            childItems.add(ImageTemplate(R.drawable.c, 0))
            childItems.add(ImageTemplate(R.drawable.csharp, 0))
            childItems.add(ImageTemplate(R.drawable.java, 0))
            childItems.add(ImageTemplate(R.drawable.cplusplus, 0))


            parentList.add(FirebaseTemplate("Game Development $i", false, R.drawable.console, childItems))
        }

        binding.rcv.adapter = AdapterFirebaseTemplate(this, parentList)





    }

    private fun getListImageProduct() {
        val stoimg = FirebaseStorage.getInstance()
        val categories = listOf("category1")


        categories.forEach { category ->
            val storageRef = stoimg.reference.child("Product").child("Template").child(category)
            storageRef.listAll().addOnSuccessListener { result ->
                val categoryImageListProdcut = ArrayList<ImageTemplate>()

                result.items.forEach { item ->
                    val imgName = item.name
                    val path = item.path
                    Log.d("huhu", "getListImage: $imgName")

                 /*   categoryImageListProdcut.add(ImageTemplate(path, 0))*/
                }

                categoryListsProduct[category] = categoryImageListProdcut
                bindDataToRecyclerView(categoryLists, categoryListsProduct)
            }
        }
    }
    private fun getListImageDEV() {
        val stoimg = FirebaseStorage.getInstance()
        val storageRef = stoimg.reference.child("Dev").child("Template")

        storageRef.listAll().addOnSuccessListener { result ->
            val categoryLists = mutableMapOf<String, ArrayList<ImageTemplate>>()

            result.prefixes.forEach { folderRef ->
                val folderName = folderRef.name // Lấy tên của thư mục con

                // Lấy danh sách các items trong thư mục con
                folderRef.listAll().addOnSuccessListener { folderResult ->
                    val categoryImageList = ArrayList<ImageTemplate>()

                    folderResult.items.forEach { item ->
                        val imgName = item.name
                        val path = item.path
                        Log.d("huhu", "getListImage: $imgName")

                        val imageType = if (imgName.endsWith("_sub.jpg")) 1 else 0
                       /* categoryImageList.add(ImageTemplate(path, imageType))*/
                    }

                    categoryLists[folderName] = categoryImageList
                    bindDataToRecyclerView(categoryLists,categoryListsProduct)
                }.addOnFailureListener { exception ->
                    Log.e("getListImageDEV", "Error getting items in folder $folderName: ${exception.message}")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("getListImageDEV", "Error getting folders: ${exception.message}")
        }
    }



    private fun bindDataToRecyclerView(
        categoryListsDev: Map<String, ArrayList<ImageTemplate>>,
        categoryListsProduct: Map<String, ArrayList<ImageTemplate>>
    ) {
        val combinedList = mutableListOf<FirebaseTemplate>()
        categoryListsDev.forEach { (category, imageList) ->
            combinedList.add(FirebaseTemplate(category, false, 0, imageList))
        }

        val combinedListProduct = mutableListOf<FirebaseTemplate>()
        Log.d("huhu", "category: ${categoryListsDev}")
        categoryListsProduct.forEach { (category, imageList) ->
            combinedListProduct.add(FirebaseTemplate(category, false, 0, imageList))
        }

        binding.progress.visibility = View.GONE
        val json = convertListToJson(combinedList)
        saveStringToFile(json,"TanhX.json")
        Log.d("json", "bindDataToRecyclerView: ${json}")
        val json1 = convertListToJson(combinedListProduct)

        binding.img.setOnClickListener {
/*
            Log.d("huhu", "bindDataToRecyclerView: ${json1}")
            Log.d("huhu", "bindDataToRecyclerView: ${combinedListProduct}")
*/
            if(!checkJson){
                Log.d("json", "JSON DEV : $json")
                checkJson = true
            } else {
                Log.d("json", "JSON PRODUCT : $json1")
                checkJson = false
            }
        }

        var listFirebase = convertJsonToFirebaseTemplateList(json)
       /*if(listFirebasecv!= null){

           binding.rcv.adapter = AdapterFirebaseTemplate(this, listFirebasecv!!)
       }*/
    }
    private fun readStringFromFile(fileName: String): String? {
        val file = File(this.filesDir, fileName)
        return try {
            val inputStream = FileInputStream(file)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            inputString
        } catch (e: IOException) {
            Log.e("FileReadError", "Error reading file: ${e.message}")
            null
        }
    }
    private fun saveStringToFile(content: String, fileName: String) {
        val file = File(this.filesDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(content.toByteArray())
            outputStream.close()
            jsonString = readStringFromFile("TanhX.json")
         /*   Log.d(TAG, "saveStringToFile: ${jsonString}")*/
            val gson = Gson()
            listFirebasecv= gson.fromJson(jsonString, object : TypeToken<List<FirebaseTemplate>>() {}.type)
            Log.d(TAG, "saveStringToFile: ${listFirebasecv}")

        } catch (e: IOException) {
            Log.e("FileSaveError", "Error saving file: ${e.message}")
        }

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
