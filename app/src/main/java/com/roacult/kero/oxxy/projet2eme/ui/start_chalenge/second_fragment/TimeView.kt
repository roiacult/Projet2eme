package com.roacult.kero.oxxy.projet2eme.ui.start_chalenge.second_fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.Nullable

class TimeView : TextView{

    //call all constructors in super class
    constructor(context: Context,@Nullable attrs : AttributeSet,defStyleAttr:Int,defStyleRes : Int) : super(context,attrs,defStyleAttr,defStyleRes)
    constructor(context: Context,@Nullable attrs : AttributeSet,defStyleAttr:Int): super(context,attrs,defStyleAttr)
    constructor(context: Context,@Nullable attrs : AttributeSet) : super(context,attrs)



    /**
     * itsAlredyStart to avoid subscibing handler
     * multiple time we need to subsribe runnable
     * to handler only one
     * */
    private var itsAlredyStart = false

    /**
     * this field  will hold the curent time
     *
     * */

    private var time: Long = 0

    /**
     * callback when time finish
     * */
    var onTimeFinishlistner : () ->Unit?  = {}

    /**
     * the view is animating ar not
     * */
    var isAnimated = false

    /**
     * once we registred this Runnable with handler he will keep call
     * onTimeChanged() every second till time finish
     * */
    private val ticker = object : Runnable {
        override fun run() {
            if(time>0) {
                //time didn't finish yet
                onTimeChanged()
                time-=1000
                handler.postAtTime(this, SystemClock.uptimeMillis() + 1000)
            }else{
                //time finish
                time = 0
                onTimeChanged()
                //callback
                onTimeFinishlistner()
            }
        }
    }

    /**
     * this function will start timer from the given value
     * (registring our tiker)
     * */
    fun startTimer(time : Long,onFinish : ()->Unit){
        onTimeFinishlistner = onFinish
        setTextColor(Color.BLACK)
        if(!itsAlredyStart){
            this.time = time
            itsAlredyStart = true
            handler.post(ticker)
        }
    }

    private fun onTimeChanged() {
        val timeOnSec = time/1000
        val seconds = timeOnSec%60
        val minute = timeOnSec/60

        if(minute == 0L && !isAnimated) {
            //if it left less than one minute we should animate the view
            //and make text color red
            this.setTextColor(Color.RED)
            isAnimated =true
            animmateView()
        }
        val minStr = if(minute<10) "0$minute" else minute.toString()
        val secStr = if(seconds<10) "0$seconds" else seconds.toString()
        setText(minStr+":"+secStr)
    }

    private fun animmateView() {
        this.animate().apply {
            //fade out
            duration = 500
            alpha(0f)
            setListener(object  : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if(isAnimated){
                        //fade in
                        this@TimeView.animate().apply {
                            duration = 500
                            alpha(1f)
                            setListener(object  : Animator.AnimatorListener{
                                override fun onAnimationRepeat(animation: Animator?) {}
                                override fun onAnimationCancel(animation: Animator?) {}
                                override fun onAnimationStart(animation: Animator?) {}
                                override fun onAnimationEnd(animation: Animator?) {
                                    //start again if it still animated
                                    if(isAnimated) this@TimeView.animmateView()
                                }
                            })
                            start()
                        }
                    }
                }
            })
            start()
        }
    }
}