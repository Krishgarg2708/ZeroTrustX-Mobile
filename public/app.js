// Interactive Logic for ZeroTrustX Vercel Web Portal & Mobile Simulator

let currentScore = 74;
let isChallenged = true;
let activeMobileTab = 'dashboard';

// 9 Stages of Signature Zero Trust Flow
const flowStages = {
  identity: {
    title: '1. IDENTITY VERIFICATION',
    desc: 'Cryptographic JWT issued by Okta/Azure AD enterprise enclave. Nonce valid for 30 minutes. Phishing-resistant credentials verified explicitly.',
    rule: 'Zero Trust Principle: "Verify identity explicitly. Never trust standing sessions."'
  },
  mfa: {
    title: '2. MULTI-FACTOR AUTHENTICATION',
    desc: 'FIDO2 / WebAuthn hardware security key biometric touch verified. Insecure legacy SMS and telephony OTP methods strictly blocked.',
    rule: 'Zero Trust Principle: "Enforce phishing-resistant MFA on all privileged asset access."'
  },
  device: {
    title: '3. ENDPOINT INTEGRITY & ATTESTATION',
    desc: 'Device TPM 2.0 hardware health attestations clean. FileVault 2 / LUKS disk encryption active. Kernel integrity verified by continuous EDR agent.',
    rule: 'Zero Trust Principle: "Assume device breach until verified by continuous telemetry."'
  },
  location: {
    title: '4. GEOLOCATION CONTEXT & VELOCITY',
    desc: 'IP and GPS coordinates correlated with corporate baseline. Physical impossible travel velocity calculation verified (<800 km/h).',
    rule: 'Zero Trust Principle: "Context-aware boundaries supersede static network perimeters."'
  },
  network: {
    title: '5. NETWORK TRUST & MICROSEGMENTATION',
    desc: 'WireGuard mutual TLS 1.3 tunnel established directly to protected asset. Direct perimeter subnet routing completely disabled.',
    rule: 'Zero Trust Principle: "Treat all networks—including internal corporate LAN—as hostile."'
  },
  resource: {
    title: '6. ASSET CLASSIFICATION & SENSITIVITY',
    desc: 'Target sensitivity ranked as CONFIDENTIAL / HIGH IMPACT. Ephemeral read-only query scope applied with 30-minute strict TTL.',
    rule: 'Zero Trust Principle: "Access granted per resource, never to the broader network."'
  },
  risk: {
    title: '7. DYNAMIC RISK ENGINE AGGREGATOR',
    desc: 'Composite risk score evaluated at 28/100 (LOW). Aggregates signals across identity, device posture, geographic velocity, and asset value.',
    rule: 'Zero Trust Principle: "Risk is continuous and dynamically calculated on every request."'
  },
  policy: {
    title: '8. POLICY ENGINE & LEAST PRIVILEGE',
    desc: 'Policy rule #ZT-402 evaluated against requester RBAC permissions. Dual-custody requirement verified for production database cluster.',
    rule: 'Zero Trust Principle: "Enforce Least Privilege: Zero standing administrative rights."'
  },
  decision: {
    title: '9. FINAL ACCESS DECISION',
    desc: 'ALLOW (LEAST PRIVILEGE TOKEN ISSUED). Ephemeral access certificate minted for 30 minutes with continuous session revocation heartbeat.',
    rule: 'Zero Trust Core Axiom: "Never trust. Always verify."'
  }
};

// Inspect Flow Node in Web Showcase
function inspectNode(stageKey) {
  const stage = flowStages[stageKey];
  if (!stage) return;

  document.querySelectorAll('.flow-node').forEach(el => el.classList.remove('active-node'));
  event.currentTarget.classList.add('active-node');

  document.getElementById('node-detail-title').innerText = `Selected Stage: ${stage.title}`;
  document.getElementById('node-detail-desc').innerText = stage.desc;
  document.getElementById('node-detail-rule').innerText = stage.rule;
}

// Continuous Trust Simulator Controls
function simulateDrift(reason, score, challenged) {
  currentScore = score;
  isChallenged = challenged;

  document.getElementById('current-score-text').innerText = score;
  document.getElementById('trust-condition-text').innerText = `Context Shift: ${reason}`;

  const alertBox = document.getElementById('trust-alert-box');
  const scoreText = document.getElementById('current-score-text');
  const pill = document.getElementById('trust-state-pill');

  if (challenged) {
    alertBox.style.display = 'flex';
    scoreText.className = 'metric-val highlight';
    pill.className = 'badge badge-danger';
    pill.innerText = 'CHALLENGED';
  } else {
    alertBox.style.display = 'none';
    scoreText.className = 'metric-val';
    scoreText.style.color = 'var(--green)';
    pill.className = 'badge badge-normal';
    pill.innerText = 'TRUSTED';
  }

  renderMobileView();
}

