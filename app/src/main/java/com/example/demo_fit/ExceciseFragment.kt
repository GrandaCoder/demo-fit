package com.example.demo_fit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebsite()
    }
    private fun loadWebsite(){

        val webSettings: WebSettings = mBinding.wbEjercicios.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.allowFileAccess = true
        mBinding.wbEjercicios.webViewClient = WebViewClient()
        mBinding.wbEjercicios.loadUrl("https://demofit-c6501.web.app/videos/html/videocategoria.html")
    }

}