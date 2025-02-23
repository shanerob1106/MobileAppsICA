package com.example.sendit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sendit.pages.AIPage
import com.example.sendit.pages.AddPage
import com.example.sendit.pages.ChatPage
import com.example.sendit.pages.HomePage
import com.example.sendit.pages.LikePage
import com.example.sendit.pages.ProfilePage
import com.example.sendit.pages.SearchPage

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ActionBars(modifier: Modifier = Modifier) {

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Default.Search),
        NavItem("Add", Icons.Default.Add),
        NavItem("AI", Icons.Default.Person),
        NavItem("Profile", Icons.Default.AccountBox)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Send It.",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
            },
            actions = {
                IconButton(onClick = { selectedIndex = 5 }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { selectedIndex = 6 }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    navItemList.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                            },
                            icon = {
                                Icon(imageVector = navItem.Icon, contentDescription = navItem.label)
                            },
                            label = {
                                Text(text = navItem.label)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex)
        }
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int) {
    when (selectedIndex) {
        0 -> HomePage(modifier)
        1 -> SearchPage()
        2 -> AddPage()
        3 -> AIPage()
        4 -> ProfilePage()
        5 -> LikePage()
        6 -> ChatPage()
    }
}
