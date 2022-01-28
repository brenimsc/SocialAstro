package com.example.socialastro.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.socialastro.R
import com.example.socialastro.databinding.ActivityMainBinding
import com.example.socialastro.model.TopToList
import com.example.socialastro.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModel()
    private var lastId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.GRAY
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fragmentHome = FragmentHome()
        val fragmentNewPost = FragmentNewPost()
        val fragmentSettings = FragmentSettings()

        setCurrentFragment(fragmentHome,fragmentHome.id)
        lastId = R.id.idHome

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.idHome -> setCurrentFragment(fragmentHome, it.itemId)
                R.id.idNewPosts -> setCurrentFragment(fragmentNewPost, it.itemId)
                else -> setCurrentFragment(fragmentSettings, it.itemId)
            }
            true
        }

       // bottom
    }

    private fun setCurrentFragment(fragment: Fragment, id: Int) {
        supportFragmentManager.beginTransaction().apply {
            if (id == lastId) {
                viewModel.backToTopList(TopToList(id, true))
            }
            lastId = id
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}