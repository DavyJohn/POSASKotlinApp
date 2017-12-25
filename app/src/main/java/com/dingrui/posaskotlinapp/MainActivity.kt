package com.dingrui.posaskotlinapp

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dingrui.posaskotlinapp.utils.SharedPreferencesUtil

class MainActivity : AppCompatActivity() {
    private var mWeb: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWeb()
    }

    private fun initWeb() {
        mWeb = findViewById(R.id.webview)
        val webSettings = mWeb!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
//        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.domStorageEnabled = true
        mWeb!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                //网页标题
                print(title)
            }
        }
        mWeb!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.contains("openid")){
                    var id = url!!.substring(url!!.indexOf("=")+1,url!!.length);
                    SharedPreferencesUtil.getInstance(this@MainActivity).putString("id",id);
                }else{
                    if (!url!!.contains("newAppLogin/login")){
                        var url = url+"?openid="+SharedPreferencesUtil.getInstance(this@MainActivity).getString("id")
                        view!!.loadUrl(url)
                    }
                }
                return false
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        if (SharedPreferencesUtil.getInstance(this@MainActivity).getString("id") == null|| TextUtils.isEmpty(SharedPreferencesUtil.getInstance(this@MainActivity).getString("id") )){
            mWeb!!.loadUrl(Contance.BASEURL+Contance.LOGIN);
        }else{
            mWeb!!.loadUrl(Contance.BASEURL+Contance.Main+"?openid="+SharedPreferencesUtil.getInstance(this@MainActivity).getString("id"))
        }

    }

    override fun onBackPressed() {
        if (mWeb!!.canGoBack()) {
            mWeb!!.goBack()
        } else {
            finish()//
        }
    }

    //销毁Webview
    override fun onDestroy() {
        if (mWeb != null) {
            mWeb!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            mWeb!!.clearHistory()
            (mWeb!!.parent as ViewGroup).removeView(mWeb)
            mWeb!!.destroy()
            mWeb = null
        }
        super.onDestroy()
    }
}
