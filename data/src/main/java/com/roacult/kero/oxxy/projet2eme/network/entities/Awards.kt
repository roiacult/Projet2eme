package com.roacult.kero.oxxy.projet2eme.network.entities

data class AwardRemote(val id:Int , val image:String , val point:Int , val description:String)
data class AwardsRemote(val data:List<AwardRemote>)
data class GetAwardRemote(val id:Int , val rewardid:Int)
data class GetAwardResponse(val reponse:Int)