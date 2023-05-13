package com.example.demo_fit

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import android.Manifest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference

class FotoPerfil : AppCompatActivity() {

    private var imageSelectIv: ImageView? = null
    private var SaveImage: Button? = null
    private var uri: Uri? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseStorage: FirebaseStorage? = null
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_perfil)

        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        imageSelectIv = findViewById(R.id.imageSelect_iv)
        SaveImage = findViewById(R.id.SaveImage)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        imageSelectIv!!.setOnClickListener {
            uploadImage()
        }

        SaveImage!!.setOnClickListener {
            subirimage()
        }

    }


    private fun subirimage() {
        val user = auth.currentUser
        val filename = "Userfoto" //UUID.randomUUID().toString()
        //val ref = storageReference.child("images/${user?.uid}/$filename")
        val reference = firebaseStorage!!.reference.child("Images/${user?.uid}/$filename")//.child(System.currentTimeMillis().toString() + "")
        reference.putFile(uri!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { uri ->
                val model = Model()
                model.image = uri.toString()
                firebaseDatabase!!.reference.child("Imagenes").push()
                    .setValue(model).addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@FotoPerfil, "Error al Subir Imagen...", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun uploadImage() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(intent, 101)
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(this@FotoPerfil, "Permiso Denegado", Toast.LENGTH_SHORT).show()
                }
                override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data!!.data
            imageSelectIv!!.setImageURI(uri)
        }
    }

}
