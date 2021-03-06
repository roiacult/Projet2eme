package com.roacult.kero.oxxy.projet2eme.ui.start_chalenge.resourefragment

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import com.roacult.kero.oxxy.projet2eme.R
import com.roacult.kero.oxxy.projet2eme.base.BaseRecyclerAdapter
import com.roacult.kero.oxxy.projet2eme.databinding.StartChalenngeCard1Binding


class ResourceAdapter : BaseRecyclerAdapter<Resource,StartChalenngeCard1Binding>(Resource::class.java,R.layout.start_chalennge_card1){
    override fun areItemsTheSame(item1: Resource, item2: Resource) = item1 == item2

    override fun compare(o1: Resource, o2: Resource) = o1.content.first.compareTo(o2.content.first)

    override fun areContentsTheSame(oldItem: Resource, newItem: Resource) = oldItem.content.second == newItem.content.second

    override fun upDateView(item: Resource, binding: StartChalenngeCard1Binding) {
        binding.textView14.text = item.content.first
        binding.imageView6.setOnClickListener{
            val url = item.content.second
            if(URLUtil.isValidUrl(url))
                binding.root.context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(item.content.second)))
            else
                Toast.makeText(binding.root.context,"sorry not a valid url",Toast.LENGTH_LONG).show()
        }
    }

    override fun onClickOnItem(item: Resource, view: View?, binding: StartChalenngeCard1Binding, adapterPostion: Int) {
        //we will open pdf reader for user
    }
}

data class Resource (val content : Pair<String,String>)