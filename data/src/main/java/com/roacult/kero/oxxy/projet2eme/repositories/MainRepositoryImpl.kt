package com.roacult.kero.oxxy.projet2eme.repositories

import android.content.Context
import com.roacult.kero.oxxy.domain.MainRepository
import com.roacult.kero.oxxy.domain.exception.Failure
import com.roacult.kero.oxxy.domain.functional.Either
import com.roacult.kero.oxxy.domain.functional.map
import com.roacult.kero.oxxy.domain.interactors.None
import com.roacult.kero.oxxy.domain.interactors.SubmitionParam
import com.roacult.kero.oxxy.domain.interactors.SubmitionResult
import com.roacult.kero.oxxy.domain.interactors.UpdateUserInfoParam
import com.roacult.kero.oxxy.domain.modules.*
import com.roacult.kero.oxxy.projet2eme.local.AuthentificationLocal
import com.roacult.kero.oxxy.projet2eme.local.MainLocal
import com.roacult.kero.oxxy.projet2eme.network.MainRemote
import com.roacult.kero.oxxy.projet2eme.network.entities.CreatePostModel
import com.roacult.kero.oxxy.projet2eme.network.entities.PostCommentModel
import com.roacult.kero.oxxy.projet2eme.network.entities.PostId
import com.roacult.kero.oxxy.projet2eme.network.entities.SolvedChallengeResult
import com.roacult.kero.oxxy.projet2eme.utils.compressConvertBase64
import com.roacult.kero.oxxy.projet2eme.utils.mapRight

import io.reactivex.Observable
import javax.inject.Inject
import kotlin.random.Random

/**
 * this is our main repository he will handle all business logic in the main feature local and remote
 * he implement the interface defined in the domaain layer
 */
