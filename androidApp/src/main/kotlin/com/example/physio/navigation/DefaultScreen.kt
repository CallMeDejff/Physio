package com.example.physio.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.physio.ui.theme.PhysioTheme
import com.example.physio.ui.theme.colorPrimary

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DefaultScreen(navController: NavHostController = rememberNavController()) {
    PhysioTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (currentRoute in listOf(
                        BottomBarScreen.Home.route,
                        BottomBarScreen.Search.route,
                        BottomBarScreen.Profile.route
                    )
                ) {
                    BottomBar(navController = navController)
                }
            }
        ) {
            HomeNavGraph(navController = navController)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Search,
        BottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            //.border(BorderStroke(2.dp, Color.Gray), shape = RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    val background = if (selected) colorPrimary else Color.Transparent
    val contentColor = if (selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .height(40.dp)
            //.clip(CircleShape)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = if (selected) screen.icon_focused else screen.icon,
                contentDescription = "icon",
                tint = contentColor
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = contentColor
                )
            }
        }
    }
}
