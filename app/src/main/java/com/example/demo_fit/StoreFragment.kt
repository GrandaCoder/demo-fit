package com.example.demo_fit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo_fit.databinding.FragmentProfileBinding
import com.example.demo_fit.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

    lateinit var mBinding: FragmentStoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

}