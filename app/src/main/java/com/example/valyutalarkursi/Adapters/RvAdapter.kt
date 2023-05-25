package com.example.valyutalarkursi.Adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valyutalarkursi.Class.Valyuta
import com.example.valyutalarkursi.databinding.ItemRvBinding

class RvAdapter(val list: ArrayList<Valyuta>): RecyclerView.Adapter<RvAdapter.vh>() {
    inner class vh(val itemRvBinding: ItemRvBinding):RecyclerView.ViewHolder(itemRvBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vh {
        return  vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: vh, position: Int) {

        val valyuta=list[position]
        holder.itemRvBinding.tvId.text=(position+1).toString()
        holder.itemRvBinding.valyutaName.text=valyuta.Ccy
        holder.itemRvBinding.tvValyutaQiymati.text=valyuta.Rate
    }

}