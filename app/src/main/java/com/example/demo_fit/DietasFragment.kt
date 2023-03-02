package com.example.demo_fit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.example.demo_fit.databinding.FragmentDietasBinding


class DietasFragment : Fragment() {
    lateinit var mBinding: FragmentDietasBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =FragmentDietasBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebsite()
    }

    private fun loadWebsite(){

        val webSettings: WebSettings = mBinding.wbDiets.settings
        webSettings.javaScriptEnabled = true
        mBinding.wbDiets.webViewClient = WebViewClient()
        mBinding.wbDiets.loadUrl("https://www.nutrition.gov/es")
    }

}