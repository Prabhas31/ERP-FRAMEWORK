# 📱 Emergency Phone Recovery Framework (ERP Framework)

## 🔥 Project Overview
The Emergency Phone Recovery Framework (ERP Framework) is an Android-based mobile security system designed to help users recover lost or stolen smartphones. It provides a secure, database-driven approach to device tracking, authentication, and recovery.

The system ensures that even after reinstallation or unauthorized access, the device can be tracked and controlled securely.

---

## 🎯 Problem Statement
Mobile theft and loss are increasing rapidly, and existing solutions often lack a proper database-driven recovery system. Most applications fail to provide:

- Secure recovery mechanisms  
- Persistent tracking after reinstall  
- Strong authentication layers  
- Structured device ownership verification  

This project solves these issues by implementing a secure and structured recovery framework using modern mobile technologies.

---

## 🚀 Key Features

- 🔐 **App Lock Security System**  
- 📍 **Live Location Tracking**  
- 🚨 **Stolen Mode Activation**  
- 🔑 **Recovery Password & Physical Key System**  
- 📧 **Trusted Email Verification**  
- 🔄 **Reinstallation Recovery Verification**  
- ⚡ **Background Security Services**  
- 🛡️ **Secure Data Handling with Firebase**

---

## 🧠 How It Works

1. **User Setup**
   - User registers recovery email and password  
   - Security keys are generated  

2. **Background Monitoring**
   - App runs background services  
   - Tracks device behavior and status  

3. **Theft Detection**
   - Stolen mode can be activated manually or automatically  
   - Device enters restricted secure mode  

4. **Recovery Process**
   - User authenticates using:
     - Recovery password  
     - Trusted email  
     - Physical recovery key  

5. **Tracking & Control**
   - Live location is fetched  
   - Device access is restricted  

---

## 🛠️ Tech Stack

- **Language:** Kotlin  
- **Platform:** Android Studio  
- **Backend:** Firebase  
- **Database:** Firebase Realtime Database / Firestore  
- **Authentication:** Custom Authentication + Email Verification  

---

## 📂 Project Structure
EmergencyPhoneRecovery/
│── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/prabhas/emergencyphonerecovery/
│ │ │ ├── res/
│ │ │ └── AndroidManifest.xml
│── functions/
│── public/
│── gradle/
│── build.gradle.kts
│── settings.gradle.kts



---

## ⚙️ Installation & Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/Prabhas31/ERP-FRAMEWORK.git


Step 2: Open in Android Studio
Open Android Studio
Click Open Project
Select the cloned folder
Step 3: Sync Project
Wait for Gradle to sync
Dependencies will install automatically
Step 4: Run Application
Connect Android device or use emulator
Click ▶️ Run

