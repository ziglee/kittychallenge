package net.cassiolandim.kittychallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navController = findNavController(R.id.navigationHostFragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            val initialNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, R.id.navigation_main)
            navController.navigate(initialNavId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navigationHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
