package com.example.wsudailyscreener

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import okhttp3.*
import okio.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var client: OkHttpClient
    private lateinit var qrCodeImageView: ImageView
    private lateinit var sharedPref: SharedPreferences
    private var password: String? = ""
    private var accessId: String? = ""
    private var phone: String? = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.homeToolbar))

        sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE)

        this.client = OkHttpClient()
        setQRCodeImageForToday()
        checkUserInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        setQRCodeImageForToday()
        checkUserInfo()
        Log.i("API", "In resume.....................................................")
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onRestart() {
//        super.onRestart()
//
//        setQRCodeImageForToday()
//        checkUserInfo()
//        Log.i("API", "In resume.....................................................")
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                gotoSettings()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setQRCodeImageForToday() {
        qrCodeImageView = findViewById(R.id.qrCodeImageView)

        var qrCodeUrlForToday = checkIfTodayQRCodeAvailable()
        if (qrCodeUrlForToday != null && qrCodeUrlForToday != "") {
            qrCodeImageView.load(qrCodeUrlForToday)
            return
        }
//        makeToast("Couldn't find today's QR code hit Screener button", Toast.LENGTH_SHORT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkIfTodayQRCodeAvailable(): String? {
        val todayDate = getTodaysDate()
        Log.i("API", todayDate)
        return sharedPref.getString(todayDate, "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysDate(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveQRCodeUrl(url: String) {
        val todayDate = getTodaysDate()
        with (sharedPref.edit()) {
            putString(todayDate, url)
            apply()
        }
    }

    fun checkUserInfo() {
        accessId = sharedPref.getString(getString(R.string.sharedpref_access_id_key), "")
        password = sharedPref.getString(getString(R.string.sharedpref_password_key), "")
        phone = sharedPref.getString(getString(R.string.sharedpref_phone_key), "")
        if (accessId == null || accessId == "" ||
            password == null || password == "" ||
            phone == null || phone == "") {
            makeToast("Some user info is missing, go to Settings and add User info.", Toast.LENGTH_LONG)
            findViewById<Button>(R.id.screener_button).isEnabled = false;
        }
    }

    fun doDailyScreener(view: View) {
        login()

        var responseBody = """<!doctype html>
                                                                                                    <html lang="en">
                                                                                                    	<head>
                                                                                                    		<meta charset="utf-8" />
                                                                                                    		<meta name="viewport" content="width=device-width" />
                                                                                                    		<meta name="Keywords" content="" />
                                                                                                    		<meta name="Description" content="" />
                                                                                                    		<meta name="Author" content="" />
                                                                                                    
                                                                                                    		<title>Success! The form has been submitted - Campus Daily Screener - Wayne State University</title>
                                                                                                    
                                                                                                    		<link href="/view/resources/css/main.min.css?20180305" rel="stylesheet" type="text/css"  media="all" />
                                                                                                    
                                                                                                    		
                                                                                                    		<!-- IOS touch icons-->
                                                                                                    		<link rel="apple-touch-icon" href="/view/resources/images/apple-touch-icon.png" />
                                                                                                    		<link rel="apple-touch-icon" sizes="72x72" href="/view/resources/images/apple-touch/apple-touch-icon-72x72-precomposed.png" />
                                                                                                    		<link rel="apple-touch-icon" sizes="114x114" href="/view/resources/images/apple-touch/apple-touch-icon-114x114.png" />
                                                                                                    		<link rel="apple-touch-icon" sizes="144x144" href="/view/resources/images/apple-touch/apple-touch-icon-144x144.png" />
                                                                                                    
                                                                                                    		<script src="/view/resources/js/modernizr.js"></script>
                                                                                                    </head>
                                                                                                    <body>
                                                                                                    
                                                                                                    	<script>
                                                                                                    		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                                                                                                    					(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                                                                                                    				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                                                                                                    		})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
                                                                                                    
                                                                                                    		ga('create', 'UA-815862-35', 'wayne.edu', {'name': 'Formy'});
                                                                                                    		ga('create', 'UA-35684592-1', 'wayne.edu', {'name': 'allWayneState'});
                                                                                                    
                                                                                                    		ga('Formy.send', 'pageview');
                                                                                                    		ga('allWayneState.send', 'pageview');
                                                                                                    	</script>
                                                                                                    
                                                                                                        <nav aria-label="Skip navigation" class="skip">
                                                                                                            <ul class="list-reset">
                                                                                                                            <li><a href="#content">Skip to main content</a></li>
                                                                                                                        </ul>
                                                                                                        </nav>
                                                                                                    <div id="container">
                                                                                                    	        <header>
                                                                                                                <div class="wsuheader">
                                                                                                        <div class="wsuwrap">
                                                                                                            <div class="wsuwordmark">
                                                                                                                <h1>
                                                                                                                    <a href="https://wayne.edu/" aria-labelledby="wsuheader-title">
                                                                                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 605 65" preserveAspectRatio="xMaxYMin meet">
                                                                                                                            <title id="wsuheader-title">Wayne State University</title>
2022-11-03 01:37:33.548 29175-29411 APIzz                   com.example.wsudailyscreener         I                          <path class="wm0" d="M574.2 15.9H553l-1 4.7s1.2-.7 1.5-.9c.3-.2.8-.4 1.7-.4h6.2v22.4c0 .8-.6 1.8-.9 2.4h6.2c-.3-.6-.9-1.6-.9-2.4V19.3h6.1c.9 0 1.3.2 1.7.4.3.2 1.5.9 1.5.9l-.9-4.7zm-40.1 14.5c-1-.6-2-1.2-3-1.7l-4.3-2.5c-.9-.5-1.6-1-2-1.5-.5-.6-.8-1.2-.8-1.9 0-1.2.5-2.2 1.6-2.9 1.1-.8 2.3-1.2 3.8-1.2 1 0 1.9.1 2.7.4.7.2 1.4.7 2 1.3l1 1v-4.6l-.4-.2c-1.2-.5-2.6-.7-3.5-.8s-5.4-.5-8.5 1.8c-1.8 1.4-2.9 3.2-2.9 5.4 0 1.5.4 2.9 1.2 4 .7 1 1.8 1.9 3.3 2.7l4.9 2.8c1.3.7 2.2 1.4 2.7 2 .7.8 1.1 1.6 1.1 2.6 0 1.5-.5 2.5-1.6 3.3s-2.7 1.2-4.7 1.2c-1.3 0-2.5-.1-3.4-.3s-1.7-.7-2.6-1.4l-1-.8v4.5c3.1 1.5 10.1 1.6 14-.5 2.5-1.3 3.7-3.5 3.7-6.4 0-1.5-.3-2.9-.9-3.9-.5-.8-1.3-1.6-2.4-2.4zm-47.3 10.5h-8V30h6.9c.8 0 1.7.3 2.2.5l.9.4v-4.1h-10v-7.7h7.4c.8 0 1.2.2 1.7.7l.9.9v-4.8H473s.9 1 1 1.1.3.4.4.7c.1.3.1.5.1.9v23.3c0 .9-1 2.2-1 2.2h17L492 39l-1.4.9c-.2.1-.9.5-1.8.7-.7.3-1.7.3-2 .3zM418.9 16s.4 1 .6 1.7c.1.5.2.9.2 1.2v17.5l-18-20.6h-6.1c.9.6 2 1.4 2.1 1.8s.2.8.2 1.2v22.3c0 .3 0 1-.3 1.6-.4.7-.8 1.3-.8 1.3h5.4s-.4-1.2-.6-2.1c-.2-.9-.2-1.4-.2-1.6V21.9l21.7 24V19c0-.3.1-.7.2-1.2.2-.9.6-1.8.6-1.8h-5zm127.9 2.4c0-.8.6-1.9.9-2.5h-5.8c.3.6.9 1.7.9 2.5v23.2c0 .8-.6 1.9-.9 2.5h5.8c-.3-.6-.9-1.7-.9-2.5V18.4zm-91.7 19s8.7-18.6 8.8-18.8.5-.9.3-1.2-.6-1.4-.6-1.4h6.2s-1.9 2.5-2.1 3c-.2.4-12.7 27-12.7 27s-13.4-27.3-13.6-27.7c-.2-.4-1.4-1.2-1.9-1.5l-.9-.7h7.8s-.7 1.3-.7 1.6c0 .3.2.6.3.9l9.1 18.8zm60 5c-.3-.3-1-1-1.4-1.5-.1-.2-7.7-11.2-7.7-11.2 1.7-.1 3.2-.8 4.5-2.1 1.3-1.4 2-3 2-4.9 0-2-.7-3.7-2.1-4.9-1.4-1.2-3.1-1.8-5.1-1.8h-10l1.6 1.6c.1.1.2.3.3.5.1.2.1.4.1.7v23.1c0 .9-1 2.2-1 2.2h5.8l-.3-.8c-.2-.5-.3-.6-.3-.7 0-.1-.1-.3-.1-.5s-.1-.4-.1-.6V29.6l9.9 14.5h6.5s-2.4-1.6-2.6-1.7zm-13.2-14.9h-.5V19c.3-.1.6-.2.8-.2.3 0 .8-.1 1.5-.1 1.8 0 3.1.4 3.9 1.3.6.6.8 1.6.8 2.8 0 1.4-.4 2.5-1.3 3.4s-2.2 1.3-3.9 1.3h-1.3zm-67.5-9.1c0-.8.6-1.9.9-2.5h-5.8c.3.6.9 1.7.9 2.5v23.2c0 .8-.6 1.9-.9 2.5h5.8c-.3-.6-.9-1.7-.9-2.5V18.4zm-42-2.5h-5.8s.7 2.4.7 2.6v16.3c0 1.8-.6 3.3-1.8 4.5-1.2 1.2-2.7 1.8-4.6 1.8s-3.6-.7-5.1-1.9c-1.4-1.3-2.1-2.8-2.1-4.7v-16l.7-2.6h-5.8s1 2 1 2.5v16.5c0 2.9 1.1 5.2 3.3 7 2.1 1.8 4.8 2.7 7.9 2.7 2.9 0 5.4-.9 7.4-2.7s3.1-4.1 3.1-6.8V18.4c.1-.4 1.1-2.5 1.1-2.5zM600 17.7c.3-.4 1.9-1.8 1.9-1.8H596v1.3c-.1.3-.3.6-.4.9l-5.4 8.5-5.1-8.3c-.2-.4-.4-.7-.5-1.1s0-1.3 0-1.3H578s1.6 1.4 1.9 1.8c.3.4 7.9 12.3 7.9 12.3v12.6c0 .1 0 .2-.2.7l-.3.8h5.2l-.3-.8c-.2-.4-.2-.6-.2-.7V30c-.2 0 7.7-11.9 8-12.3zm-473.3.3c.3-.4 1.6-2.1 1.6-2.1H122l.5.9c.1.1.2.4.2.7 0 .1 0 .3-.1.4-.1.2-5.8 17.2-5.8 17.2s-6-16.6-6-16.8c-.2-.5 0-1.2.1-1.5s.6-.9.6-.9H104s.8.6 1.5 1.4c.6.6 1 1.3 1 1.4l1.2 3.2-5 13.3s-5.9-16.7-6-17.2.1-.8.2-1.1l.6-.9h-8.2s1 .4 1.7 1 1.2 1.3 1.6 2.3c.3.9 9.9 26.5 9.9 26.5l6.9-19.1 7.1 19s9.4-26.3 9.5-26.5c.2-.3.4-.7.7-1.2zm129.6 12.4c-1-.6-2-1.2-3-1.7l-4.3-2.5c-.9-.5-1.6-1-2-1.5-.5-.6-.8-1.2-.8-1.9 0-1.2.5-2.2 1.6-2.9 1.1-.8 2.3-1.2 3.8-1.2 1 0 1.9.1 2.7.4.7.2 1.4.7 2 1.3l1 1v-4.6l-.4-.2c-1.2-.5-2.6-.7-3.5-.8s-5.4-.5-8.5 1.8C243 19 242 20.7 242 23c0 1.5.4 2.9 1.2 4 .7 1 1.8 1.9 3.3 2.7l4.9 2.8c1.3.7 2.2 1.4 2.7 2 .7.8 1.1 1.6 1.1 2.6 0 1.5-.5 2.5-1.6 3.3s-2.7 1.2-4.7 1.2c-1.3 0-2.5-.1-3.4-.3s-1.7-.7-2.6-1.4l-1-.8v4.5c3.1 1.5 10.1 1.6 14-.5 2.5-1.3 3.7-3.5 3.7-6.4 0-1.5-.3-2.9-.9-3.9-.4-.8-1.2-1.6-2.4-2.4zM197.9 16s.4 1 .6 1.7c.1.5.2.9.2 1.2v17.5l-18-20.6h-6.1c.9.6 2 1.4 2.1 1.8.2.4.2.8.2 1.2v22.3c0 .3 0 1-.3 1.6-.4.7-.8 1.3-.8 1.3h5.4s-.4-1.2-.6-2.1c-.2-.9-.2-1.4-.2-1.6V21.9l21.7 24V19c0-.3.1-.7.2-1.2.2-.9.6-1.8.6-1.8h-5zm24.4 24.9h-8V30h6.9c.8 0 1.7.3 2.2.5l.9.4v-4.1h-10v-7.7h7.4c.8 0 1.2.2 1.7.7l.9.9v-4.8h-15.7s.9 1 1 1.1.3.4.4.7c.1.3.1.5.1.9v23.3c0 .9-1 2.2-1 2.2h17l1.6-5.1-1.4.9c-.2.1-.9.5-1.8.7-.9.3-1.9.3-2.2.3zm61.5-25h-21.2l-1 4.7s1.2-.7 1.5-.9c.3-.2.8-.4 1.7-.4h6.2v22.4c0 .8-.6 1.8-.9 2.4h6.2c-.3-.6-.9-1.6-.9-2.4V19.3h6.1c.9 0 1.3.2 1.7.4.3.2 1.5.9 1.5.9l-.9-4.7zm48.3 0h-21.2l-1 4.7s1.2-.7 1.5-.9c.3-.2.8-.4 1.7-.4h6.2v22.4c0 .8-.6 1.8-.9 2.4h6.2c-.3-.6-.9-1.6-.9-2.4V19.3h6.1c.9 0 1.3.2 1.7.4.3.2 1.5.9 1.5.9l-.9-4.7zm18.2 25h-8V30h6.9c.8 0 1.7.3 2.2.5l.9.4v-4.1h-10v-7.7h7.4c.8 0 1.2.
2022-11-03 01:37:33.548 29175-29411 APIzz                   com.example.wsudailyscreener         I                          <path class="wm1" d="M69.5 8S52.7 1.7 35.8 1.7 2 8 2 8s-4.7 32.2 33.7 55.3C74.2 40.1 69.5 8 69.5 8z"/>
                                                                                                                            <path class="wm2" d="M35.8 1.7C52.7 1.7 69.5 8 69.5 8s4.7 32.2-33.7 55.3V1.7z"/>
                                                                                                                            <path class="wm3" d="M64.6 11.5s-15.7-5-28.8-5c-13.1 0-28.8 5-28.8 5S6 39 35.7 56.9v.1-.1C65.5 39 64.6 11.5 64.6 11.5z"/>
                                                                                                                            <path class="wm4" d="M35.8 6.5c13.1 0 28.9 5 28.9 5s.8 27.6-28.9 45.5V6.5z"/>
                                                                                                                            <path class="wm5" d="M64.7 11.5s-15.8-5-28.9-5c-13.1 0-28.9 5-28.9 5S6 39.1 35.8 57c29.7-17.9 28.9-45.5 28.9-45.5z"/>
                                                                                                                            <path class="wm6" d="M41.7 21.8l-2.1 4.8 2.1 4.7c7.4-5.3 8.7-10.7 8.7-10.7l-4.2-4.8h11.9c0 8.2-5.3 17.8-18.1 28.1l-4.3-11-4.3 11c-12.7-10.2-18-19.8-18-28h11.9L21 20.7s1.3 5.4 8.7 10.7l2.1-4.7-2.1-4.8h12v-.1z"/>
                                                                                                                        </svg>
                                                                                                                    </a>
                                                                                                                </h1>
                                                                                                            </div>
                                                                                                    
                                                                                                            <div class="wsumenu">
                                                                                                                <a class="warriorstrong" href="https://wayne.edu/warriorstrong/" aria-labelledby="wsuheader-brand">
                                                                                                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 380 30"  preserveAspectRatio="xMaxYMax">
                                                                                                                        <title id="wsuheader-brand">Warrior strong</title>
                                                                                                                        <path d="M13.4 2.9H18l4.1 15 1.7-15H29L25.3 27H20l-4.3-15.8L11.5 27H6.2L2.5 2.9h5.1l1.7 15 4.1-15zm44.2 24.2h-5.4l-1.5-4.8h-7.4l-1.4 4.8h-5.4L44.4 3h5.3l7.9 24.1zm-12.7-9.5h4.3l-2.2-8-2.1 8zm31.3.6h-3.6v8.9h-5.3V2.9h13.4c2.2 0 4 1.8 4 4v7.3c0 1.8-1.2 3.3-2.9 3.8l4.3 9h-5.8l-4.1-8.8zM72.6 7.7v5.7h6.6c.2 0 .4-.1.4-.4V8c0-.2-.1-.4-.4-.4h-6.6zm33.2 10.5h-3.6v8.9H97V2.9h13.4c2.2 0 4 1.8 4 4v7.3c0 1.8-1.2 3.3-2.9 3.8l4.3 9H110l-4.2-8.8zm-3.6-10.5v5.7h6.6c.2 0 .4-.1.4-.4V8c0-.2-.1-.4-.4-.4h-6.6zm29.7 19.4h-5.3V2.9h5.3v24.2zm25.1 0h-9c-2.2 0-4-1.8-4-4V6.9c0-2.2 1.8-4 4-4h8.9c2.2 0 4 1.8 4 4v16.2c0 2.2-1.7 4-3.9 4zm-1.7-19.4h-5.7c-.2 0-.4.1-.4.4v13.8c0 .2.1.4.4.4h5.7c.2 0 .4-.1.4-.4V8.1c0-.2-.2-.4-.4-.4zm26.6 10.5h-3.6v8.9H173V2.9h13.4c2.2 0 4 1.8 4 4v7.3c0 1.8-1.2 3.3-2.9 3.8l4.3 9H186l-4.1-8.8zm-3.6-10.5v5.7h6.6c.2 0 .4-.1.4-.4V8c0-.2-.1-.4-.4-.4h-6.6zm55.3 15.4c0 2.2-1.8 4-4 4h-8.2c-2.2 0-4-1.8-4-4v-2.7l5.1-.9v2.4c0 .2.1.4.4.4h5.1c.2 0 .4-.1.4-.4v-3.4c0-.3-.2-.4-.5-.5-.6-.2-2.5-.7-7-2-2.1-.6-3.4-1.6-3.4-3.8V6.9c0-2.2 1.8-4 4-4h8c2.2 0 4 1.8 4 4v2.5l-5.1.9V8.1c0-.2-.1-.4-.4-.4h-4.9c-.2 0-.4.1-.4.4v3.1c0 .3.2.4.4.5.4.1 2.4.7 7.2 2 2.1.6 3.3 1.6 3.3 3.8v5.6zm10.2-20.2h17.7v4.8h-6.2v19.4H250V7.7h-6.2V2.9zM281 18.2h-3.6v8.9h-5.3V2.9h13.4c2.2 0 4 1.8 4 4v7.3c0 1.8-1.2 3.3-2.9 3.8l4.3 9h-5.8l-4.1-8.8zm-3.6-10.5v5.7h6.6c.2 0 .4-.1.4-.4V8c0-.2-.1-.4-.4-.4h-6.6zm37.2 19.4h-8.9c-2.2 0-4-1.8-4-4V6.9c0-2.2 1.8-4 4-4h8.9c2.2 0 4 1.8 4 4v16.2c0 2.2-1.8 4-4 4zm-1.7-19.4h-5.7c-.2 0-.4.1-.4.4v13.8c0 .2.1.4.4.4h5.7c.2 0 .4-.1.4-.4V8.1c0-.2-.1-.4-.4-.4zm36 19.4h-5.1l-8-14.1v14.1h-5.1V2.9h5.1l8 14.1V2.9h5.1v24.2zm19.8-13.2h8.8v9.2c0 2.2-1.8 4-4 4H365c-2.2 0-4-1.8-4-4V6.9c0-2.2 1.8-4 4-4h8.5c2.2 0 4 1.8 4 4v2.9l-5.1.9V8.1c0-.2-.1-.4-.4-.4h-5.3c-.2 0-.4.1-.4.4v13.8c0 .2.1.4.4.4h5.4c.2 0 .4-.1.4-.4v-3.4h-3.7v-4.6z"/>
                                                                                                                    </svg>
                                                                                                                </a>
                                                                                                    
                                                                                                                <div class="wsumenuwrap">
                                                                                                                    <div class="wsulogin">
                                                                                                                        <a href="https://login.wayne.edu"><span class="wsuhidesmall">Login</span></a>
                                                                                                                    </div>
                                                                                                    
                                                                                                                    <div class="wsusearch"><a href="https://wayne.edu/search/" class="wsusearchicon"><span class="wsuhidesmall">Search</span></a></div>
                                                                                                    
                                                                                                                    <div class="wsusearchbar">
                                                                                                                        <form method="get" action="https://wayne.edu/search/" role="search">
                                                                                                                            <label for="q">Search:</label>
                                                                                                                            <input name="q" class="wsusearchfield" size="15" id="q" placeholder="Search..." type="text">
                                                                                                                            <button type="submit" class="wsusearchicon"><span>Search</span></button>
                                                                                                                        </form>
                                                                                                                    </div>
                                                                                                                </div>
                                                                                                            </div>
                                                                                                        </div>
                                                                                                    </div>
2022-11-03 01:37:33.548 29175-29411 APIzz                   com.example.wsudailyscreener         I              <div class="header-menu">
                                                                                                                    <div class="row">
                                                                                                                                                <div class="xlarge-12 large-12 medium-12 small-12 columns">
                                                                                                                                                <h1>Campus Daily Screener</h1>
                                                                                                                        </div>
                                                                                                                                        </div>
                                                                                                                </div>
                                                                                                            </header>
                                                                                                    		<div class="row">
                                                                                                    		<div class="xlarge-12 large-12 medium-12 small-12 columns">
                                                                                                    
                                                                                                    		<main id="content" tabindex="-1">
                                                                                                                <div id="notifications">
                                                                                                                    
                                                                                                                    
                                                                                                                </div>
                                                                                                        <script>
                                                                                                            document.addEventListener("DOMContentLoaded", function() {
                                                                                                                document.querySelectorAll('.formy fieldset legend').forEach(function (legend) {
                                                                                                                    legend.style.display = 'block';
                                                                                                                });
                                                                                                            });
                                                                                                        </script>
                                                                                                    
                                                                                                    	<div class="formy">
                                                                                                    		
                                                                                                    					<div class="form-confirmation form-description">
                                                                                                    				<p>Thank you for your submission. You are permitted to be physically present on campus as planned today, November 3, 2022.</p><hr /><div class="row">
                                                                                                                    <div class="columns medium-12 large-5 medium-centered">
                                                                                                                        <div class="row">
                                                                                                                            <div class="columns medium-4">
                                                                                                                                <img src="https://wayne.edu/coronavirus/masked-person.png" alt="Silhouette of person with mask on" />
                                                                                                                            </div>
                                                                                                                            <div class="columns medium-8">
                                                                                                                                <h2 style="border-bottom: 0;">Masks required in classrooms and laboratories, and optional in most other campus locations.</h2>
                                                                                                                            </div>
                                                                                                                        </div>
                                                                                                                    </div>
                                                                                                                </div><div style="text-align:center"><hr><p>Record of your campus daily screener procedure submission:</p><table style="border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: center; width:400px; position: relative; display: block; background: #0C5449; padding: 0;" bgcolor="#0C5449" role="presentation" align="center">
                                                                                                                <tr style="vertical-align: top; text-align: center; padding: 0;" align="center">
                                                                                                                    <td class="wrapper last" style="width:400px; word-break: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; border-collapse: collapse !important; vertical-align: center; text-align: center; position: relative; color: #222; font-family: "Helvetica","Arial",sans-serif; font-weight: 400; line-height: 19px; font-size: 14px; background: #0C5449; margin: 0; padding: 10px 0 0;" align="center" bgcolor="#0C5449" valign="center"><p style="color:#fff;text-align:center;width:400px;font-weight:bold;font-size:22px;margin:22px 0;">Allowed on <a href="webcal:#" style="color:white;text-decoration:none;">November 3, 2022</a></p><p style="color:#fff;text-align:center;width:400px;font-size:18px;margin:18px 0;">Riddhesh Markandeya (hm9650)</p></td></tr></table><div style="text-align:center;"><img src="https://chart.apis.google.com/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=https://forms.wayne.edu/daily-screening-verification/?verify=hm9650%3A7706601" alt="QR code verification - Enable images to view"></div><p>Submitted on: November 3, 2022 at 1:37 am EDT<br /></div>
                                                                                                    			</div>
                                                                                                    
                                                                                                    						
                                                                                                    		
                                                                                                    		<h2>Questions or comments</h2>
                                                                                                    		<p class="form-description">For health related questions, contact the <a href="https://health.wayne.edu">Campus Health Center</a> at <a href="tel:313-577-5003">313-577-5003</a>. For general questions or comments, <a href="https://forms.wayne.edu/return-to-campus-questions-comments/">send a message to the campus restart committee</a>.</p>
                                                                                                    	</div>
                                                                                                    
                                                                                                    <script>
                                                                                                      (function () {
                                                                                                    	let phone_number = document.querySelector(".field_f_253006");
                                                                                                    	let phone_context = document.createElement("p");
                                                                                                    	phone_context.id = "phone-context";
                                                                                                    	phone_context.textContent = "To assist with contact tracing, please provide a phone number where you can easily be reached.";
                                                                                                    	phone_context.setAttribute("style", "font-style:italic; margin: 0 0 1rem;");
2022-11-03 01:37:33.548 29175-29411 APIzz                   com.example.wsudailyscreener         I  	phone_number.appendChild(phone_context);
                                                                                                    
                                                                                                    	let phone_field = document.getElementById("f_253006")
                                                                                                    	phone_field.type = "tel";
                                                                                                    	phone_field.autocomplete = "on";
                                                                                                    
                                                                                                    	phone_field.style.marginBottom = "0";
                                                                                                    	phone_field.setAttribute("aria-describedby", "phone-context");
                                                                                                    
                                                                                                    	let form = document.getElementById("form-12827");
                                                                                                    	form.addEventListener('submit', onSubmit);
                                                                                                    
                                                                                                    	function onSubmit(event) {
                                                                                                    		document.getElementById("formy-button").disabled=true;
                                                                                                    		document.getElementById("formy-button").value="Submitting, please wait...";
                                                                                                    		document.getElementById("form-12827").submit();
                                                                                                    	}
                                                                                                      })();
                                                                                                    </script>
                                                                                                    
                                                                                                    			</main>
                                                                                                    		</div>
                                                                                                    	</div>
                                                                                                    </div>
                                                                                                        <footer>
                                                                                                            <div class="wsufooter">
                                                                                                        <div class="wsuwrap">
                                                                                                            <div class="privacy">
                                                                                                                <p><a href="https://wayne.edu/policies/">Privacy and University Policies</a></p>
                                                                                                            </div>
                                                                                                            <div class="copyright">
                                                                                                                <p><a href="https://wayne.edu/">Wayne State University</a> &copy; 2022</p>
                                                                                                            </div>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                        </footer>
                                                                                                    
                                                                                                    
                                                                                                        
                                                                                                        <script type="text/javascript">
                                                                                                            request = new XMLHttpRequest();
                                                                                                            var in_error = false;
                                                                                                    
                                                                                                            var sessionError = function() {
                                                                                                                in_error = true;
                                                                                                            }
                                                                                                    
                                                                                                            window.setInterval(function() {
                                                                                                                // Heartbeat is required to keep users logged in during a long idle period.
                                                                                                                request.open('GET', '/session/heartbeat', true);
                                                                                                    
                                                                                                                request.onload = function() {
                                                                                                                    if (request.status >= 200 && request.status < 400){
                                                                                                                        // Returned some content
                                                                                                                        resp = request.responseText;
                                                                                                                        obj = JSON.parse(resp);
                                                                                                    
                                                                                                                        // That return included an error
                                                                                                                        if (obj.error) {
                                                                                                                            sessionError();
                                                                                                                        }
                                                                                                                    } else {
                                                                                                                        // We reached our target server, but it returned an error
                                                                                                                        sessionError();
                                                                                                                    }
                                                                                                                };
                                                                                                    
                                                                                                                request.onerror = sessionError;
                                                                                                    
                                                                                                                request.send();
                                                                                                            }, 60000); // Every minute
                                                                                                        </script>
                                                                                                        
                                                                                                    </body>
                                                                                                    </html>"""

//        qrCodeImageView.load("https://chart.apis.google.com/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=https://forms.wayne.edu/daily-screening-verification/?verify=hm9650%3A7706601")

//        val qrCodeRegex = """src="(https:\/\/chart\.apis\.google\.com\/chart\?.*?)"""".toRegex()
//
//        val qrCodeUrlMatch = qrCodeRegex.find(responseBody)
//        if (qrCodeUrlMatch != null) {
//            Log.i("aayo1", qrCodeUrlMatch.groupValues[0] + "\n" + qrCodeUrlMatch.groupValues[1])
//        }
    }

    private fun login() {
//      accessid: hm9650
//      passwd: <password>
//      submit: Login
        val formBody = FormBody.Builder()
            .add("accessid", accessId!!)
            .add("passwd", password!!)
            .add("submit", "Login")
            .build()

        val request = Request.Builder()
            .url(getString(R.string.screener_form_login))
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                this@MainActivity.makeToast("Something Went wrong, couldn't login.", Toast.LENGTH_LONG)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }


                        val strCookie = response.headers["set-cookie"]!!.split(";")[0]
                        submitForm(strCookie)
                        for ((name, value) in response.headers) {
                            Log.i("APIxxxx", "$name: $value")
                        }

                        Log.i("APIfooo", response.body!!.string())
                    } catch (ex: Exception) {
                        Log.e("API", ex.toString())
                        this@MainActivity.makeToast("Something Went wrong, couldn't login.", Toast.LENGTH_LONG)
//                        throw ex
                    }
                }
            }
        })
    }

    fun submitForm(strCookie: String) {
//        f_253006:3312035841
//        f_253229[]:1000
//        f_251741:No
//        f_255927:No
//        formy-save:12827
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("f_253006", phone!!)
            .addFormDataPart("f_253229[]", "1000")
            .addFormDataPart("f_251741", "No")
            .addFormDataPart("f_255927", "No")
            .addFormDataPart("formy-save", "12827")
            .build()

        val request = Request.Builder()
            .header("Cookie", strCookie)
            .url(getString(R.string.screener_form_url))
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                this@MainActivity.makeToast("Something Went wrong, couldn't submit form.", Toast.LENGTH_LONG)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        var responseBody = response.body!!.string()

                        val qrCodeRegex =
                            """src="(https:\/\/chart\.apis\.google\.com\/chart\?.*?)"""".toRegex()

                        val qrCodeUrlMatch = qrCodeRegex.find(responseBody)

                        var qrCodeUrl = ""
                        if (qrCodeUrlMatch != null) {
                            qrCodeUrl = qrCodeUrlMatch.groupValues[1]
                        }

                        if (qrCodeUrl == "") {
                            this@MainActivity.makeToast("Couldn't find qr code url", Toast.LENGTH_LONG)
                            throw Exception("Couldn't find qr code url")
                        }

                        Log.i("APIuu", qrCodeUrl)

                        qrCodeImageView.load(qrCodeUrl)

                        saveQRCodeUrl(qrCodeUrl)

                        // https://chart.apis.google.com/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=https://forms.wayne.edu/daily-screening-verification/?verify=hm9650%3A7642449
                        // src="(https:\/\/chart\.apis\.google\.com\/chart\?.*?)"
                        Log.i("APIzz", responseBody)
                    } catch (ex: Exception) {
                        Log.e("API", ex.toString())
                        this@MainActivity.makeToast("Something Went wrong, couldn't submit form.", Toast.LENGTH_LONG)
//                        throw ex
                    }
                }
            }
        })
    }

    fun makeToast(text: String,length: Int) {
        this.runOnUiThread {
            Toast.makeText(this@MainActivity, text, length).show()
        }
    }

    fun gotoSettings() {
        val intent = Intent(this, UserDataActivity::class.java)
        startActivity(intent)
    }

    fun openBrowser(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.screener_form_url)))
        startActivity(browserIntent)
    }
}