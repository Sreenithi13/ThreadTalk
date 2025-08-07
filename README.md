# 🧵 ThreadTalk - A Java-Based Multithreaded Console Chat Application
## 🚀 Features

- ✅ **User Authentication**
  - Signup and login system using SHA-256 password hashing
  - Email and password validation
- 💬 **Messaging**
  - Public and private messaging
  - Notifications for user join/leave events
- 🧵 **Multithreading**
  - Handles multiple clients simultaneously using threads
  - Thread-safe handling with `ConcurrentHashMap`
- 📡 **Client-Server Architecture**
  - Java Sockets for networking
  - Real-time communication via TCP
- 🔒 **Synchronization**
  - Ensures consistency and safety of shared resources
- 🧠 **Planned Features**
  - Sentiment analysis integration
  - Emoji support
  - Typing indicators and notification sounds
  - Chat history logging with timestamps

---

## 🧩 Project Structure

```bash
ThreadTalk/
├── ChatClient.java              # Client-side GUI/console
├── ClientHandler.java           # Server-side handler for each client
├── ThreadTalkServer.java        # Main server to handle connections
├── AuthenticationController.java# Handles login/signup logic
├── Database.java                # Stores user data
├── User.java                    # User model

## 🧩 How to Run
javac ThreadTalkServer.java ClientHandler.java AuthenticationController.java Database.java User.java
java ThreadTalkServer
javac ChatClient.java
java ChatClient



