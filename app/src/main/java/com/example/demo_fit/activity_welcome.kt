package com.example.demo_fit
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2


class activity_welcome : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        viewPager = findViewById(R.id.viewPager)

        val images = listOf(
            R.drawable.img001,
            R.drawable.img002,
            R.drawable.img003,
            R.drawable.img004,
            R.drawable.img005,
            R.drawable.img006
        )

        val adapter = ImageAdapter(images)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Si el usuario está en la última página, cierra la actividad
                if (position == images.size - 1) {
                    viewPager.postDelayed({
                        finish()
                    }, 2000)  // Cambia este valor para ajustar el tiempo de retardo antes de cerrar la actividad
                }
            }
        })
    }
}