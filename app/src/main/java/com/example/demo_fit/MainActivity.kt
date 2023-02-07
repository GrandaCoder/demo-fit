package com.example.demo_fit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demo_fit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Data binding instance for the activity
    private lateinit var mBinding: ActivityMainBinding

    // Reference to the active fragment
    private lateinit var mActiveFragment: Fragment
    // Fragment manager for managing fragments
    private lateinit var mFragmentManager: FragmentManager


    // Override the onCreate method to inflate the layout and setup bottom navigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using data binding
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        // Set the content view to the inflated layout
        setContentView(mBinding.root)

        //fijamos el menu principal
        setupBottomNav()
    }

    // Method for setting up the bottom navigation
    private fun setupBottomNav(){
        // Get the fragment manager
        mFragmentManager = supportFragmentManager

        //instanciamos todos los fragmentos disponibles en el proyecto
        val homeFragment = HomeFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()

        // Set the home fragment as the active fragment
        mActiveFragment = homeFragment

        //creamos los fragments, tiene que ir de forma inversa, es decir del ultimo al primero segun el menu
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment) // se oculta
            .commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, addFragment, AddFragment::class.java.name)
            .hide(addFragment) // se oculta
            .commit()

        // Add the home fragment
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name)
            .commit()

        // Set the listener for bottom navigation item selection
        mBinding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    // Show the home fragment, hide the active fragment
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    // Update the active fragment
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_add -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(addFragment).commit()
                    mActiveFragment = addFragment
                    true
                }

                R.id.action_profile -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(profileFragment).commit()
                    mActiveFragment = profileFragment
                    true
                }
                else -> false
            }
        }
    }
}