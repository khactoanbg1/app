package com.vf7.smartlauncher

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/** Phone launcher only. Android Auto controls its own app categories and display layout. */
class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("vf7_settings", MODE_PRIVATE) }
    private lateinit var selected: TextView
    private var layoutRatio = "50:50"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRatio = prefs.getString("ratio", "50:50") ?: "50:50"
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(36), dp(22), dp(24))
            setBackgroundColor(Color.rgb(12, 21, 33))
        }
        scroll.addView(root)
        root.addView(label("VF7 SMART LAUNCHER", 27f, true, 0xFFFFFFFF.toInt()))
        root.addView(label("Samsung S22 Ultra • Android 14", 15f, false, 0xFFB4C6DA.toInt()))
        root.addView(label("Chọn bố cục ưu tiên trên điện thoại", 18f, true, 0xFFFFFFFF.toInt()))
        selected = label("Đã chọn: $layoutRatio", 16f, false, 0xFF70E0BB.toInt())
        root.addView(selected)
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for (ratio in listOf("50:50", "70:30", "30:70")) {
            val button = Button(this).apply {
                text = ratio
                setOnClickListener {
                    layoutRatio = ratio
                    prefs.edit().putString("ratio", ratio).apply()
                    selected.text = "Đã chọn: $layoutRatio"
                }
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(54), 1f))
        }
        root.addView(row)
        root.addView(label("Mở ứng dụng", 18f, true, 0xFFFFFFFF.toInt()))
        root.addView(action("MỞ GOOGLE MAPS") { launchMaps() })
        root.addView(action("MỞ YOUTUBE") { launchYouTube() })
        root.addView(action("MỞ YOUTUBE MUSIC") { launchMusic() })
        root.addView(action("THỬ MỞ HAI ỨNG DỤNG") { launchAdjacent() })
        root.addView(label(
            "Lưu ý: tỷ lệ đã chọn chỉ là cấu hình lưu trong app; Android không cấp API công khai " +
                "để ép tỷ lệ chia màn hình của Android Auto. Nút thử hai ứng dụng chỉ yêu cầu " +
                "chế độ đa cửa sổ trên điện thoại; tùy One UI có thể cần chia màn hình thủ công. " +
                "Không phát video trên màn hình Android Auto khi xe di chuyển.",
            14f, false, 0xFFB4C6DA.toInt()
        ))
        root.addView(label("Dùng Google Maps và YouTube Music trực tiếp trong Android Auto để dẫn đường và nghe âm thanh.", 14f, false, 0xFF70E0BB.toInt()))
        setContentView(scroll)
    }

    private fun launchMaps() {
        val app = packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
        if (app != null) safeStart(app) else safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps")))
    }

    private fun launchYouTube() {
        val app = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
        if (app != null) safeStart(app) else safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")))
    }

    private fun launchMusic() {
        val app = packageManager.getLaunchIntentForPackage("com.google.android.apps.youtube.music")
        if (app != null) safeStart(app) else safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com")))
    }

    private fun launchAdjacent() {
        val maps = packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
        val youtube = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
        if (maps == null || youtube == null) {
            Toast.makeText(this, "Cần cài Google Maps và YouTube", Toast.LENGTH_LONG).show()
            return
        }
        safeStart(maps)
        // Best effort: OS may ignore adjacent placement if not already in multi-window mode.
        youtube.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
        safeStart(youtube)
        Toast.makeText(this, "Nếu chưa chia đôi, mở ứng dụng gần đây và chọn Chia đôi màn hình.", Toast.LENGTH_LONG).show()
    }

    private fun safeStart(intent: Intent) {
        try { startActivity(intent) }
        catch (_: ActivityNotFoundException) { Toast.makeText(this, "Không tìm thấy ứng dụng tương thích", Toast.LENGTH_LONG).show() }
        catch (_: SecurityException) { Toast.makeText(this, "Thiết bị không cho phép mở ứng dụng này", Toast.LENGTH_LONG).show() }
    }

    private fun action(title: String, onClick: () -> Unit): Button = Button(this).apply {
        text = title
        isAllCaps = false
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(58)).apply { topMargin = dp(8) }
    }

    private fun label(t: String, size: Float, bold: Boolean, color: Int): TextView = TextView(this).apply {
        text = t
        textSize = size
        setTextColor(color)
        gravity = Gravity.START
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setPadding(0, dp(8), 0, dp(13))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
