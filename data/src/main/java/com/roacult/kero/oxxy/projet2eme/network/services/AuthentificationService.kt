package com.roacult.kero.oxxy.projet2eme.network.services

import com.roacult.kero.oxxy.domain.interactors.ResetPasswordParams
import com.roacult.kero.oxxy.projet2eme.network.entities.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthentificationService {
@POST("?op=check")
fun checkUserMail(@Body mail:Mail , @Query(value = "tooken" ) key:String):Call<MailResponse>
    @POST("?op=signin")
    fun saveUserInfo(@Body user:SaveInfo , @Query(value = "tooken" ) key:String):Call<SaveInfoResult>
    @POST("?op=rcode")
    fun sendMailConfirmation(@Body mail: CofirmMail , @Query(value = "tooken" ) key:String):Call<Code>
    @POST("?op=login")
    fun logUserIn(@Body user:LoginParame , @Query(value = "tooken" ) key:String):Call<LoginResult>
    @POST("?op=reset")
    fun resetePassword(@Body passwordParams: ResetPasswordParams, @Query(value = "tooken")key:String ):Call<Reponse>
}