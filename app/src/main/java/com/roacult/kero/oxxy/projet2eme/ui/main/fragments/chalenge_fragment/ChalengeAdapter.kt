package com.roacult.kero.oxxy.projet2eme.ui.main.fragments.chalenge_fragment

import android.view.View
import com.roacult.kero.oxxy.projet2eme.base.BaseRecyclerAdapter
import com.roacult.kero.oxxy.domain.modules.ChalengeGlobale
import com.roacult.kero.oxxy.projet2eme.R
import com.roacult.kero.oxxy.projet2eme.databinding.MainChalengesCardBinding

class ChalengeAdapter
    : BaseRecyclerAdapter<ChalengeGlobale, MainChalengesCardBinding>(ChalengeGlobale::class.java, R.layout.main_chalenges_card){


    override fun areItemsTheSame(item1: ChalengeGlobale, item2: ChalengeGlobale) = item1.id == item2.id

    override fun compare(o1: ChalengeGlobale, o2: ChalengeGlobale) = o1.id.compareTo(o2.id)

    override fun areContentsTheSame(oldItem: ChalengeGlobale, newItem: ChalengeGlobale)= oldItem.module == newItem.module &&
            oldItem.nbOfQuestions == newItem.nbOfQuestions &&
            oldItem.nbPersonSolveded == newItem.nbPersonSolveded &&
            oldItem.story == newItem.story &&
            oldItem.point == newItem.point

    override fun upDateView(item: ChalengeGlobale, binding: MainChalengesCardBinding) {
        //TODO update view
        binding.textView7.setText(item.story)
        binding.textView6.setText(item.module)
    }

    override fun onClickOnItem(item: ChalengeGlobale, view: View?, binding: MainChalengesCardBinding, adapterPostion : Int) {
        //TODO  implement this
    }
}