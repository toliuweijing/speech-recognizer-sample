package com.innosage.cmp.example.speechrecognizer

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.innosage.cmp.example.speechrecognizer.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SpeechRecognizerViewModel by viewModels()
    private val voskViewModel: VoskSpeechServiceViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LiveTranscribeScreen(
                        voskViewModel,
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

@Composable
fun LiveTranscribeScreen(
    viewModel: VoskSpeechServiceViewModel,
    viewModel2: SpeechRecognizerViewModel,
    modifier: Modifier = Modifier
) {
    val isListening by viewModel.isListening.collectAsState()
    val transcribedText by viewModel.transcribedText.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.toggleTranscription() }) {
            Text(text = if (isListening) "Stop Transcription" else "Start Transcription")
        }
        Text(
            text = transcribedText,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LiveTranscribeScreenPreview() {
    MyApplicationTheme {
        // This is a preview, so we can't use a real ViewModel.
        // We can create a dummy for preview purposes if needed.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { }) {
                Text(text = "Start Transcription")
            }
            Text(
                text = "Your transcribed text will appear here.",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
