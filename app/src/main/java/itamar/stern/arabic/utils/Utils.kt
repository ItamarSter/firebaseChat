package itamar.stern.arabic.utils

import android.app.Activity
import android.content.res.Resources
import android.util.Patterns
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager


fun CharSequence.isEmailValid(): Boolean{
    return this.length >= 6 && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
fun hideKeyboard(activity: Activity) {
    //system service that can hide the keyboard:
    val imm = activity.getSystemService(InputMethodManager::class.java)
    //which edittext
    imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}
fun Number.dp(): Float{
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)
}
