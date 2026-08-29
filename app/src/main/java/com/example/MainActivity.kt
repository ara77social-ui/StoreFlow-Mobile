package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ui.LoginScreen
import com.example.ui.StoreFlowApp
import com.example.ui.StoreViewModel
import com.example.ui.getSavedUserEmail
import com.example.ui.saveUserEmail
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    private val viewModel: StoreViewModel by viewModels()
    private var isAuthenticated by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val savedEmail = viewModel.getSavedUserEmail()
        val bioEnabled = viewModel.biometricEnabled.value

        if (savedEmail != null) {
            if (bioEnabled) {
                promptBiometric()
            } else {
                isAuthenticated = true
            }
        }

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isAuthenticated) {
                        StoreFlowApp(viewModel = viewModel)
                    } else {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { email ->
                                viewModel.saveUserEmail(email)
                                isAuthenticated = true
                            },
                            onBiometricLoginRequest = {
                                promptBiometric()
                            },
                            showBiometricOption = bioEnabled && savedEmail != null
                        )
                    }
                }
            }
        }
    }

    private fun promptBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "خطا در احراز هویت", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "اثر انگشت تایید نشد", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود به StoreFlow")
            .setSubtitle("ورود سریع با اثر انگشت")
            .setNegativeButtonText("لغو")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
