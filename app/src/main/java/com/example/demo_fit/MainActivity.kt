package com.example.demo_fit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demo_fit.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.Arrays

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 21
    // Data binding instance for the activity
    private lateinit var mBinding: ActivityMainBinding
    // Reference to the active fragment
    private lateinit var mActiveFragment: Fragment
    // Fragment manager for managing fragments
    private lateinit var mFragmentManager: FragmentManager

    // Autenticacion de usuarios.
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth : FirebaseAuth? = null

    // Override the onCreate method to inflate the layout and setup bottom navigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using data binding
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        // Set the content view to the inflated layout
        setContentView(mBinding.root)

        setupAuth()

        //fijamos el menu principal
        setupBottomNav()
    }

    private fun setupAuth() {
        //traemos la autenticacion
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            // usuario sin logearse, muestra el inicio de sesion
            if(user == null){
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    //.setIsSmartLockEnabled(false) si quiere que pida una cuenta
                    .setAvailableProviders(
                        Arrays.asList(
                            AuthUI.IdpConfig.EmailBuilder().build(), // login con correo
                            AuthUI.IdpConfig.GoogleBuilder().build() // login con google
                        )
                    )
                    .build(),RC_SIGN_IN)
            }
        }
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

    override fun onResume() {
        super.onResume()
        //si se sale o pasa algo el vuelve a capturar el usuario
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        // si se pausa la app, se remueve la autenticacion mientras el usuario esta en otra app o similar
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                //El usuario pudo iniciar sesion or primera vez
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            }else{
                //esto significa que el usuario cancela la actividad de iniciar sesion
                if(IdpResponse.fromResultIntent(data) == null){
                    finish()
                }
            }
        }
    }
}