package com.vf7.smartlauncher

import android.app.Activity
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.content.pm.ActivityInfo
import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.content.Context
import android.webkit.WebSettings

/** Embedded dual web panel for PHONE use only; not an Android Auto projection app. */
class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("vf7_settings", MODE_PRIVATE) }
    private lateinit var videoPane: WebView
    private lateinit var mapsPane: WebView
    private lateinit var contentRow: LinearLayout
    private lateinit var status: TextView
    private var ratio = "50:50"
    private var videoUrl = "https://www.youtube.com/"
    private val mapHtml by lazy { assets.open("map.html").bufferedReader().use { it.readText() } }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        ratio = prefs.getString("ratio", "50:50") ?: "50:50"
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(14, 22, 34))
            setPadding(dp(6), dp(7), dp(6), dp(6))
        }
        setContentView(root)
        val title = TextView(this).apply {
            text = "VF7 SMART VIEW 2 · OSM"
            textSize = 17f
            setTextColor(Color.WHITE)
            setPadding(dp(8), dp(5), 0, dp(7))
        }
        root.addView(title)
        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(controls)
        for (choice in listOf("50:50", "70:30", "30:70")) {
            controls.addView(Button(this).apply {
                text = choice
                textSize = 11f
                isAllCaps = false
                setOnClickListener {
                    ratio = choice
                    prefs.edit().putString("ratio", choice).apply()
                    updateWeights()
                }
            }, LinearLayout.LayoutParams(0, dp(48), 1f))
        }
        val videoControls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(videoControls)
        val input = EditText(this).apply {
            hint = "Dán link video YouTube"
            setSingleLine(true)
            textSize = 13f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
        }
        videoControls.addView(input, LinearLayout.LayoutParams(0, dp(49), 1f))
        videoControls.addView(Button(this).apply {
            text = "Mở video"
            textSize = 11f
            isAllCaps = false
            setOnClickListener {
                val id = parseYoutubeId(input.text.toString())
                if (id == null) {
                    Toast.makeText(this@MainActivity, "Nhập link YouTube hợp lệ", Toast.LENGTH_SHORT).show()
                } else {
                    videoUrl = "https://www.youtube-nocookie.com/embed/$id?playsinline=1"
                    videoPane.loadUrl(videoUrl)
                }
            }
        }, LinearLayout.LayoutParams(dp(118), dp(49)))
        contentRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(contentRow, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        videoPane = WebView(this)
        mapsPane = WebView(this)
        for (webView in listOf(videoPane, mapsPane)) {
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            webView.settings.mediaPlaybackRequiresUserGesture = true
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = WebViewClient()
            webView.setBackgroundColor(Color.WHITE)
        }
        updateWeights()
        videoPane.loadUrl(videoUrl)
        mapsPane.loadDataWithBaseURL("https://appassets.androidplatform.net/", mapHtml, "text/html", "UTF-8", null)
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(actions)
        actions.addView(Button(this).apply {
            text = "YouTube trang chủ"
            textSize = 11f
            isAllCaps = false
            setOnClickListener { videoPane.loadUrl("https://www.youtube.com/") }
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        actions.addView(Button(this).apply {
            text = "Làm mới OSM"
            textSize = 11f
            isAllCaps = false
            setOnClickListener { mapsPane.loadDataWithBaseURL("https://appassets.androidplatform.net/", mapHtml, "text/html", "UTF-8", null) }
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        actions.addView(Button(this).apply {
            text = "Vị trí"
            textSize = 11f
            isAllCaps = false
            setOnClickListener { showMyLocation() }
        }, LinearLayout.LayoutParams(0, dp(48), 0.8f))
        status = TextView(this).apply {
            text = "Bản đồ OpenStreetMap + MapLibre (cần mạng). Chỉ dùng khi xe đỗ. Chưa hỗ trợ Android Auto hoặc dẫn đường theo tuyến."
            textSize = 11f
            setTextColor(0xFFB6C6D9.toInt())
            gravity = Gravity.CENTER
        }
        root.addView(status)
    }

    private fun showMyLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
            return
        }
        try {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val loc = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                .filter { lm.isProviderEnabled(it) }
                .mapNotNull { lm.getLastKnownLocation(it) }
                .maxByOrNull { it.time }
            if (loc == null) {
                Toast.makeText(this, "Chưa có vị trí. Hãy bật GPS và thử lại.", Toast.LENGTH_LONG).show()
            } else {
                val lat = loc.latitude.coerceIn(-90.0, 90.0)
                val lon = loc.longitude.coerceIn(-180.0, 180.0)
                mapsPane.evaluateJavascript("window.goToLocation($lon,$lat)", null)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Không đọc được GPS: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) showMyLocation()
    }

    private fun updateWeights() {
        if (!::videoPane.isInitialized || !::mapsPane.isInitialized) return
        contentRow.removeAllViews()
        val pair = when (ratio) {
            "70:30" -> 7f to 3f
            "30:70" -> 3f to 7f
            else -> 5f to 5f
        }
        contentRow.addView(videoPane, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, pair.first))
        contentRow.addView(mapsPane, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, pair.second))
    }

    private fun parseYoutubeId(raw: String): String? {
        val value = raw.trim()
        if (value.matches(Regex("[a-zA-Z0-9_-]{11}"))) return value
        val uri = try { android.net.Uri.parse(value) } catch (_: Exception) { return null }
        val host = uri.host?.lowercase() ?: return null
        val id = when {
            host == "youtu.be" || host == "www.youtu.be" -> uri.pathSegments.firstOrNull()
            host == "youtube.com" || host == "www.youtube.com" || host == "m.youtube.com" -> {
                if (uri.pathSegments.firstOrNull() in listOf("shorts", "embed", "live")) uri.pathSegments.getOrNull(1)
                else uri.getQueryParameter("v")
            }
            else -> null
        }
        return id?.takeIf { it.matches(Regex("[a-zA-Z0-9_-]{11}")) }
    }

    override fun onDestroy() {
        for (view in listOfNotNull(if (::videoPane.isInitialized) videoPane else null, if (::mapsPane.isInitialized) mapsPane else null)) {
            view.stopLoading()
            view.destroy()
        }
        super.onDestroy()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
