package com.example.nestedrecyclerview_image_firebase.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nestedrecyclerview_image_firebase.R
import com.example.nestedrecyclerview_image_firebase.model.FirebaseTemplate

class AdapterFirebaseTemplate(var context: Context,var firebaseTemplate: List<FirebaseTemplate>) :
    RecyclerView.Adapter<AdapterFirebaseTemplate.firebaseViewholder>() {
    inner class firebaseViewholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val categoryName : TextView = itemView.findViewById(R.id.categoryName)
        var imageTemplateRcv : RecyclerView = itemView.findViewById(R.id.rcv_firebasetemplate)
        var layout : LinearLayout = itemView.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): firebaseViewholder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_firebasetemplate, parent, false)
        return firebaseViewholder(view)
    }

    override fun getItemCount(): Int {
        return firebaseTemplate.size
    }

    override fun onBindViewHolder(holder: firebaseViewholder, position: Int) {
        var currentItem = firebaseTemplate[position]
        holder.categoryName.text = currentItem.categoryName
        holder.imageTemplateRcv.adapter = AdapterImageTemplate(context, currentItem.mlist, object: OnChildListSelected{
            override fun selected(position: Int, adapter: AdapterImageTemplate) {
                if (childAdapterSelected != null) {
                    childAdapterSelected?.unSelectedItem()
                }
                childAdapterSelected = adapter
            }

        })
        holder.layout.setOnClickListener {
            Log.d("huhu", "onBindViewHolder: ${position}")
        }

    }
    var childAdapterSelected: AdapterImageTemplate? = null;
}
interface OnChildListSelected {
   fun selected(position: Int, adapter: AdapterImageTemplate)
}
