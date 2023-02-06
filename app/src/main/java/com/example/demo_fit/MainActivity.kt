package com.example.demo_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo_fit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //fijamos el menu principal
        setupBottomNav()
    }
    private fun setupBottomNav(){
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.hostFragment, HomeFragment()).commit()
    }
}