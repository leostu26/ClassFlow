package com.classflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.classflow.databinding.ActivityMainBinding

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

        // Only Home and Classes are top-level (no back arrow shown for these)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.classListFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

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
                    // Pop back to Classes if already on the stack, else navigate
                    val popped = navController.popBackStack(R.id.classListFragment, false)
                    if (!popped) {
                        navController.navigate(R.id.classListFragment)
                    }
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
                else -> { }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

