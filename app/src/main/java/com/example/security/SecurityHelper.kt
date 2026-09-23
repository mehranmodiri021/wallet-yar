package com.example.security

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SecurityHelper {

    /**
     * فعال‌سازی FLAG_SECURE برای جلوگیری از اسکرین‌شات و ضبط تصویر در صفحات حساس
     */
    fun setSecureFlag(activity: Activity?, secure: Boolean) {
        activity?.let {
            if (secure) {
                it.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                it.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    /**
     * پاک‌سازی امن کلیپ‌بورد پس از ۳۰ ثانیه جهت جلوگیری از نشت عبارات محرمانه
     */
    fun clearClipboardDelayed(context: Context, delayMillis: Long = 30_000L) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayMillis)
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                clipboard?.setPrimaryClip(ClipData.newPlainText("", ""))
            } catch (_: Exception) {}
        }
    }

    /**
     * دریافت تنظیمات محرمانه (ترجیحات امن محلی)
     */
    fun getSecurePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("wallet_yar_secure_prefs", Context.MODE_PRIVATE)
    }

    const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"
    const val KEY_FLAG_SECURE_ENABLED = "flag_secure_enabled"
    const val KEY_VIP_STATUS = "vip_status"
    const val KEY_REWARD_TOKENS = "reward_tokens"
    const val KEY_LAST_REWARD_DATE = "last_reward_date"
}
