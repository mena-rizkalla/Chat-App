# 🚀 ChatApp: A Modern Android Messenger

![ChatApp Banner](https://placehold.co/1200x400/00639B/FFFFFF?text=ChatApp&font=poppins)

**ChatApp** is a fully-featured, real-time messaging application for Android, built with the latest technologies. It showcases a modern, clean, and animated user interface following Material 3 design principles. Connect with users in private chats, join the global conversation, or even chat with a Gemini-powered AI!

---

## ✨ Features

- **🔒 Authentication**: Secure user sign-up and login using Firebase Authentication. Includes a "Forgot Password" flow.
- **👤 User Directory**: Browse a list of all registered users to start new conversations.
- **💬 Private & Global Chat**: Engage in one-on-one private messaging or join the public global chat room.
- **🤖 AI Chat**: Have a conversation with Google's Gemini, integrated directly into the app.
- **😍 Message Reactions**: React to messages with emojis (❤️, 👍, 😂, 😢, 😠).
- **🎨 Modern UI**: A beautiful and responsive UI built with Jetpack Compose and Material 3, supporting both light and dark themes.
- **🚀 Real-time Communication**: Messages and reactions appear instantly thanks to Firebase Firestore's real-time listeners.
- **✨ Fluid Animations**: Smooth and fluid animations for message appearance and UI transitions, enhancing the user experience.

## 📸 Screenshots

Here's a glimpse of the beautiful new interface you'll experience in ChatApp.

| Users List | Private Chat | Global Chat |
| :---: | :---: | :---: |
| ![Users Screen](https://github.com/user-attachments/assets/fca732ec-3479-4700-ab4b-fb7efd85e367) | ![Private Chat Screen](https://github.com/user-attachments/assets/a1d4068e-d10d-4406-9402-544ee4909d3c) | ![Global Chat Screen](https://github.com/user-attachments/assets/6c83bda5-ec3f-4f98-a81a-2a48992cea4d) |
| **AI Chat** | **Login Screen** | **Sign Up Screen** |
| ![AI Chat Screen](https://github.com/user-attachments/assets/55895599-4c74-4a16-9972-c9d7971a1df5) | ![Login Screen](https://github.com/user-attachments/assets/a8c6233a-0ea0-439c-948d-c5b34434c5a0) | ![Sign Up Screen](https://github.com/user-attachments/assets/b7af567f-ddd8-4144-aef3-a7abd0938cbd) |


---

## 🛠️ Tech Stack & Architecture

This project leverages a modern Android development stack:

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the UI declaratively with Kotlin.
- **Architecture**: *Model-View-Intent (MVI)** using a single state object (`UiState`) and unidirectional data flow to ensure a predictable and maintainable state management.
- **Dependency Injection**: [Koin](https://insert-koin.io/) for managing dependencies and injecting ViewModels.
- **Backend**: [Firebase](https://firebase.google.com/)
    - **Firestore**: For real-time database functionality (messages, users, reactions).
    - **Authentication**: For handling user accounts.
- **Asynchronous Programming**: Kotlin Coroutines and Flows for managing background tasks and handling data streams.
- **Navigation**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation) to handle screen transitions.

---

## ⚙️ Setup & Installation

To get this project running on your own machine, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/mena-rizkalla/chatapp.git](https://github.com/mena-rizkalla/chatapp.git)
    ```

2.  **Connect to Firebase:**
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project.
    - Add an Android app to your Firebase project with the package name `com.example.chatapp`.
    - Download the `google-services.json` file and place it in the `app/` directory of your project.
    - In the Firebase Console, enable **Authentication** (with the Email/Password provider) and **Firestore Database**.

3.  **Build and Run:**
    - Open the project in Android Studio.
    - Let Gradle sync the dependencies.
    - Build and run the app on an emulator or a physical device.
---

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features or find a bug, please feel free to open an issue or submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request
