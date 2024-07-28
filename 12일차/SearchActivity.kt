package com.example.guru_app_

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    lateinit var BackButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val books = mutableListOf<Book>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        BackButton = findViewById<ImageButton>(R.id.BackButton)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(books)
        recyclerView.adapter = bookAdapter

        BackButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val query = intent.getStringExtra("query")
        if (query != null) {
            fetchBooks(query)
        } else {
            Toast.makeText(this, "검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchBooks(query: String) {
        val thread = Thread {
            try {
                val result = getNaverBookSearch(query)
                runOnUiThread {
                    parseBookInfo(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "검색 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        thread.start()
    }

    private fun getNaverBookSearch(keyword: String): String {
        // 네이버 책 검색 API를 호출하는 코드
        val clientID = "qIL4TXtqeLNoiACa14G5"
        val clientSecret = "hPPiIzBqS3"
        val sb = StringBuilder()

        try {
            val text = URLEncoder.encode(keyword, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/search/book.json?query=$text"
            val url = URL(apiURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("X-Naver-Client-Id", clientID)
            connection.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
            } else {
                throw Exception("응답 코드: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun parseBookInfo(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")

            if (items != null) {
                for (i in 0 until items.length()) {
                    val item = items.optJSONObject(i)
                    if (item != null) {
                        val title = item.optString("title", "제목 없음")
                        val author = item.optString("author", "저자 없음")
                        val image = item.optString("image", "")
                        books.add(Book(title, author, image))
                    }
                }
                bookAdapter.notifyDataSetChanged()
            } else {
                Log.e("SearchActivity", "Items array is null")
                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "JSON 파싱 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
