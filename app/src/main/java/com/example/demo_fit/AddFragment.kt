package com.example.demo_fit

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo_fit.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private val RC_GALLERY =  18

    private lateinit var mBinding: FragmentAddBinding

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
    }

    private fun openGallery() {
        TODO("Not yet implemented")
    }

    private fun postSnapshot() {
        TODO("Not yet implemented")
    }

}