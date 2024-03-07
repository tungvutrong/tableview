package com.example.tablegridview

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import customtablegridview.CustomTableView

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_main)
        val view = findViewById<CustomTableView>(R.id.ctv_data)
        view.config(
            rowData = Triple(first = false, second = 0.3f, third = null),
            rowViewHolder = ConfigDataTest::epgRowHeaderCreator,
            columnData = null,
            columnViewHolder = ConfigDataTest::epgColumnHeaderCreator,
            itemViewHolder = ConfigDataTest::epgChildCreator
        )
        val rowData = ConfigDataTest.dataTest.map {
            ConfigDataTest.EpgRowHeaderModel(
                id = "${it.id}",
                url = it.url
            )
        }
        val columnData = (0 until ConfigDataTest.epgTest).map {
            ConfigDataTest.EpgColumnHeaderModel(
                id = "$it",
                name = "$it"
            )
        }
        view.updateData(rowData, columnData, ConfigDataTest.dataTest)
    }
}