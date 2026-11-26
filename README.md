# BookTracker
BookTracker is an Android app built in Kotlin that lets you search for books through the Open Library API and save them to a local library on your device. I built this project to practice combining clean network requests with reliable, offline-first local storage.

## Features
- Live Book Search: Query titles and authors directly from the Open Library Search API.
- Rotation Safe UI: Uses a ViewModel and StateFlow to handle Idle, Loading, Success, and Error states cleanly, ensuring screen rotations don't trigger duplicate network calls or lose your search results.
- Smart Local Imports: Saves books into a Room database using a single transaction that finds or creates the author first and prevents duplicate book entries.
- Automatic UI Updates: Observes database changes via Kotlin Flow, so your saved reading list updates automatically the moment a book is imported.

## Built With
- Kotlin And Coroutines for asynchronous background work
- Retrofit And Gson for Open Library API integration
- Room (with KSP) for local SQLite database management
- MVVM Architecture (ViewModel, StateFlow, repeatOnLifecycle)

## Getting Started
- Clone the repository:

* Bash
git clone https://github.com/yourusername/BookTracker.git
** Open in Android Studio:
Open the project folder and let Gradle sync the dependencies.

Run the app:
Select an emulator or physical Android device (Android 7.1 / API 25 or higher) and click Run.

Project Overview
Plaintext
com.example.booktracker
├── OpenLibraryApi.kt      # Retrofit interface and shared API client
├── OpenLibraryModels.kt   # Gson data classes for API responses
├── BookDatabase.kt        # Room entities, DAO transactions, and database setup
├── BookViewModel.kt       # Manages search states and database streams
└── MainActivity.kt        # Observes UI state and handles user interaction
Future Improvements
Add book cover thumbnails using the Open Library Covers API

Include reading status filters (e.g., Want to Read, Reading, Finished)

Add search debouncing so queries run automatically as you type
