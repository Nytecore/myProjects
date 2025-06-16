package com.example.superherobook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.superherobook.ui.theme.SuperheroBookTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val superheroList = ArrayList<Superhero>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            SuperheroBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {

                        NavHost(navController = navController,
                            startDestination = "list_screen") {

                            composable("list_screen") {
                                getData()
                                SuperheroList(superheroes = superheroList, navController = navController)
                            }

                            composable("detail_screen/{superhero}",
                                arguments = listOf(
                                    navArgument("superhero") {
                                        type = NavType.StringType
                                    }
                                )
                            ) {

                                val superheroString = remember {
                                    it.arguments?.getString("superhero")
                                }

                                val selectedSuperhero = Gson().fromJson(superheroString, Superhero::class.java)
                                DetailScreen(superhero = selectedSuperhero)

                            }
                        }
                    }
                }
            }
        }
    }

    private fun getData() {
        val superman = Superhero("Superman" , "DC Universe" , R.drawable.superman , "Super strength")
        val batman = Superhero("Batman" , "DC Universe" , R.drawable.batman , "Super rich")
        val spiderMan = Superhero("Spider Man" , "Marvel Universe" , R.drawable.spider_man , "Spider")
        val wolverine = Superhero("Wolverine" , "DC Universe" , R.drawable.wolverine , "Bone like iron")
        val hulk = Superhero("Hulk" , "Marvel Universe" , R.drawable.hulk , "Green giant")
        val ironman = Superhero("Iron Man" , "Marvel Universe" , R.drawable.ironman , "Techno-genius ")
        val thor = Superhero("Thor" , "Marvel Universe" , R.drawable.thor , "Thunder god")
        val deadpool = Superhero("Deadpool" , "DC Universe" , R.drawable.deadpool , "Regenerator")

        superheroList.add(superman)
        superheroList.add(batman)
        superheroList.add(spiderMan)
        superheroList.add(wolverine)
        superheroList.add(hulk)
        superheroList.add(ironman)
        superheroList.add(thor)
        superheroList.add(deadpool)
    }
}

@Preview(showBackground = true)
@Composable
fun SuperheroPreview() {
    SuperheroBookTheme {

    }
}