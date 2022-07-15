package io.github.jae02546.ekinarabe

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //プレーヤsummaryにプレーヤ名設定
        val p1: EditTextPreference? = findPreference(getString(R.string.setting_playerName1_key))
        p1?.summary = p1?.text ?: ""
        val p2: EditTextPreference? = findPreference(getString(R.string.setting_playerName2_key))
        p2?.summary = p2?.text ?: ""
        val p3: EditTextPreference? = findPreference(getString(R.string.setting_playerName3_key))
        p3?.summary = p3?.text ?: ""
        val p4: EditTextPreference? = findPreference(getString(R.string.setting_playerName4_key))
        p4?.summary = p4?.text ?: ""
        val p5: EditTextPreference? = findPreference(getString(R.string.setting_playerName5_key))
        p5?.summary = p5?.text ?: ""

        //効果音summaryに効果音名設定
        val sde = resources.getStringArray(R.array.setting_sound_entries)
        val sd: ListPreference? =
            preferenceScreen.findPreference(getString(R.string.setting_soundNo_key))
        val soundNo = sd?.value?.toInt() ?: R.string.setting_soundNo_defaultValue
        if (sde.size > soundNo)
            sd?.summary = sde[soundNo]
        else
            sd?.summary = sde[0]

        //バイブレーションsummaryにバイブレーション名設定
        val vbe = resources.getStringArray(R.array.setting_vibration_entries)
        val vb: ListPreference? =
            preferenceScreen.findPreference(getString(R.string.setting_vibrationNo_key))
        val vibrationNo = vb?.value?.toInt() ?: R.string.setting_vibrationNo_defaultValue
        if (vbe.size > vibrationNo)
            vb?.summary = vbe[vibrationNo]
        else
            vb?.summary = vbe[0]

        //データセットsummaryにデータセット名設定
        val dse = resources.getStringArray(R.array.setting_dataset_entries)
        val ds: ListPreference? =
            preferenceScreen.findPreference(getString(R.string.setting_datasetNo_key))
        val datasetNo = ds?.value?.toInt() ?: R.string.setting_datasetNo_defaultValue
        if (dse.size > datasetNo)
            ds?.summary = dse[datasetNo]
        else
            ds?.summary = dse[0]

        //このアプリについてsummaryにアプリ名とバージョン名設定
        val about: Preference? = findPreference(getString(R.string.setting_about_key))
        about!!.summary = getString(R.string.app_label) + " " + BuildConfig.VERSION_NAME

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            //変更時summary表示
            when (key) {
                getString(R.string.setting_playerName1_key) -> {
                    val p: EditTextPreference? =
                        findPreference(getString(R.string.setting_playerName1_key))
                    p?.summary = p?.text ?: ""
                }
                getString(R.string.setting_playerName2_key) -> {
                    val p: EditTextPreference? =
                        findPreference(getString(R.string.setting_playerName2_key))
                    p?.summary = p?.text ?: ""
                }
                getString(R.string.setting_playerName3_key) -> {
                    val p: EditTextPreference? =
                        findPreference(getString(R.string.setting_playerName3_key))
                    p?.summary = p?.text ?: ""
                }
                getString(R.string.setting_playerName4_key) -> {
                    val p: EditTextPreference? =
                        findPreference(getString(R.string.setting_playerName4_key))
                    p?.summary = p?.text ?: ""
                }
                getString(R.string.setting_playerName5_key) -> {
                    val p: EditTextPreference? =
                        findPreference(getString(R.string.setting_playerName5_key))
                    p?.summary = p?.text ?: ""
                }
                getString(R.string.setting_soundNo_key) -> {
                    val sde = resources.getStringArray(R.array.setting_sound_entries)
                    val sd: ListPreference? =
                        preferenceScreen.findPreference(getString(R.string.setting_soundNo_key))
                    val soundNo = sd?.value?.toInt() ?: R.string.setting_soundNo_defaultValue
                    if (sde.size > soundNo)
                        sd?.summary = sde[soundNo]
                    else
                        sd?.summary = sde[0]
                }
                getString(R.string.setting_vibrationNo_key) -> {
                    val vbe = resources.getStringArray(R.array.setting_vibration_entries)
                    val vb: ListPreference? =
                        preferenceScreen.findPreference(getString(R.string.setting_vibrationNo_key))
                    val vibrationNo =
                        vb?.value?.toInt() ?: R.string.setting_vibrationNo_defaultValue
                    if (vbe.size > vibrationNo)
                        vb?.summary = vbe[vibrationNo]
                    else
                        vb?.summary = vbe[0]
                }
                getString(R.string.setting_datasetNo_key) -> {
                    val dse = resources.getStringArray(R.array.setting_dataset_entries)
                    val ds: ListPreference? =
                        preferenceScreen.findPreference(getString(R.string.setting_datasetNo_key))
                    val datasetNo = ds?.value?.toInt() ?: R.string.setting_datasetNo_defaultValue
                    if (dse.size > datasetNo)
                        ds?.summary = dse[datasetNo]
                    else
                        ds?.summary = dse[0]
                }
                else -> {
                }
            }
        }


}
