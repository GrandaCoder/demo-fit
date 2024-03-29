package com.example.demo_fit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demo_fit.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.Arrays

class MainActivity : AppCompatActivity(),MainAux {

    private lateinit var mBinding: ActivityMainBinding



    lateinit var mActiveFragment: Fragment
    var mFragmentManager: FragmentManager? = null

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth? = null

    private val authResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            // Lanzar la actividad de bienvenida
            val intent = Intent(this, activity_welcome::class.java)
            startActivity(intent)
        } else {
            if (IdpResponse.fromResultIntent(it.data) == null) {
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupAuth()
    }

    private fun setupAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { it ->
            if (it.currentUser == null) {
                authResult.launch(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                            listOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build())
                        )
                        .setLogo(R.drawable.dall_e_2023_02_11_08_47_32___mobile_exercise_application_logo)
                        .setTheme(R.style.GreenTheme)
                        .build()
                )
                mFragmentManager = null
            } else {
                SnapshotsApplication.currentUser = it.currentUser!!

                val fragmentProfile = mFragmentManager?.findFragmentByTag(ProfileFragment::class.java.name)
                fragmentProfile?.let {
                    (it as FragmentAux).refresh()
                }

                if (mFragmentManager == null) {
                    mFragmentManager = supportFragmentManager
                    setupBottomNav(mFragmentManager!!)
                }
            }
        }
    }

    private fun setupBottomNav(fragmentManager: FragmentManager) {
        mFragmentManager?.let { //clean before to prevent errors
            for (fragment in it.fragments) {
                it.beginTransaction().remove(fragment!!).commit()
            }
        }

        val homeFragment = HomeFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()
        val storeFragment = StoreFragment()
        val dietasFragment = DietasFragment()
        val excerciseFragment = ExceciseFragment()

        mActiveFragment = homeFragment

        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment,dietasFragment , DietasFragment::class.java.name)
            .hide(dietasFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment,storeFragment, StoreFragment::class.java.name)
            .hide(storeFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, addFragment, AddFragment::class.java.name)
            .hide(addFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, excerciseFragment, ExceciseFragment::class.java.name)
            .hide(excerciseFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name).commit()


        mBinding.btnCambiarUpluad.setOnClickListener {
            fragmentManager.beginTransaction().hide(mActiveFragment).show(addFragment).commit()
            mActiveFragment = addFragment
        }

        mBinding.btnPublicaciones.setOnClickListener {
            fragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
            mActiveFragment = homeFragment
        }

        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    esconderBotones(true)
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_exercise -> {
                    esconderBotones()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(excerciseFragment).commit()
                    mActiveFragment = excerciseFragment
                    true
                }
                //esto debe de modificarse
                /*
                R.id.action_add -> {
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(addFragment).commit()
                    mActiveFragment = addFragment
                    true
                } */
                R.id.action_profile -> {
                    esconderBotones()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(profileFragment).commit()
                    mActiveFragment = profileFragment
                    true
                }
                R.id.action_store -> {
                    esconderBotones()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(storeFragment).commit()
                    mActiveFragment = storeFragment
                    true
                }
                R.id.action_diets-> {
                    esconderBotones()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(dietasFragment).commit()
                    mActiveFragment = dietasFragment
                    true
                }
                else -> false
            }
        }

        mBinding.bottomNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.action_home -> (homeFragment as FragmentAux).refresh()
            }
        }
    }

    private fun esconderBotones(isVisible :Boolean = false){
        if (isVisible){
            mBinding.llCambiarVista.visibility = View.VISIBLE
        }else{
            mBinding.llCambiarVista.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)

        //barra aparecer

    }

    /*
    *   MainAux
    * */
    override fun showMessage(resId: Int, duration: Int) {
        Snackbar.make(mBinding.root, resId, duration)
            .setAnchorView(mBinding.bottomNav)
            .show()
    }
}