package com.example.artbooknavfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.artbooknavfragment.databinding.RecyclerRowBinding
import com.example.artbooknavfragment.model.Art
import com.example.artbooknavfragment.view.ArtFragmentDirections


class ArtAdapter(val artList: List<Art>) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ArtHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recycleViewTextView.text = artList.get(position).artName

        holder.itemView.setOnClickListener {
            val action = ArtFragmentDirections.actionArtFragmentToUploadFragment("old" , id = artList[position].id)
            it.findNavController().navigate(action)
        }
    }

}