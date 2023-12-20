package com.example.testappforclicklead.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testappforclicklead.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    // выход из приложения по нажатию на кнопку НАЗАД
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}