package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.AppDestination
import com.example.presentation.MainViewModel
import com.example.presentation.auth.DeviceTrustScreen
import com.example.presentation.auth.LoginScreen
import com.example.presentation.auth.MfaScreen
import com.example.presentation.auth.SplashScreen
import com.example.presentation.components.BiometricScanDialog
import com.example.presentation.navigation.MainAppScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.ZeroTrustXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZeroTrustXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberBackground
                ) {
                    ZeroTrustXApp()
                }
            }
        }
    }
}

@Composable
fun ZeroTrustXApp(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.currentDestination) {
            AppDestination.SPLASH -> {
                SplashScreen(
                    onTimeout = { viewModel.onSplashFinished() }
                )
            }

            AppDestination.LOGIN -> {
                LoginScreen(
                    onLoginSubmit = { email, pass ->
                        viewModel.login(email, pass)
                    }
                )
            }

            AppDestination.MFA -> {
                MfaScreen(
                    onVerifyOtp = { otp ->
                        viewModel.verifyMfa(otp)
                    },
                    onTriggerBiometrics = {
                        viewModel.startBiometricVerification {
                            viewModel.verifyMfa("482169")
                        }
                    },
                    onBackToLogin = {
                        viewModel.signOut()
                    }
                )
            }

            AppDestination.DEVICE_TRUST -> {
                DeviceTrustScreen(
                    onTrustSelected = { trusted ->
                        viewModel.completeDeviceTrust(trusted)
                    }
                )
            }

            AppDestination.MAIN_APP -> {
                MainAppScreen(
                    viewModel = viewModel,
                    onSignOut = {
                        viewModel.signOut()
                    }
                )
            }
        }

        // Biometric dialog on Auth screens if active
        if (uiState.showBiometricScanDialog && uiState.currentDestination != AppDestination.MAIN_APP) {
            BiometricScanDialog(
                isScanning = uiState.biometricScanning,
                isSuccess = uiState.biometricScanSuccess,
                onDismiss = { viewModel.dismissBiometricDialog() }
            )
        }
    }
}
