package com.dontsu.dogs2.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.dontsu.dogs2.R
import com.dontsu.dogs2.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*

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

    fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder(this)
                    .setTitle("메세지 보내기 권한")
                    .setMessage("메세지에 접근을 요청합니다")
                    .setPositiveButton("물어보세요") { dialog, which ->  
                        requestSmsPermission()
                    }
                    .setNegativeButton("아니요") {dialog, which ->
                        notifyDetailFragment(false)
                    }
                    .show()
            } else {
                requestSmsPermission()
            }
        } else {
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true)
                } else {
                    notifyDetailFragment(false)
                }
            }
        }
    }

    private fun notifyDetailFragment(permissionGranted: Boolean) {
        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment) {
            (activeFragment as DetailFragment).onPermissionResult(permissionGranted)
        }
    }

}
