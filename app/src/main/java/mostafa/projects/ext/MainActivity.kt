package mostafa.projects.ext

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import mostafa.projects.HelperExtensions.HelperExts.Companion.RemoveArabic
import mostafa.projects.HelperExtensions.HelperExts.Companion.RemoveEnglish
import mostafa.projects.HelperExtensions.HelperExts.Companion.isEmailValid


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mail = "mostafa@a-r2yk.com"

        var types:ArrayList<String> = ArrayList()
        types.add("a-r2yk")
        var validMailCheck = mail.isEmailValid(mailTypes = types)
        Log.i("validMailCheck" , validMailCheck.toString())

        var testRemoveAr = "Stringنخحخنلبيل".RemoveArabic()
        var testRemoveEn = "Stringنخحخنلبيل".RemoveEnglish()
        Log.i("RemoveArabicTest" , testRemoveAr)
        Log.i("RemoveEnglishTest" , testRemoveEn)




    }


}