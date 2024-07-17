package com.example.bmicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class ResultActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultTextView = findViewById<TextView>(R.id.textView)
        imageView = findViewById<ImageView>(R.id.imageView)
        // null 체크와 기본값 설정
        val height = intent.getStringExtra("height")?.toIntOrNull() ?: -1
        val weight = intent.getStringExtra("weight")?.toIntOrNull() ?: -1
        var name = intent.getStringExtra("name")

        if (height == -1 || weight == -1) {
            resultTextView.text = "유효하지 않은 입력입니다."
            return
        }
        //var height = intent.getStringExtra("height").toInt()
        //var weight = intent.getStringExtra("weight").toInt()

        //bmi계산
        var bmi = weight / Math.pow(height/100.0, 2.0)

        when {
            bmi >= 35 -> resultTextView.text = "고도 비만"
            bmi >= 30 -> resultTextView.text = "2단계 비만"
            bmi >= 25 -> resultTextView.text = "1단계 비만"
            bmi >= 23 -> resultTextView.text = "과체중"
            bmi >= 18.5 -> resultTextView.text = "정상"
            else -> resultTextView.text = "저체중"
        }

        //이미지 출력 부분
        when{
            bmi >= 23 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            bmi > 18.5 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            else ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_mood_bad_24)
        }

        Toast.makeText(this, "$name: $bmi", Toast.LENGTH_SHORT).show()
    }
}