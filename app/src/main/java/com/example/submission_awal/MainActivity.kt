package com.example.submission_awal

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.submission_awal.ui.setting.SettingPreferences
import com.example.submission_awal.ui.setting.SettingViewModel
import com.example.submission_awal.ui.setting.SettingViewModelFactory
import com.example.submission_awal.ui.setting.dataStore
import com.example.submission_awal.workmanager.MyWorker
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(pref)
        )[SettingViewModel::class.java]
        settingViewModel.getThemeSetting().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setContentView(R.layout.activity_main)

        scheduleEventReminder()


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        if (savedInstanceState == null) {
            navController.navigate(R.id.homeFragment)
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,R.id.searchFragment,R.id.upcomingFragment, R.id.settingFragment
            )
        )



        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailEventFragment -> {
                    navView.visibility = View.GONE
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.favoriteFragment -> {
                    navView.visibility = View.GONE
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                else -> {
                    navView.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
        }


    }



    private fun scheduleEventReminder() {
        val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()

        WorkManager.getInstance(this).enqueue(workRequest)

        WorkManager.getInstance(this).pruneWork();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                Log.d("MainActivity", "Work finished: ${workInfo.state}")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem):  Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}