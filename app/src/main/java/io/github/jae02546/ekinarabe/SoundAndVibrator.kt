package io.github.jae02546.ekinarabe

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator


object SoundAndVibrator {
    private var soundPool: SoundPool? = null
    private var soundMove = 0
    private var soundComp = 0
    private var soundRecord = 0

    fun loadSound(context: Context) {

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(2)
            .build()

        soundMove = soundPool!!.load(context, R.raw.pushing_a_key, 1)
        soundComp = soundPool!!.load(context, R.raw.coin05, 1)
        soundRecord = soundPool!!.load(context, R.raw.crrect_answer3, 1)
    }

    fun playSoundMove() {
        soundPool!!.play(soundMove, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playSoundComp() {
        soundPool!!.play(soundComp, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playSoundRecord() {
        soundPool!!.play(soundRecord, 1.0f, 1.0f, 1, 0, 1.0f)
    }


    fun runVibratorMove(context: Context) {
        runVibrator(context, 100)
    }

    fun runVibratorComp(context: Context) {
        runVibrator(context, 300)
    }

    fun runVibratorRecord(context: Context) {
        runVibrator(context, 500)
    }

    //@RequiresApi(31)
    private fun runVibrator(context: Context, VibrationTime: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as Vibrator
        val vibrationEffect =
            VibrationEffect.createOneShot(VibrationTime, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }


}

