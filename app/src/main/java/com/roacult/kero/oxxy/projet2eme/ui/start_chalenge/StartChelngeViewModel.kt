package com.roacult.kero.oxxy.projet2eme.ui.start_chalenge

import com.roacult.kero.oxxy.domain.exception.Failure
import com.roacult.kero.oxxy.domain.interactors.GetChalengeDetaills
import com.roacult.kero.oxxy.domain.interactors.launchInteractor
import com.roacult.kero.oxxy.domain.modules.ChalengeDetailles
import com.roacult.kero.oxxy.domain.modules.ChalengeGlobale
import com.roacult.kero.oxxy.domain.modules.Question
import com.roacult.kero.oxxy.projet2eme.base.BaseViewModel
import com.roacult.kero.oxxy.projet2eme.ui.start_chalenge.first_fragment.FirstFragment
import com.roacult.kero.oxxy.projet2eme.utils.Loading
import com.roacult.kero.oxxy.projet2eme.utils.Success
import com.roacult.kero.oxxy.projet2eme.utils.Fail
import com.roacult.kero.oxxy.projet2eme.utils.Event
import com.roacult.kero.oxxy.projet2eme.utils.extension.questionSolved
import javax.inject.Inject

class StartChelngeViewModel @Inject constructor(private val useCase : GetChalengeDetaills) :
    BaseViewModel<StartChalengeState>(StartChalengeState(Event(STARTCHALENGE_FRAGMENT1),Loading(),Event(0),0,0)), FirstFragment.CallbackToViewModel {

    lateinit var chalengeGlobale: ChalengeGlobale
    val answers = mutableMapOf<Long,Long>()

    var firstTime = true
    var lastTime : Int = 0
    var size = 0

    override fun isItFirstTime() = firstTime

    override fun saveData(chalengeGlobale: ChalengeGlobale) {
        this.chalengeGlobale = chalengeGlobale
        firstTime = false
        fetchData()
    }

    override fun fetchData() {
        scope.launchInteractor(useCase,chalengeGlobale.id){
            it.either(::handleFailure,::handleSuccesss)
        }
    }


    private fun handleSuccesss(chalengeDetailles: ChalengeDetailles) {
        lastTime = chalengeDetailles.time
        size = chalengeDetailles.questions.size
        //init map of answers
        for(question in chalengeDetailles.questions){ answers[question.id] = -1 }
        setState { copy(getChalengeDetailles = Success(chalengeDetailles),questionSolved =answers.questionSolved ) }
    }

    private fun handleFailure(getChalengeDetailsFailure: Failure.GetChalengeDetailsFailure) {
        setState { copy(getChalengeDetailles = Fail(getChalengeDetailsFailure)) }
    }

    override fun startChalenge() {
        setState { copy(selectedFragment = Event(STARTCHALENGE_FRAGMENT2) ) }
    }

    fun setPage(page : Int){
        setState{copy(page = Event(page))}
    }

    fun setAnswer(questionsId :Long ,optionId :Long){
        answers[questionsId] = optionId
        setState { copy(questionSolved = answers.questionSolved) }
    }
}