function satisfyTrust() {
  simulateDrift('Hardware biometric FIDO2 key re-verified on mobile secure enclave.', 92, false);
}

// Mobile Simulator View Renderer
function switchTab(tab) {
  activeMobileTab = tab;
  document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
  event.currentTarget.classList.add('active');
  renderMobileView();
}

function renderMobileView() {
  const container = document.getElementById('mobile-view-container');
  if (!container) return;

  if (activeMobileTab === 'dashboard') {
    container.innerHTML = `
      <div style="background:#151D2F; border:1px solid #22314E; border-radius:12px; padding:12px;">
        <div style="font-size:9px; color:#06B6D4; font-weight:800;">SECURITY SCORE</div>
        <div style="font-size:28px; font-weight:800; color:#fff; margin:2px 0;">87 <span style="font-size:12px; color:#94A3B8;">/ 100</span></div>
        <div style="font-size:10px; color:#10B981; font-weight:bold;">Protected • Zero Trust Active</div>
      </div>

      <div style="background:#182236; border:1px solid ${isChallenged ? '#EF4444' : '#22314E'}; border-radius:10px; padding:10px;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <span style="font-size:9px; font-weight:bold; color:#06B6D4;">DYNAMIC TRUST</span>
          <span style="font-size:8px; background:${isChallenged ? '#EF4444' : '#10B981'}; color:#fff; padding:1px 5px; border-radius:4px; font-weight:bold;">${isChallenged ? 'STEP-UP' : 'HEALTHY'}</span>
        </div>
        <div style="font-size:16px; font-weight:bold; color:#fff; margin-top:4px;">92 ➔ <span style="color:${isChallenged ? '#EF4444' : '#10B981'}">${currentScore}</span></div>
        <div style="font-size:9px; color:#94A3B8; margin-top:2px;">Context shifted outside trust threshold.</div>
      </div>

      <div style="background:#151D2F; border:1px solid #22314E; border-radius:10px; padding:10px;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <span style="font-weight:bold; font-size:11px; color:#fff;">Production Database</span>
          <span style="font-size:8px; background:#450A0A; color:#EF4444; border:1px solid #B91C1C; padding:1px 4px; border-radius:4px; font-weight:bold;">HIGH RISK</span>
        </div>
        <div style="font-size:9px; color:#94A3B8; margin:2px 0;">MacBook Pro • Mumbai, IN</div>
        <div style="display:flex; gap:6px; margin-top:8px;">
          <button style="flex:1; background:transparent; border:1px solid #EF4444; color:#EF4444; border-radius:6px; font-size:9px; font-weight:bold; padding:4px;" onclick="alert('Access Request Denied')">DENY</button>
          <button style="flex:1; background:#3B82F6; border:none; color:#fff; border-radius:6px; font-size:9px; font-weight:bold; padding:4px;" onclick="alert('Access Request Approved for 30m')">APPROVE</button>
        </div>
      </div>

      <div style="background:#151D2F; border:1px solid #22314E; border-radius:10px; padding:10px;">
        <div style="font-size:9px; font-weight:bold; color:#06B6D4;">ACTIVE ENDPOINTS</div>
        <div style="font-size:10px; color:#fff; margin-top:4px;">• Pixel 9 Pro (This Device) - 98%</div>
        <div style="font-size:10px; color:#fff;">• MacBook Pro 16" - 94%</div>
      </div>
    `;
  } else if (activeMobileTab === 'flow') {
    container.innerHTML = `
      <div style="font-size:12px; font-weight:800; color:#06B6D4; margin-bottom:6px;">SIGNATURE ZERO TRUST PIPELINE</div>
      <div style="font-size:9px; color:#94A3B8; margin-bottom:8px;">"Never trust. Always verify."</div>
      
      <div style="display:flex; flex-direction:column; gap:6px;">
        <div style="background:#151D2F; border:1px solid #10B981; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">1. IDENTITY: Verified Alex Morgan</div>
        <div style="background:#151D2F; border:1px solid #10B981; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">2. MFA: FIDO2 Hardware Key</div>
        <div style="background:#151D2F; border:1px solid #10B981; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">3. DEVICE: TPM 2.0 & Encrypted</div>
        <div style="background:#151D2F; border:1px solid #F59E0B; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">4. LOCATION: Mumbai Delta Flagged</div>
        <div style="background:#151D2F; border:1px solid #10B981; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">5. NETWORK: WireGuard mTLS 1.3</div>
        <div style="background:#151D2F; border:1px solid #06B6D4; border-radius:8px; padding:6px 10px; font-size:10px; font-weight:bold;">6. DECISION: ALLOW (30m TTL)</div>
      </div>
    `;
  } else if (activeMobileTab === 'policy') {
    container.innerHTML = `
      <div style="font-size:12px; font-weight:800; color:#06B6D4; margin-bottom:4px;">DEMO POLICY SIMULATOR</div>
      <div style="font-size:9px; color:#94A3B8; margin-bottom:8px;">Test Zero Trust conditional access rules</div>

      <div style="background:#151D2F; border:1px solid #22314E; border-radius:8px; padding:8px; font-size:10px; display:flex; flex-direction:column; gap:6px;">
        <div><strong>User:</strong> Alex Morgan (SecOps)</div>
        <div><strong>Endpoint:</strong> Compliant MacBook Pro</div>
        <div><strong>Location:</strong> Mumbai Branch</div>
        <div><strong>Network:</strong> mTLS Tunnel</div>
        <div><strong>Resource:</strong> Production PostgreSQL</div>
      </div>

      <div style="background:#182236; border:1px solid #06B6D4; border-radius:8px; padding:8px; margin-top:8px;">
        <div style="font-size:9px; color:#94A3B8;">SIMULATED RISK SCORE</div>
        <div style="font-size:18px; font-weight:800; color:#F59E0B;">54 / 100 (MEDIUM)</div>
        <div style="font-size:9px; color:#10B981; font-weight:bold; margin-top:2px;">VERDICT: STEP-UP MFA REQUIRED</div>
      </div>
    `;
  } else if (activeMobileTab === 'devices') {
    container.innerHTML = `
      <div style="font-size:12px; font-weight:800; color:#06B6D4; margin-bottom:6px;">TRUSTED ENDPOINTS (4)</div>
      <div style="display:flex; flex-direction:column; gap:6px;">
        <div style="background:#151D2F; border:1px solid #22314E; border-radius:8px; padding:8px;">
          <div style="font-size:10px; font-weight:bold; color:#fff;">Pixel 9 Pro (This Device)</div>
          <div style="font-size:8px; color:#10B981;">Trusted • Android 16 • Score: 98%</div>
        </div>
        <div style="background:#151D2F; border:1px solid #22314E; border-radius:8px; padding:8px;">
          <div style="font-size:10px; font-weight:bold; color:#fff;">MacBook Pro 16"</div>
          <div style="font-size:8px; color:#10B981;">Trusted • macOS Sequoia • Score: 94%</div>
        </div>
        <div style="background:#151D2F; border:1px solid #22314E; border-radius:8px; padding:8px;">
          <div style="font-size:10px; font-weight:bold; color:#fff;">ThinkPad X1 Carbon</div>
          <div style="font-size:8px; color:#10B981;">Trusted • Win 11 Ent • Score: 88%</div>
        </div>
        <div style="background:#151D2F; border:1px solid #EF4444; border-radius:8px; padding:8px;">
          <div style="font-size:10px; font-weight:bold; color:#fff;">iPhone 17 Pro</div>
          <div style="font-size:8px; color:#EF4444;">Untrusted • Firewall Disabled • Score: 76%</div>
        </div>
      </div>
    `;
  } else if (activeMobileTab === 'alerts') {
    container.innerHTML = `
      <div style="font-size:12px; font-weight:800; color:#06B6D4; margin-bottom:6px;">ACTIVE THREAT ALERTS (2)</div>
      <div style="display:flex; flex-direction:column; gap:6px;">
        <div style="background:#151D2F; border:1px solid #EF4444; border-radius:8px; padding:8px;">
          <div style="font-size:9px; font-weight:bold; color:#EF4444;">CRITICAL: Impossible Travel</div>
          <div style="font-size:8px; color:#94A3B8; margin-top:2px;">Session in Mumbai within 12 min of Delhi baseline.</div>
        </div>
        <div style="background:#151D2F; border:1px solid #F59E0B; border-radius:8px; padding:8px;">
          <div style="font-size:9px; font-weight:bold; color:#F59E0B;">HIGH: Disabled Firewall</div>
          <div style="font-size:8px; color:#94A3B8; margin-top:2px;">Endpoint iPhone 17 Pro unencrypted network packet.</div>
        </div>
      </div>
    `;
  }
}

// Live Clock Updater
function updateClock() {
  const clockEl = document.getElementById('live-clock');
  if (!clockEl) return;
  const now = new Date();
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  clockEl.innerText = `${hours}:${minutes}`;
}

setInterval(updateClock, 1000);
document.addEventListener('DOMContentLoaded', () => {
  updateClock();
  renderMobileView();
});
