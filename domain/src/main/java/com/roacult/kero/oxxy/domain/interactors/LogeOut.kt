package com.roacult.kero.oxxy.domain.interactors

import com.roacult.kero.oxxy.domain.MainRepository
import javax.inject.Inject

class LogeOut @Inject constructor(val repo:MainRepository) {
    /**
     * just invoke this function
     */
    fun invoke(){
         repo.logOut()
    }
}