package io.github.jae02546.ekinarabe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    private var beforeDatasetNo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsContainer, SettingsFragment())
            .commit()

        //ActionBar設定
        supportActionBar?.setTitle(R.string.menu_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //preferenceより設定前のdatasetNo取得
        beforeDatasetNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_datasetNo_key),
            getString(R.string.setting_datasetNo_defaultValue)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //homeでMainActivityに戻る

                //preferenceより設定後のdatasetNo取得
                val datasetNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_datasetNo_key),
                    getString(R.string.setting_datasetNo_defaultValue)
                )
                val intent = Intent().apply {
                    putExtra("before", beforeDatasetNo)
                    putExtra("after", datasetNo)
                }
                setResult(Activity.RESULT_OK, intent)

                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

