package net.cassiolandim.kittychallenge.ui

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import net.cassiolandim.kittychallenge.MainActivity

class MainActivityTestRule(
    private val initialNavId: Int
) : ActivityTestRule<MainActivity>(MainActivity::class.java) {

    override fun getActivityIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            putExtra(MainActivity.EXTRA_NAVIGATION_ID, initialNavId)
        }
    }
}