class MainRepositoryImpl @Inject constructor( private val remote :MainRemote  ,private val local :MainLocal,
                                              private val context: Context ,
                                              private val authLocal:AuthentificationLocal):MainRepository {
    override suspend fun getAllChallenges(): Either< Failure.GetAllChalengesFailure  , List<ChalengeGlobale>> {
    return remote.getChallenges(local.getChallengeRequest())
    }

    /**
     * this function will logout the user
     *
     */
    override  fun logOut() {
        local.logOut()
    }

    /**
     * this function will get the challenge detailles of a challenge defined by its id
     */
    override suspend fun getChallengeDetaille(challengeId: Int): Either<Failure.GetChalengeDetailsFailure, ChalengeDetailles> {
return remote.getChallengeDetaille(challengeId)
    }

    /**
     * this function will check the challenge each 30 seconds and deliver an integer so and if the number of pasTime
     * is not equal to this number we will notify the user or we wont do thing
     *
     */
    override fun checkChallenge(id:Int): Observable<Int> {
     return remote.checkChallenge(id).filter {
         local.checkNumber(it)
     }.doOnComplete {
       local.remove()
     }.doOnNext {
         local.save(it)
     }
    }

    override fun clearObservable() {
        remote.clear()
    }

    /**
     * so this function will get the userID from local and run the request with the
     * @param challengeId
     */
    override suspend fun setUserTry(challengeId: Int): Either<Failure.UserTryFailure, Map<Long , Long>> {
            return remote.setUserTry(local.getUserId() , challengeId)
    }

    /**
     * correction function it verifyi if the operation of correction happened well
     *  and if the user succed we add the point to local
     */
    override suspend fun checkSubmit(answer: SubmitionParam): Either<Failure.SubmitionFailure, SubmitionResult> {
        val correctionOperationResult = remote.correctChallenge(answer , local.getUserId())
        if(correctionOperationResult is Either.Right<SubmitionResult>)
            if(correctionOperationResult.b.success){
                local.addPointToUser(correctionOperationResult.b.points)
            }

        return correctionOperationResult
    }

    /**
     * get all the posts from the remote database
     * @return either a failure or a list of posts
     */
    override suspend fun getAllPosts(): Either<Failure.PostsFailure, List<Post>> {
        return remote.getAllPosts().mapRight {
            it.map {
                Post(it.id , it.postimg ,it.description , it.userid , it.username , it.useryear.toString() , it.userpicture)
            }
        }
    }

    override suspend fun getPostComments(postId: Long): Either<Failure.PostsFailure, List<Comment>> {
        return remote.getComments(PostId(postId)).mapRight{
            it.map {
                Comment(it.userid , it.userpicture , it.username , it.useryear , it.content)
            }
        }
    }

    override suspend fun commentPost(comment: Pair<String, Long>): Either<Failure.PostsFailure, None> {
        return remote.commentPost(PostCommentModel(comment.second , comment.first , local.getUserId().toLong()))
    }

    /**
     * create a new post in the database
     * @param post detaills ( image  , description)
     */
    override suspend fun createPost(post: Post): Either<Failure.PostsFailure, None> {
        return remote.createPost(CreatePostModel(post.postImage.compressConvertBase64(context) , post.postDesc , post.userId))
    }

    /**
     *  get user info we get frst from the remote if ther is error we get from local if not we update the local first before getting
     * from local
     */
    override suspend fun getUserInfo(): User {
          val gettingUserInfoFromRemote = remote.getUserInfoFromRemote(local.getUserId())
        if(gettingUserInfoFromRemote is Either.Right){
              authLocal.saveUserLogged(gettingUserInfoFromRemote.b)
        }
        return local.getUser()
    }

    /**
     * update user Info if the user has change his image then we update the local with the url we got from the server
     */
    override suspend fun updateUserInfo(userInfoParam: UpdateUserInfoParam): Either<Failure.UpDateUserInfo, None> {
        val updatingUserInfo = remote.updateUserInfo(userInfoParam , local.getUserId())
          return if(updatingUserInfo is Either.Right){
             local.updateUserData(userInfoParam.lName , userInfoParam.fname , userInfoParam.public , if(userInfoParam.picture==null) null else updatingUserInfo.b)
              Either.Right(None())
          }else{
              updatingUserInfo as Either.Left<Failure.UpDateUserInfo>
          }
    }

    override suspend fun getAwards(): Either<Failure.GetAwardsFailure, List<Award>> {
          return remote.getRewards()
    }

    override suspend fun getAward(giftId: Int): Either<Failure.GetGift, None> {
        return remote.getAward(local.getUserId() , giftId)
    }

    override suspend fun getSolvedChallenge(): Either<Failure.SolvedChalengeFailure, List<SolvedChalenge>> {
        return remote.getSolvedChallengeFromRemote(local.getUserId()).run {
          this.mapRight {
              it.map {
                  SolvedChalenge(it.id.toLong() , it.image , it.resultpts , 0.5f,it.resultpts.toFloat()/it.challengepts.toFloat(), it.mdl)
              }
          }
        }
    }

    //    override suspend fun checkSubmit(answer: SubmitionParam): Either<Failure.SubmitionFailure, SubmitionResult> {
//        //getting all true options for the challenge
//             val gettingTrueOptionOperation = remote.getTrueOptionOfChallenge(answer.chalengeId)
//        //checking for errors
//        if(gettingTrueOptionOperation.isLeft){
//            //if there is error return immediately
//             return gettingTrueOptionOperation as Either.Left<Failure.SubmitionFailure>
//        }else{
//            //if there are no error we correct the answer
//           val resultPoints = correctChallenge(answer = answer.data, timeTakenPercent =answer.timeTakenPercentage
//                   , bareme =
//                   (gettingTrueOptionOperation as Either.Right<TrueOptions>).b.options!!)
//            return if(resultPoints.success){
//                //if he succed we add the number he got
//                val addingPointToUser =  remote.addPointToUser(local.getUserId() , resultPoints.points)
//                if(addingPointToUser.isLeft){
//                    addingPointToUser as Either.Left<Failure.SubmitionFailure >
//                }else{
//                    local.addPointToUser(resultPoints.points)
//
//                    Either.Right(resultPoints)
//                }
//            }else{
//                //if not we return directly
//                Either.Right(resultPoints)
//            }
//
//        }
//    }
//
//    /**
//     * a fair correctio function that will decide if the challenge was solved and how many point thes user will get
//     * from solving the challenge
//     * @param answer the user data of the challenge
//     * @param bareme the correct data and the timeTakenPercentage of the challenge
//     * @return  the resultPoints of the correction
//     */
//    fun correctChallenge(answer:Map<Long , Long> , timeTakenPercent:Float  , bareme:List<TrueOption>):SubmitionResult{
//       val userErrorNummbers = checkUserErrors(answer,bareme.associate {
//           Pair(it.questionId , it.optionId)
//       })
//        var hasGotPenalty = false
//       if(userErrorNummbers==1){
//            hasGotPenalty = true
//        }else if(userErrorNummbers>=2){
//            return SubmitionResult(false , 0)
//        }
//        var pointsWithoutCountingTime = calculatePointGotFromChallenge(answer , bareme)
//       if(hasGotPenalty) pointsWithoutCountingTime /= 2
//        val  finalPointGot = calculateTimePenalty(timeTakenPercent , pointsWithoutCountingTime)
//        return if(finalPointGot ==0L) SubmitionResult(false , 0L)
//        else SubmitionResult(true , finalPointGot)
//
//    }
//
//    /**
//     * we calculate the isFinished penalty if the user solve it in <60% of the given isFinished  he got all the
//     * point dedicated to the challenge else for each 10%isFinished taken mor than 60% he will be
//     * cut 20% of his point
//     */
//    private fun calculateTimePenalty(timeTakenPercent: Float , pointGot :Long) = when {
//            timeTakenPercent==1.0f -> 0L
//            timeTakenPercent>1.0f -> 0L
//            timeTakenPercent<=0.6f -> pointGot
//            timeTakenPercent<=0.7f -> pointGot -(pointGot*2)/10
//            timeTakenPercent <=0.8f -> pointGot -(pointGot*4)/10
//            timeTakenPercent<=0.9f -> pointGot-(pointGot *6)/10
//            else -> pointGot-(pointGot*8)/10
//        }
//
//
//    /**
//     * we calculate the number of point the user got from solving the challenge for each
//     * correct answer he got the point of that question
//     */
//    private fun calculatePointGotFromChallenge(answer:Map<Long , Long>  , bareme:List<TrueOption>):Long{
//        var totalPoint  =0L
//        val baremMap = bareme.associate {
//            Pair(it.questionId , Pair(it.optionId , it.point))
//        }
//        answer.forEach {
//            if(it.value == baremMap[it.key]!!.first){
//                totalPoint += baremMap[it.key]!!.second
//            }
//        }
//        return totalPoint
//
//    }
//
//    /**
//     * we calculate the number of errors the user got in the challenge
//     */
//    private fun checkUserErrors(answer: Map<Long, Long>, barem:Map<Long , Long>):Int {
//        var errorNumber = 0
//        answer.forEach {
//            if(barem[it.key]!=it.value){
//                errorNumber++
//            }
//        }
//        return errorNumber
//    }



}