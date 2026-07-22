package com.example.dikkat

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build

class SoundAlertManager(private val context: Context) {
    
    private var toneGenerator: ToneGenerator? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var lastAlertTime = 0L
    private val alertCooldownMs = 2000L // 2 saniye cooldown

    init {
        initializeToneGenerator()
    }

    private fun initializeToneGenerator() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            } else {
                @Suppress("DEPRECATION")
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSpeedExceededAlert() {
        val currentTime = System.currentTimeMillis()
        
        // Cooldown kontrolü - çok sık sesler uyarılardan kaçın
        if (currentTime - lastAlertTime < alertCooldownMs) {
            return
        }
        
        lastAlertTime = currentTime
        
        try {
            // Ses seviyesini yüksek ayarla
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
            
            // Beep sesini çal - 3 kez
            repeat(3) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
                Thread.sleep(300)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playWarningAlert() {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastAlertTime < alertCooldownMs / 2) {
            return
        }
        
        lastAlertTime = currentTime
        
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 150)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}