package com.compi.dinhnt.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.compi.dinhnt.travelplanner.database.getDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}