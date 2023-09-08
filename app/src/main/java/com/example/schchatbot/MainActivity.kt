package com.example.schchatbot

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.schchatbot.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fl: FrameLayout by lazy {
        findViewById(R.id.containers)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnClickListener{view ->
            Snackbar.make(view, "rersre", Snackbar.LENGTH_LONG).setAction("Action", null).show()

        }

        val bnv_main = findViewById<BottomNavigationView>(R.id.bottom_navigationview)

        bnv_main.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.navigation_manual-> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,  R.color.click_manual)
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.click_manual)
                        ManualFragment()
                        // Respond to navigation item 1 click
                    }
                    R.id.navigation_home -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.click_home)
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.click_home)
                        HomeFragment()
                        // Respond to navigation item 2 click
                    }
                    else -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this, R.color.click_user)
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this, R.color.click_user)
                        UserFragment()
                        // Respond to navigation item 3 click
                    }
                }
            )
            true
        }
        bnv_main.selectedItemId = R.id.navigation_home

    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containers, fragment)
            .commit()
    }
}