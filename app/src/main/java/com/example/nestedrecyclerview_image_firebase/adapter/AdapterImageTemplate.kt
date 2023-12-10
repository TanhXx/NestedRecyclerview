package com.example.nestedrecyclerview_image_firebase.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nestedrecyclerview_image_firebase.R
import com.example.nestedrecyclerview_image_firebase.RxBus
import com.example.nestedrecyclerview_image_firebase.model.ImageTemplate
import com.example.nestedrecyclerview_image_firebase.model.imagePath

class AdapterImageTemplate(var context : Context, var imageList: List<ImageTemplate>, var callBack: OnChildListSelected) :
    RecyclerView.Adapter<AdapterImageTemplate.imageViewholder>() {
    private var selectedPosition = -1

    inner class imageViewholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imgFirebase: ImageView = itemView.findViewById(R.id.Imageview)
        var layout = itemView.findViewById<LinearLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): imageViewholder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagepath, parent, false)
        return imageViewholder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: imageViewholder, @SuppressLint("RecyclerView") position: Int) {
        Glide.with(context)
            .load(imageList[position].imagePath)
            .into(holder.imgFirebase)

        if (selectedPosition == position) {
            holder.layout.setBackgroundResource(R.drawable.bg_itemclick)
        } else {
            holder.layout.setBackgroundResource(R.color.white)
        }

        holder.imgFirebase.setOnClickListener {
            callBack.selected(position, this)
            RxBus.publish(imagePath(imageList[position].imagePath))
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun unSelectedItem() {
        selectedPosition = -1
        notifyDataSetChanged()
    }
}
