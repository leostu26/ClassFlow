package com.classflow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.classflow.databinding.ActivityMainBinding
import com.classflow.notification.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top-level destinations: no back arrow shown
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.classListFragment, R.id.ganttChartFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        if (savedInstanceState == null) {
            handleNotificationIntent(intent)
        }

        // Manually handle bottom nav clicks to avoid the back stack bug
        // where tapping Home from a nested screen registers but doesn't navigate
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Pop everything off the back stack back to Home
                    navController.popBackStack(R.id.homeFragment, false)
                    true
                }
                R.id.classListFragment -> {
                    val popped = navController.popBackStack(R.id.classListFragment, false)
                    if (!popped) navController.navigate(R.id.classListFragment)
                    true
                }
                R.id.ganttChartFragment -> {
                    val popped = navController.popBackStack(R.id.ganttChartFragment, false)
                    if (!popped) navController.navigate(R.id.ganttChartFragment)
                    true
                }

                else -> false
            }
        }

        // Keep bottom nav highlight in sync with current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> binding.bottomNavigation.menu
                    .findItem(R.id.homeFragment)?.isChecked = true
                R.id.classListFragment, R.id.tasksFragment, R.id.addClassFragment ->
                    binding.bottomNavigation.menu
                        .findItem(R.id.classListFragment)?.isChecked = true
                R.id.ganttChartFragment ->
                    binding.bottomNavigation.menu
                        .findItem(R.id.ganttChartFragment)?.isChecked = true
                else -> { }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val taskId = intent?.getLongExtra(NotificationHelper.EXTRA_TASK_ID, -1L) ?: -1L
        if (taskId < 0) return
        val courseName = intent?.getStringExtra(NotificationHelper.EXTRA_COURSE_NAME) ?: ""
        navController.navigate(
            R.id.taskDetailFragment,
            Bundle().apply {
                putLong("taskId", taskId)
                putString("courseName", courseName)
            },
            NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

