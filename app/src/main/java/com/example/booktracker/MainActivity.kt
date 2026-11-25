package com.example.booktracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: BookApiService

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var resultsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout = findViewById<View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                16 + systemBars.left,
                16 + systemBars.top,
                16 + systemBars.right,
                16 + systemBars.bottom
            )

            insets
        }

        apiService = BookApiService()

        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
        resultsContainer = findViewById(R.id.resultsContainer)

        btnSearch.setOnClickListener {
            searchBooks()
        }
    }

    private fun searchBooks() {

        val query = etSearch.text.toString().trim()

        if (query.isEmpty()) {
            etSearch.error = getString(R.string.search_empty_error)
            return
        }

        progressBar.visibility = View.VISIBLE
        btnSearch.isEnabled = false
        tvStatus.text = getString(R.string.searching_books)
        resultsContainer.removeAllViews()

        executor.execute {

            try {
                val books = apiService.searchBooks(query)

                mainHandler.post {

                    progressBar.visibility = View.GONE
                    btnSearch.isEnabled = true

                    resultsContainer.removeAllViews()

                    if (books.isEmpty()) {
                        tvStatus.text = getString(R.string.no_books_found)
                    } else {
                        tvStatus.text = getString(
                            R.string.books_found,
                            books.size
                        )

                        displayBooks(books)
                    }
                }

            } catch (e: Exception) {

                Log.e("BookTracker", "Book search failed", e)

                mainHandler.post {

                    progressBar.visibility = View.GONE
                    btnSearch.isEnabled = true

                    tvStatus.text = getString(R.string.network_error)
                }
            }
        }
    }

    private fun displayBooks(books: List<Book>) {

        for ((title, author, firstPublishYear) in books) {

            val bookText = TextView(this)

            val year = firstPublishYear?.toString()
                ?: getString(R.string.publication_year_unavailable)

            bookText.text = getString(
                R.string.book_result,
                title,
                author,
                year
            )

            bookText.textSize = 16f
            bookText.setPadding(16, 16, 16, 16)

            resultsContainer.addView(bookText)

            val divider = View(this)
            divider.setBackgroundColor(0xFFDDDDDD.toInt())

            resultsContainer.addView(
                divider,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
