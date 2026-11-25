
package com.example.booktracker

import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class BookApiService {

    private val baseUrl = "https://openlibrary.org"

    private val searchEndpoint = "$baseUrl/search.json"

    fun searchBooks(query: String): List<Book> {

        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        val url = URL("$searchEndpoint?q=$encodedQuery&limit=10")

        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("API request failed: HTTP $responseCode")
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val jsonObject = JSONObject(response)

            val documents = jsonObject.getJSONArray("docs")

            val books = mutableListOf<Book>()

            for (i in 0 until documents.length()) {

                val document = documents.getJSONObject(i)

                val title = document.optString("title", "Unknown title")

                val authors = document.optJSONArray("author_name")

                val author = if (authors != null && authors.length() > 0) {
                    authors.optString(0, "Unknown author")
                } else {
                    "Unknown author"
                }

                val year = if (
                    document.has("first_publish_year") &&
                    !document.isNull("first_publish_year")
                ) {
                    document.optInt("first_publish_year")
                } else {
                    null
                }

                books.add(
                    Book(
                        title = title,
                        author = author,
                        firstPublishYear = year
                    )
                )
            }

            return books

        } finally {
            connection.disconnect()
        }
    }
}
