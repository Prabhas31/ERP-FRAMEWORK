const { onValueWritten } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");
const sgMail = require("@sendgrid/mail");

// ✅ Initialize Firebase Admin with correct DB URL
admin.initializeApp({
  databaseURL:
    "https://emergencyphonerecovery-default-rtdb.asia-southeast1.firebasedatabase.app",
});

// ✅ Load SendGrid API key from Firebase environment config
sgMail.setApiKey(process.env.SENDGRID_KEY);

/**
 * DEMO EMAIL FUNCTION
 * -------------------
 * Triggered when a new share session is created.
 * Attempts to send email once.
 * Logs SendGrid rejection clearly (free-tier limitation).
 */
exports.sendRecoveryEmailOnSessionCreate = onValueWritten(
  {
    ref: "users/{ownerId}/share_sessions/{sessionId}",
    region: "asia-southeast1",
  },
  async (event) => {
    console.log("🔥 EMAIL FUNCTION TRIGGERED");

    const after = event.data.after.val();
    const { ownerId, sessionId } = event.params;

    if (!after) {
      console.log("❌ Session missing, exiting");
      return;
    }

    console.log("🟢 Processing session:", ownerId, sessionId);
    console.log("📧 Attempting SendGrid email (DEMO MODE)");

    // 🚨 DEMO: Forced send to show SendGrid behavior
    return sgMail
      .send({
        to: "prabhas.anumula@gmail.com", // demo receiver
        from: {
          email: "prabhas.anumula@gmail.com", // VERIFIED sender
          name: "Emergency Phone Recovery",
        },
        subject: "🚨 EmergencyPhoneRecovery – Email Pipeline Demo",
        text:
          "This is a demo email triggered by Firebase Cloud Functions. " +
          "Delivery may fail due to SendGrid free-tier restrictions.",
      })
      .then(() => {
        console.log(
          "✅ EMAIL SENT SUCCESSFULLY (This would work on a paid SendGrid account)"
        );
      })
      .catch((err) => {
        console.error("❌ SENDGRID REJECTED EMAIL");
        console.error(
          "📛 Reason:",
          err?.response?.body || err.message
        );
        console.error(
          "ℹ️ Note: This rejection is expected on SendGrid free-tier accounts."
        );
      });
  }
);
