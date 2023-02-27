package com.example.demo_fit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo_fit.databinding.FragmentExceciseBinding
import com.example.demo_fit.databinding.FragmentProfileBinding

class ExceciseFragment : Fragment() {

    private lateinit var mBinding: FragmentExceciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentExceciseBinding.inflate(inflater, container, false)
        return mBinding.root
    }

}