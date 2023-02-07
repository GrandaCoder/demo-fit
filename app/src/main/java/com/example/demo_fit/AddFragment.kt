package com.example.demo_fit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo_fit.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddFragment : Fragment() {

    private val RC_GALLERY =  18
    private val PATH_SNAPSHOT = "snapshots"

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var mStorageReference: StorageReference
    //para traer la url y ponerla en rel time dataBase
    private lateinit var mDatabaseReference: DatabaseReference

    private var mPhotoSelectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentAddBinding.inflate(inflater,container,false)
        return  mBinding.root
    }

    //configurar los botones
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.btnPost.setOnClickListener { postSnapshot() }

        mBinding.btnSelect.setOnClickListener { openGallery() }

        mStorageReference = FirebaseStorage.getInstance().reference
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOT)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //se muestra la galeria
        startActivityForResult(intent,RC_GALLERY)
    }

    private fun postSnapshot() {
        //para guardar la foto
        mBinding.progressBarAdd.visibility = View.VISIBLE
        val key = mDatabaseReference.push().key!!

        val storageReferece = mStorageReference.child(PATH_SNAPSHOT).child("My_photo")

        if (mPhotoSelectedUri != null){
            storageReferece.putFile(mPhotoSelectedUri!!)
            .addOnProgressListener {
                val progress = (100*it.bytesTransferred/it.totalByteCount).toDouble()
                mBinding.progressBarAdd.progress = progress.toInt()
                mBinding.tvMessage.text = "$progress%"
            }
            .addOnCompleteListener{
                mBinding.progressBarAdd.visibility = View.INVISIBLE
            }

            .addOnSuccessListener {
                Snackbar.make(mBinding.root,"Instantanea publicada",Snackbar.LENGTH_SHORT).show()
                it.storage.downloadUrl.addOnSuccessListener {
                    saveSnapshot(key,it.toString(),mBinding.etTitle.text.toString().trim())
                    mBinding.tiTitle.visibility = View.GONE
                    mBinding.tvMessage.text = getString(R.string.post_message_title)
                }
            }
            .addOnFailureListener{
                Snackbar.make(mBinding.root,"No se pudo subir intente mas tarde",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSnapshot(key:String,url:String,title:String){
        val snapshot = Snapshot(title = title, photoUrl = url)
        mDatabaseReference.child(key).setValue(snapshot)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == RC_GALLERY){
                mPhotoSelectedUri = data?.data
                mBinding.imgPhoto.setImageURI(mPhotoSelectedUri)
                mBinding.tiTitle.visibility = View.VISIBLE
                mBinding.tvMessage.text = getString(R.string.post_message_valid_title)
            }
        }
    }

}