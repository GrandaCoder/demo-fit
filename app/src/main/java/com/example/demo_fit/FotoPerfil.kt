package com.example.demo_fit

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import com.example.demo_fit.databinding.ActivityFotoPerfilBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FotoPerfil : AppCompatActivity() {

    val pickMedia = registerForActivityResult(PickVisualMedia()){ uri ->
        if(uri!=null){
            imageSelectIv.setImageURI(uri)
        }else{
            Log.i("aris", "No seleccionado")
        }
    }

    lateinit var imageSelectIv: ImageView
    lateinit var SaveImage: Button
    var uri: Uri? = null
    var firebaseStorage: FirebaseStorage? = null
    var firebaseDatabase: FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_perfil)

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        imageSelectIv = findViewById(R.id.imageSelect_iv)
        imageSelectIv.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        SaveImage = findViewById(R.id.SaveImage)
        SaveImage.setOnClickListener {
            subirimagen()
        }

    }

    private fun subirimagen() {
        val reference = firebaseStorage!!.reference.child("Images").child(System.currentTimeMillis().toString() + "")
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
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data!!.data
            binding!!.ivSelector.setImageURI(uri)
        }
    }
    */
}