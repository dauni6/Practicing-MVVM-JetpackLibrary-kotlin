package com.dontsu.dogs2.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.dontsu.dogs2.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.fragment) //navigation 들을 관리하는 navController
        NavigationUI.setupActionBarWithNavController(this, navController) //up(뒤로가기)버튼을 만들기 위해 사용

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null) //up(뒤로가기)버튼 만들어짐
    }

}
