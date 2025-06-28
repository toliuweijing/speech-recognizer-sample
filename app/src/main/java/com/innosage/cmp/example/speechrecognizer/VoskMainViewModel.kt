package com.innosage.cmp.example.speechrecognizer

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.innosage.cmp.example.speechrecognizer.vosk.SpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.StorageService
import java.io.IOException
import java.lang.Exception

class VoskMainViewModel(application: Application) : AndroidViewModel(application) {

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var model: Model? = null
    private var speechService: SpeechService? = null

    init {
        initModel()
    }

    private val listener = object : RecognitionListener {
        override fun onPartialResult(p0: String) {
            _transcribedText.value = p0
        }

        override fun onResult(p0: String) {
            _transcribedText.value = p0
        }

        override fun onFinalResult(p0: String) {
            _transcribedText.value = p0
        }

        override fun onError(p0: Exception?) {
            Toast.makeText(application, "", Toast.LENGTH_SHORT).show()
        }

        override fun onTimeout() {
            Toast.makeText(application, "onTimeout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recognizeMicrophone() {
        if (speechService != null) {
            speechService?.stop()
            speechService = null
        } else {
            try {
                val rec = Recognizer(model, 16000.0f)
                speechService = SpeechService(rec, 16000.0f)
                speechService?.startListening(listener)
            } catch (e: IOException) {
                showToast(e.message!!)
            }
        }
    }

    fun toggleTranscription() {
        if (speechService != null) {
            speechService?.stop()
            speechService?.shutdown()
            speechService = null
            _isListening.value = false
        } else {
            recognizeMicrophone()
            _isListening.value = true
        }
    }

    private fun initModel() {
        StorageService.unpack(
            this.getApplication(), "model-small-cn", "model",
            { model: Model ->
                this.model = model
            },
            { exception: IOException? ->
                showToast("Failed to unpack the model, ${exception?.message}")
            }
        )
    }

    private fun showToast(message: String) {
        Toast
            .makeText(
                this.getApplication(),
                message,
                Toast.LENGTH_SHORT
            )
            .show()
    }

    override fun onCleared() {
        super.onCleared()
        speechService?.shutdown()
    }
}
