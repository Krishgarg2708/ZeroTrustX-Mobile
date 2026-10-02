package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RiskLevel
import com.example.data.repository.SecurityRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ZeroTrustX Mobile", appName)
    }

    @Test
    fun `security repository initial authentication state`() {
        val repo = SecurityRepository()
        assertTrue(!repo.isAuthenticated.value)

        // Valid demo login
        val loginSuccess = repo.signIn("admin@zerotrustx.demo", "Demo@123")
        assertTrue(loginSuccess)
        assertTrue(repo.isAuthenticated.value)

        // Valid demo MFA
        val mfaSuccess = repo.verifyMfa("482169")
        assertTrue(mfaSuccess)
        assertTrue(repo.isMfaVerified.value)
    }

    @Test
    fun `risk engine calculation for low and high risk contexts`() {
        val repo = SecurityRepository()

        // High risk scenario: unfamiliar location, unverified MFA, unknown device
        val highRiskDecision = repo.evaluateZeroTrustDecision(
            resourceName = "Production Database",
            location = "Unknown Geo Location",
            isKnownDevice = false,
            isMfaVerified = false
        )
        assertTrue(highRiskDecision.calculatedRiskScore > 50)

        // Low risk scenario: managed device, corporate network in Delhi, verified MFA
        val lowRiskDecision = repo.evaluateZeroTrustDecision(
            resourceName = "Salesforce",
            location = "Delhi, IN",
            isKnownDevice = true,
            isMfaVerified = true
        )
        assertTrue(lowRiskDecision.calculatedRiskScore <= 35)
    }
}
