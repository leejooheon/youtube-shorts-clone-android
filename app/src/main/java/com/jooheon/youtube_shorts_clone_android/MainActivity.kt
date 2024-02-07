package com.jooheon.youtube_shorts_clone_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.jooheon.youtube_shorts_clone_android.databinding.ActivityMainBinding
import com.jooheon.youtube_shorts_clone_android.player.ShortsCache

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ShortsCache.init(this)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        val navInflater = navController.navInflater

        navController.apply {
            val navGraph = navInflater.inflate(R.navigation.nav_graph)
            graph = navGraph
        }
    }

    override fun onDestroy() {
        ShortsCache.release(this)
        super.onDestroy()
    }
}