package com.example.pokedex.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.core.status
import com.example.pokedex.navigation.NavGraph
import com.example.pokedex.network.ConnectivityObserver
import com.example.pokedex.network.NetworkConnectivityObserver

private lateinit var connectivityObserver: ConnectivityObserver
var keepSplashOpened = true

class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        enableEdgeToEdge()
        setContent {
            val orientation by remember { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }
            this.requestedOrientation = orientation
            connectivityObserver = NetworkConnectivityObserver(applicationContext ?: return@setContent)
            status = connectivityObserver.observe().collectAsState(
                initial = ConnectivityObserver.Status.Unavailable
            ).value
            navController = rememberNavController()
            NavGraph(navController = navController ?: return@setContent)
        }
    }
}