# ZeroTrustX Mobile — Enterprise Zero Trust Platform

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https://github.com/krish-garg2708/ZeroTrustX)

> **"Verify Every Access. Trust Nothing by Default."**

ZeroTrustX Mobile is an enterprise Zero Trust Network Access (ZTNA) companion and security platform built to continuously verify user identities, device posture, location familiarity, network trust, and resource sensitivity before granting access.

---

## 🚀 Deploy on Vercel

This repository is pre-configured with `vercel.json` to deploy a zero-configuration global web command center and interactive mobile simulator on Vercel.

### Option 1: 1-Click Vercel Web Deployment
1. Push this project to your GitHub repository.
2. Go to [Vercel](https://vercel.com/new) and click **"Add New Project"**.
3. Select your GitHub repository and click **Deploy**.
4. Vercel will automatically serve the enterprise Zero Trust web command center located in `/public`.

### Option 2: Deploy via Vercel CLI
```bash
npm i -g vercel
vercel deploy --prod
```

---

## 📱 Native Android App (Jetpack Compose & Kotlin)

The native Android mobile app source code is located in `/app`.

### Generate and Install APK
1. **Direct in AI Studio:** Go to the Settings / Export menu in the top right to download the project ZIP or generate the APK.
2. **Via Gradle:**
   ```bash
   gradle :app:assembleDebug
   ```
   The APK output will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Install on Phone via ADB:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🛡️ Enterprise Features
- **Zero Trust Command Center:** Answers *Am I secure?*, *What requires my attention?*, and *Why is something considered risky?*
- **Explainable Access Decisions:** Full forensic breakdown on identity, MFA, TPM 2.0 posture, geolocation delta, and applied rules.
- **Continuous Trust Engine:** Dynamic score decay (92 → 74) upon context drift, requiring hardware biometric step-up verification.
- **Signature Zero Trust Flow:** 9-node interactive verification pipeline: Identity → MFA → Device → Location → Network → Resource → Risk Engine → Policy → Decision.
- **Policy Simulator:** Demo policy engine to test conditional access permutations.
- **Security Analytics & Threat Radar:** Real-time access trends, active threat telemetry, and risk distribution.
