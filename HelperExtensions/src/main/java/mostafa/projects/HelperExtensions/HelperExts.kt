package mostafa.projects.HelperExtensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.WriterException
import de.mateware.snacky.Snacky
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.fragment.app.Fragment


class HelperExts {
    companion object {

        fun Uri.getQuery(query: String): String? {
            val chapter: String? = this.getQueryParameter(query) //will return "V-Maths-Addition "
            return chapter
        }

        private val currentLocation: MutableLiveData<Location> by lazy { MutableLiveData<Location>() }

        @SuppressLint("MissingPermission")
        fun Context.startLocationUpdates(): LiveData<Location> {
            //To avoid multi observables from HomeActivity.
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(
                    getLocationRequest(),
                    object : LocationCallback() {
                        @SuppressLint("MissingPermission")
                        override fun onLocationResult(p0: LocationResult?) {
                            super.onLocationResult(p0)
                            val location: Location? = p0?.lastLocation
                            if (location != null) {
                                if (currentLocation.value != null &&
                                    currentLocation.value!!.distanceTo(location) < 10
                                )
                                    return

                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            return currentLocation
        }

        private fun getLocationRequest(): LocationRequest = LocationRequest
            .create().apply {
                interval = 6000
                fastestInterval = 4000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

        fun Context.isLocationEnabled(): Boolean {
            var enabled = false
            val sdkVersion = Build.VERSION.SDK_INT
            if (sdkVersion == 29) {
                enabled = backGroundLocation29()
            } else if (sdkVersion == 30) {
                enabled = backGroundLocation()
            } else {
                enabled = checkLocationPermission()
            }
            return enabled
        }

        fun Context.checkLocationPermission(): Boolean {
            return !(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        }


        fun Context.backGroundLocation(): Boolean {
            return !(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    )
        }

        fun Context.backGroundLocation29(): Boolean {
            return !(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    )
        }

        fun RequestLocationPermission(activity: Activity, PERMISSION_REQUEST_CODE: Int = 0) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }


        fun Context.getNext7Days(): ArrayList<Day> {
            var dayslist: ArrayList<Day> = ArrayList()


            val cal = Calendar.getInstance()
            val month_date = SimpleDateFormat("MMMM")
            val month_name: String = month_date.format(cal.time)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)


            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (cal.get(Calendar.MONTH) > 8) {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            } else {
                var day1Obj = Day(
                    "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}",
                    dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                    day = cal.getDays(this), month = cal.getMonth(this)
                )
                if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                    day1Obj = Day(
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-0${
                            cal.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }",
                        dayOfMon = cal.get(Calendar.DAY_OF_MONTH),
                        day = cal.getDays(this), month = cal.getMonth(this)
                    )
                }
                dayslist.add(day1Obj)
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)

            return dayslist
        }

        fun Context.getDay(num: Int): String {
            return when (num) {
                1 -> return this.getString(R.string.saturday)
                2 -> return this.getString(R.string.sunday)
                3 -> return this.getString(R.string.monday)
                4 -> return this.getString(R.string.tuesday)
                5 -> return this.getString(R.string.wednesday)
                6 -> return this.getString(R.string.thursday)
                7 -> return this.getString(R.string.friday)
                else -> return ""
            }
        }

        fun Calendar.getDays(ctx: Context): String {
            return when (this.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> return ctx.getString(R.string.saturday)
                Calendar.SUNDAY -> return ctx.getString(R.string.sunday)
                Calendar.MONDAY -> return ctx.getString(R.string.monday)
                Calendar.TUESDAY -> return ctx.getString(R.string.tuesday)
                Calendar.WEDNESDAY -> return ctx.getString(R.string.wednesday)
                Calendar.THURSDAY -> return ctx.getString(R.string.thursday)
                Calendar.FRIDAY -> return ctx.getString(R.string.friday)
                else -> return ""
            }
        }

        fun Calendar.getMonth(ctx: Context): String {
            return when (this.get(Calendar.MONTH)) {
                Calendar.JANUARY -> return ctx.getString(R.string.jan)
                Calendar.FEBRUARY -> return ctx.getString(R.string.feb)
                Calendar.MARCH -> return ctx.getString(R.string.march)
                Calendar.APRIL -> return ctx.getString(R.string.apr)
                Calendar.MAY -> return ctx.getString(R.string.may)
                Calendar.JUNE -> return ctx.getString(R.string.jun)
                Calendar.JULY -> return ctx.getString(R.string.july)
                Calendar.AUGUST -> return ctx.getString(R.string.august)
                Calendar.SEPTEMBER -> return ctx.getString(R.string.sept)
                Calendar.OCTOBER -> return ctx.getString(R.string.oct)
                Calendar.NOVEMBER -> return ctx.getString(R.string.novmber)
                Calendar.DECEMBER -> return ctx.getString(R.string.december)
                else -> return ""
            }
        }


        fun List<String>.Concat(seprator: String? = ","): String {
            var str: String? = ""
            this?.forEachIndexed { index, element ->
                str = str + "${element?.capitalize()}"
                if (index != this?.size) {
                    if (index != (this?.size?.minus(1))) {
                        str = str + "${seprator}"
                    }
                }
            }
            return str!!
        }

        fun ArrayList<String>.Concat(seprator: String? = ","): String {
            var str: String? = ""
            this?.forEachIndexed { index, element ->
                str = str + "${element?.capitalize()}"
                if (index != this?.size) {
                    if (index != (this?.size?.minus(1))) {
                        str = str + "${seprator}"
                    }
                }
            }
            return str!!
        }

        @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        fun Activity.GenereteQrCode(id: Int?): Bitmap? {

            val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            val display = manager!!.defaultDisplay

            val point = Point()
            display.getSize(point)

            val width: Int = point.x
            val height: Int = point.y

            var dimen = if (width < height) width else height
            dimen = dimen * 2 / 4

            val qrgEncoder = QRGEncoder(id?.toString(), null, QRGContents.Type.TEXT, dimen)
            qrgEncoder.colorBlack = Color.BLACK
            qrgEncoder.colorWhite = Color.WHITE
            try {
                var bitmap = qrgEncoder.bitmap
                return bitmap
            } catch (e: WriterException) {

            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        fun Activity.GenereteQrCode(id: String?): Bitmap? {

            val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            val display = manager!!.defaultDisplay

            val point = Point()
            display.getSize(point)

            val width: Int = point.x
            val height: Int = point.y

            var dimen = if (width < height) width else height
            dimen = dimen * 2 / 4

            val qrgEncoder = QRGEncoder(id?.toString(), null, QRGContents.Type.TEXT, dimen)
            qrgEncoder.colorBlack = Color.BLACK
            qrgEncoder.colorWhite = Color.WHITE
            try {
                var bitmap = qrgEncoder.bitmap
                return bitmap
            } catch (e: WriterException) {

            }
            return null
        }

        fun Activity.ErrorMsg(msg: String, duration: Int) {
            val warningSnackBar = Snacky.builder()
                .setActivity(this)
                .setText(msg)
                .setDuration(duration)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(Color.RED)
                .setIcon(R.drawable.ic_error)
                .error()

            warningSnackBar.show()

        }


        fun NavController.NavigateUp(bundle: Bundle?, navOptions: NavOptions?) {
            var pervId = this?.previousBackStackEntry?.destination?.id
            this.navigate(pervId!!, bundle, navOptions)
        }


        fun NavController.RefreshCurrentFragment() {
            val id = this.currentDestination?.id
            this.popBackStack(id!!, true)
            this.navigate(id)
        }

        fun withDelay(delay: Long, block: () -> Unit) {
            object : CountDownTimer(delay, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    block
                }
            }.start()
        }


        fun Context.GetLocationData(lat: Double, long: Double): GeoData {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(lat, long, 1)
            val cityName = addresses[0].locality
            val countryName = addresses[0].countryName
            val stateName = addresses[0].adminArea
            var countryCode = addresses[0].countryCode
            var phone = addresses[0].phone
            return GeoData(
                cityName = cityName,
                countryName = countryName,
                stateName = stateName,
                countryCode = countryCode,
            )
        }

        @RequiresApi(Build.VERSION_CODES.CUPCAKE)
        fun Activity.GetDeviceId(): String {
            val android_id = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return android_id
        }

        // https://stackoverflow.com/a/57925521
        @RequiresApi(Build.VERSION_CODES.ECLAIR)
        fun Activity.openActivity(block: Intent.() -> Unit = {}) {
            val intent = Intent(this, T::class.java)
            block(intent)
            startActivity(intent)
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        @RequiresApi(Build.VERSION_CODES.CUPCAKE)
        fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }


        @RequiresApi(Build.VERSION_CODES.M)
        fun Activity.storageGranted(): Boolean {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            }
            return false
        }

        fun String.StrongPass(): Boolean {
            val PASSWORD_REGEX = """(?=.*[A-Z]).{8,}""".toRegex()
            if (this.matches(regex = PASSWORD_REGEX))
                return true
            else
                return false
        }

        fun String.isEmpty(): Boolean {
            if (TextUtils.isEmpty(this))
                return true
            return false
        }

        fun String.Equals(str2: String): Boolean {
            if (TextUtils.equals(this, str2))
                return true
            return false
        }

        fun String.isEmailValid(mailTypes: ArrayList<String>? = null): Boolean {
            var mailTypeCorrect = false
            if (this.contains("@") && this.endsWith(".com")) {
                var mailType = this.substring(this.indexOf("@") + 1, this.indexOf(".com"))
                mailTypeCorrect =
                    mailType.equals("gmail") || mailType.equals("yahoo") || mailTypes?.contains(
                        mailType
                    )!!
            }

            if (this.contains("@") && this.endsWith(".com") && mailTypeCorrect == true)
                return true
            return false
        }

        @RequiresApi(Build.VERSION_CODES.FROYO)
        fun CharSequence.isValidEmail(): Boolean {
            return if (TextUtils.isEmpty(this)) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(this).matches()
            }
        }


        fun String.countWords(): Int {
            var wordCount = 0
            var word = false
            val endOfLine = this.length - 1
            for (i in 0 until this.length) {
                // if the char is a letter, word = true.
                if (Character.isLetter(this[i]) && i != endOfLine) {
                    word = true
                    // if char isn't a letter and there have been letters before,
                    // counter goes up.
                } else if (!Character.isLetter(this[i]) && word) {
                    wordCount++
                    word = false
                    // last word of String if it doesn't end with a non letter, it
                    // wouldn't count without this.
                } else if (Character.isLetter(this[i]) && i == endOfLine) {
                    wordCount++
                }
            }
            return wordCount
        }

        fun EditText.isArabic() {
            this.addTextChangedListener(object : TextWatcher {

                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun afterTextChanged(s: Editable) {
                    if (this@isArabic.text.toString().ContainEnglish()) {
                        this@isArabic.setText(this@isArabic.text.toString().RemoveEnglish())
                        this@isArabic.setSelection(this@isArabic.length())
                    }


                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    Log.i("CharInput", "${s.toString()}")


                }
            })
        }

        fun EditText.isEngilish() {
            this.addTextChangedListener(object : TextWatcher {

                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun afterTextChanged(s: Editable) {
                    if (this@isEngilish.text.toString().ContainArabic()) {
                        this@isEngilish.setText(this@isEngilish.text.toString().RemoveArabic())
                        this@isEngilish.setSelection(this@isEngilish.length())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    Log.i("CharInput", "${s.toString()}")


                }
            })
        }


        fun Fragment.ShowToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
            this?.requireActivity()?.ShowToast(message, duration)
        }

        fun Activity.ShowToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
            Toast.makeText(this, message, duration).show()

        fun Context.ShowToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
            Toast.makeText(this, message, duration).show()

        fun String.RemoveNumbers(): String {
            var re = Regex("[0-9]")
            var testRegx = re.replace(this, "")
            Log.i("TEST_REGX", testRegx)
            return testRegx
        }

        fun String.RemoveArabic(): String {
            var re = Regex("[ء-ي]+")
            var testRegx = re.replace(this, "")
            Log.i("TEST_REGX", testRegx)
            return testRegx
        }

        fun String.RemoveEnglish(): String {
            var re = Regex("[a-zA-Z]+")
            var testRegx = re.replace(this, "")
            Log.i("TEST_REGX", testRegx)
            return testRegx
        }

        fun String.ContainArabic(): Boolean {
            var re = Regex("[ء-ي]+")
            return re.containsMatchIn(this)
        }

        fun String.ContainEnglish(): Boolean {
            var re = Regex("[a-zA-Z]+")
            return re.containsMatchIn(this)
        }


    }
}