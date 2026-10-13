# 🏛️ TenderAnalyser — Android Client

An AI-powered, full-stack mobile application designed for procurement management, post-bidding feedback workflows, and competitive analytics. Built natively for Android using Kotlin, Jetpack Compose, and Retrofit.

> 🔗 **Backend Repository:** Looking for the API service? Check out the [TenderAnalyser Backend Service](https://github.com/aravinth016/TenderAnalyser-Flask-Backend).

---

## 🌟 Key Features

* **Dual-Portal System:** Dedicated, role-based workflows for both **Tendering Authorities** and **Commercial Bidders**.
* **Post-Bidding Feedback Engine:** Dynamic generation and viewing of official **Award Certificates** for winning bids and **Notices of Non-Selection** for outbid participants.
* **Competitive Comparison Analytics:** Interactive financial breakdowns, L1 pricing gap analysis, market average metrics, and AI-driven competitive insights.
* **Live Bid Tracking Pipeline:** Multi-stage status tracking covering Technical Review, Financial Opening, and Final Award distribution.
* **Modern UI & Navigation:** Declarative UI built with Jetpack Compose, Material 3, and deep-link routing.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Asynchronous Logic:** Kotlin Coroutines & StateFlow
* **Networking:** Retrofit 2 & OkHttp3
* **Navigation:** Jetpack Navigation Component
* **Architecture:** MVVM (Model-View-ViewModel) pattern with unidirectional data flow

---

## 📱 Application Screenshots

| My Bids Dashboard | Official Award Certificate | Competitive Analytics |
| :---: | :---: | :---: |
|  ![My Bids Dashboard]([https://www.figma.com/design/MpIC8dTL5IiiVJaanvOpEa/PDD?node-id=285-2&t=EohVDSURKYqpcyMd-4]) | ![Award Letter]([https://www.figma.com/design/MpIC8dTL5IiiVJaanvOpEa/PDD?node-id=285-2&t=EohVDSURKYqpcyMd-4]) | ![Analytics]([https://www.figma.com/design/MpIC8dTL5IiiVJaanvOpEa/PDD?node-id=285-2&t=EohVDSURKYqpcyMd-4]) |


---

## 🚀 Getting Started

### Prerequisites
* Android Studio (Jellyfish / Koala or later)
* JDK 17 or higher
* Android SDK 24 (Android 7.0) minimum target


### Installation & Local Run
1. **Clone the repository:**
   ```bash
 git clone [https://github.com/aravinth016/TenderAnalyser-Frontend-Kotlin.git](https://github.com/aravinth016/TenderAnalyser-Frontend-Kotlin.git)

2.Open in Android Studio:

 Select File ➔ Open and navigate to the project directory.

3.Configure Environment Variables:

 Open local.properties in your project root and specify your backend base URL:

4.Properties
 BASE_URL=[https://your-render-backend.onrender.com/](https://your-render-backend.onrender.com/)
 Build and Run:

 Sync Gradle dependencies and run the project on an emulator or physical Android device.

📦 Download Compiled APK
To test the live application directly on your Android phone without setting up Android Studio:
👉 Download TenderAnalyser v1.0.0 APK
