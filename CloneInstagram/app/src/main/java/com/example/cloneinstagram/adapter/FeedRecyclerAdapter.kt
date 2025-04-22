package com.example.cloneinstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloneinstagram.databinding.RecyclerRowBinding
import com.example.cloneinstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postList : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        //PostHolder sınıfı ilk kez oluştuğunda ne olacağını yazdığımız alandır. Layout ile bağlama işlemini yaparız.
            //RecyclerRow ile Layout bağlama işlemini yapacağız.

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        //Listeleme içerisinde kaç tane liste kullanacağımızı belirttiğimiz yerdir. Liste internetten çekileceği için ona göre yazacağız.

        return postList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        //onCreateViewHolder içerisindeki bağlama işlemini yaptıktan sonra ne olacağını yazdığımız alandır.
            //Hangi alanda hangi veri kullanılacak onu yazarız.

        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)

    }
}