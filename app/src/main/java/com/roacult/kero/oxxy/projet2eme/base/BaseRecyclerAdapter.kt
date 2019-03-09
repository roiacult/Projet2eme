package com.roacult.kero.oxxy.projet2eme.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList

abstract class BaseRecyclerAdapter<D ,B : ViewDataBinding>(kClass: Class<D>,@LayoutRes val layoutId : Int) : RecyclerView.Adapter<BaseRecyclerAdapter<D,B>.ViewHolder>() {

    val callback : SortedList.Callback<D>  = object : SortedList.Callback<D>(){
        override fun areItemsTheSame(item1: D, item2: D): Boolean  = this@BaseRecyclerAdapter.areItemsTheSame(item1,item2)

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            this@BaseRecyclerAdapter.notifyItemMoved(fromPosition,toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            this@BaseRecyclerAdapter.notifyItemRangeChanged(position,count)
        }

        override fun onInserted(position: Int, count: Int) {
            this@BaseRecyclerAdapter.notifyItemRangeInserted(position,count)
        }

        override fun onRemoved(position: Int, count: Int) {
            this@BaseRecyclerAdapter.notifyItemRangeRemoved(position,count)
        }

        override fun compare(o1: D, o2: D): Int =this@BaseRecyclerAdapter.compare(o1,o2)

        override fun areContentsTheSame(oldItem: D, newItem: D): Boolean = this@BaseRecyclerAdapter.areContentsTheSame(oldItem,newItem)

    }

    /**
     * if you want change any thing in recyclerview just
     * change the content of @param{listOfData} and it
     * will notify the adapter automaticlly
     * */

    val listOfData : SortedList<D> = SortedList(kClass,callback)

    abstract fun areItemsTheSame(item1: D, item2: D) : Boolean
    abstract fun compare(o1: D, o2: D): Int
    abstract fun areContentsTheSame(oldItem: D, newItem: D): Boolean
    abstract fun upDateView(item : D ,binding: B  )
    abstract fun onClickOnItem(item : D , view : View?, binding: B)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<B>(inflater,layoutId,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfData.size()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.upDateView(listOfData[position])
    }

    inner class ViewHolder(val binding: B ) :RecyclerView.ViewHolder(binding.root) {

        fun upDateView(item : D){
            upDateView(item,binding)
            binding.root.setOnClickListener {
                onClickOnItem(item,it,binding)
            }
        }
    }

}