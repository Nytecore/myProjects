package com.example.retrofitkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitkotlin.databinding.RecyclerRowBinding
import com.example.retrofitkotlin.model.CryptoData

class CryptoAdapter(val cryptoList: List<CryptoData>) : RecyclerView.Adapter<CryptoAdapter.CryptoHolder>() {

    class CryptoHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CryptoHolder(binding)
    }

    override fun getItemCount(): Int {
        return cryptoList.size
    }

    override fun onBindViewHolder(holder: CryptoHolder, position: Int) {
        holder.binding.cryptoName.text = cryptoList.get(position).name
        holder.binding.symbol.text = cryptoList.get(position).symbol
        holder.binding.price.text = "$ ${cryptoList.get(position).quote.USD.price.toString()}"
    }
}