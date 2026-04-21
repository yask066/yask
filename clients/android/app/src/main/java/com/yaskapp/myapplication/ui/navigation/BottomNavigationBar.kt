package com.yaskapp.myapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val YaskRed = Color(0xFFE53935)
private val InactiveGray = Color(0xFF8F8F8F)
private val NavBackground = Color.White

@Composable
fun BottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(NavBackground),
        containerColor = NavBackground
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onScreenSelected(AppScreen.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = YaskRed,
                selectedTextColor = YaskRed,
                unselectedIconColor = InactiveGray,
                unselectedTextColor = InactiveGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.TRENDING,
            onClick = { onScreenSelected(AppScreen.TRENDING) },
            icon = {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Trending"
                )
            },
            label = { Text("Trending") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = YaskRed,
                selectedTextColor = YaskRed,
                unselectedIconColor = InactiveGray,
                unselectedTextColor = InactiveGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CREATE,
            onClick = { onScreenSelected(AppScreen.CREATE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create"
                )
            },
            label = { Text("Create") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = YaskRed,
                selectedTextColor = YaskRed,
                unselectedIconColor = InactiveGray,
                unselectedTextColor = InactiveGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.NOTIFICATIONS,
            onClick = { onScreenSelected(AppScreen.NOTIFICATIONS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = YaskRed,
                selectedTextColor = YaskRed,
                unselectedIconColor = InactiveGray,
                unselectedTextColor = InactiveGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.PROFILE,
            onClick = { onScreenSelected(AppScreen.PROFILE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = YaskRed,
                selectedTextColor = YaskRed,
                unselectedIconColor = InactiveGray,
                unselectedTextColor = InactiveGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}