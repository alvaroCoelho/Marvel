package alvaro.com.br.marvelappstarter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import alvaro.com.br.marvelappstarter.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var naviHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews(binding)
    }

    private fun initViews(binding: ActivityMainBinding) {
        naviHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = naviHostFragment.navController

        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnNavigationItemReselectedListener {  }
        }
    }
}