package com.roacult.kero.oxxy.projet2eme.ui.main.fragments.profile_fragment

import android.util.Log
import com.roacult.kero.oxxy.domain.interactors.GetUserInfo
import com.roacult.kero.oxxy.domain.interactors.None
import com.roacult.kero.oxxy.domain.interactors.SolvedChalenges
import com.roacult.kero.oxxy.domain.interactors.launchInteractor
import com.roacult.kero.oxxy.domain.modules.SolvedChalenge
import com.roacult.kero.oxxy.domain.modules.User
import com.roacult.kero.oxxy.projet2eme.base.BaseViewModel
import com.roacult.kero.oxxy.projet2eme.base.State
import com.roacult.kero.oxxy.projet2eme.utils.Async
import com.roacult.kero.oxxy.projet2eme.utils.Fail
import com.roacult.kero.oxxy.projet2eme.utils.Loading
import com.roacult.kero.oxxy.projet2eme.utils.Success
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
   private val userInfo : GetUserInfo ,
    private val solvedChalenges : SolvedChalenges
) :BaseViewModel<ProfileState>(ProfileState()){


    init {
      intialize()
    }
    fun intialize(){
        scope.launchInteractor(userInfo, None()){
            setState { copy(userInfo = Success(it)) }
        }

        scope.launchInteractor(solvedChalenges,None()){
            it.either({
                Log.e("errr", "failure Solved ")
                setState{copy(challenges = Fail(it))}
            },{
                setState{copy(challenges = Success(it))}
                Log.e("errr", "success Solved")
            })
        }
    }
}

data class ProfileState(
    val userInfo : Async<User> = Loading() ,
    val challenges : Async<List<SolvedChalenge>> = Loading()
) :State