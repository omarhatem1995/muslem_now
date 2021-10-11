package com.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    var firstFragment = HomeFragment()
    var secondFragment = MoreFragment()
    var thirdFragment = ThirdFragment()
    var moreFragment = MoreFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(firstFragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.home -> replaceFragment(firstFragment)
                R.id.quran -> replaceFragment(secondFragment)
                R.id.azkar -> replaceFragment(thirdFragment)
                R.id.more -> replaceFragment(moreFragment)
            }
            true
        }

    }


    private fun replaceFragment(fragment:Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flFragment , fragment)
            transaction.commit()
        }
    }


}