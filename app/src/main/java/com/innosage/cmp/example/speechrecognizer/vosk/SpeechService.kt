package com.innosage.cmp.example.speechrecognizer.vosk

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.os.Handler
import android.os.Looper
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import java.io.IOException
import kotlin.concurrent.Volatile


class SpeechService @SuppressLint("MissingPermission") constructor(
    private val recognizer: Recognizer,
    sampleRate: Float
) {
    private val sampleRate: Int
    private val bufferSize: Int
    private val recorder: AudioRecord
    private var recognizerThread: RecognizerThread? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        this.sampleRate = sampleRate.toInt()
        this.bufferSize = Math.round(this.sampleRate.toFloat() * 0.2f)
        this.recorder = AudioRecord(6, this.sampleRate, 16, 2, this.bufferSize * 2)
        if (this.recorder.getState() == 0) {
            this.recorder.release()
            throw IOException("Failed to initialize recorder. Microphone might be already in use.")
        }
    }

    fun startListening(listener: RecognitionListener): Boolean {
        if (null != this.recognizerThread) {
            return false
        } else {
            this.recognizerThread = RecognizerThread(listener)
            this.recognizerThread!!.start()
            return true
        }
    }

    fun startListening(listener: RecognitionListener, timeout: Int): Boolean {
        if (null != this.recognizerThread) {
            return false
        } else {
            this.recognizerThread = RecognizerThread(listener, timeout)
            this.recognizerThread!!.start()
            return true
        }
    }

    private fun stopRecognizerThread(): Boolean {
        if (null == this.recognizerThread) {
            return false
        } else {
            try {
                this.recognizerThread!!.interrupt()
                this.recognizerThread!!.join()
            } catch (var2: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            this.recognizerThread = null
            return true
        }
    }

    fun stop(): Boolean {
        return this.stopRecognizerThread()
    }

    fun cancel(): Boolean {
        if (this.recognizerThread != null) {
            this.recognizerThread!!.setPause(true)
        }

        return this.stopRecognizerThread()
    }

    fun shutdown() {
        this.recorder.release()
    }

    fun setPause(paused: Boolean) {
        if (this.recognizerThread != null) {
            this.recognizerThread!!.setPause(paused)
        }
    }

    fun reset() {
        if (this.recognizerThread != null) {
            this.recognizerThread!!.reset()
        }
    }

    private inner class RecognizerThread @JvmOverloads constructor(
        var listener: RecognitionListener,
        timeout: Int = -1
    ) : Thread() {
        private var remainingSamples: Int
        private val timeoutSamples: Int

        @Volatile
        private var paused = false

        @Volatile
        private var reset = false

        init {
            if (timeout != -1) {
                this.timeoutSamples = timeout * this@SpeechService.sampleRate / 1000
            } else {
                this.timeoutSamples = -1
            }

            this.remainingSamples = this.timeoutSamples
        }

        fun setPause(paused: Boolean) {
            this.paused = paused
        }

        fun reset() {
            this.reset = true
        }

        override fun run() {
            this@SpeechService.recorder.startRecording()
            if (this@SpeechService.recorder.getRecordingState() == 1) {
                this@SpeechService.recorder.stop()
                val ioe =
                    IOException("Failed to start recording. Microphone might be already in use.")
                this@SpeechService.mainHandler.post(Runnable { this.listener.onError(ioe) })
            }

            val buffer = ShortArray(this@SpeechService.bufferSize)

            while (!interrupted() && (this.timeoutSamples == -1 || this.remainingSamples > 0)) {
                val nread = this@SpeechService.recorder.read(buffer, 0, buffer.size)
                if (!this.paused) {
                    if (this.reset) {
                        this@SpeechService.recognizer.reset()
                        this.reset = false
                    }

                    if (nread < 0) {
                        throw RuntimeException("error reading audio buffer")
                    }

                    if (this@SpeechService.recognizer.acceptWaveForm(buffer, nread)) {
                        val result = this@SpeechService.recognizer.getResult()
                        this@SpeechService.mainHandler.post(Runnable { this.listener.onResult(result) })
                    } else {
                        val partialResult = this@SpeechService.recognizer.getPartialResult()
                        this@SpeechService.mainHandler.post(Runnable {
                            this.listener.onPartialResult(
                                partialResult
                            )
                        })
                    }

                    if (this.timeoutSamples != -1) {
                        this.remainingSamples -= nread
                    }
                }
            }

            this@SpeechService.recorder.stop()
            if (!this.paused) {
                if (this.timeoutSamples != -1 && this.remainingSamples <= 0) {
                    this@SpeechService.mainHandler.post(Runnable { this.listener.onTimeout() })
                } else {
                    val finalResult = this@SpeechService.recognizer.getFinalResult()
                    this@SpeechService.mainHandler.post(Runnable {
                        this.listener.onFinalResult(
                            finalResult
                        )
                    })
                }
            }
        }
    }

    companion object {
        private const val BUFFER_SIZE_SECONDS = 0.2f
    }
}
