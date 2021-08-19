package dev.mrzha.scan01

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class check : AppCompatActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                cek()
            }
        },1500)
    }
    private fun lompat(){
        val itn = Intent(this, main_manu::class.java)
        itn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(itn)
        this.finish()
    }
    private fun cek(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
       //     Toast.makeText(this, "new 1", Toast.LENGTH_SHORT).show()
            Log.e("ini","1")
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            Log.e("ini", appUpdateInfoTask.toString())
            appUpdateInfoTask.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    Log.e("ini","2")
                    appUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        999
                    )
                } else {
                    Log.e("ini","3")
                    lompat()
                    //Toast.makeText(this, "new", Toast.LENGTH_SHORT).show()
                }
            }
            lompat()
        }
        else{
            Toast.makeText(this,"Sorry, Your Android Version is not Supported", Toast.LENGTH_LONG).show()
            return
        }
    }

    override fun onResume() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.IMMEDIATE,
                        this,
                        999
                    )
                }
            }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
          //    Toast.makeText(this, "sukses", Toast.LENGTH_SHORT).show()
        } else {
        //        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
        }
    }
}