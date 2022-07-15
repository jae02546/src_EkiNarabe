package io.github.jae02546.ekinarabe

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceCategory
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {
    //内部変数
    //private var mAdView: AdView? = null //広告
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var first = false
    private var startTime = 0L
    private var defaultColorStateList: ColorStateList? = null //TextViewのデフォルトカラー
    //private var soundPool: SoundPool? = null
    //private var compSound = 0
    //private var recordSound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //RecyclerView設定
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.lineRecyclerView).also {
            it.layoutManager = layoutManager
            it.adapter = MainAdapter(mutableListOf())
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }
        //recyclerViewのTouchHelper設定
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
        val playerNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_playerNo_key),
            getString(R.string.setting_playerNo_defaultValue)
        )
        val datasetNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_datasetNo_key),
            getString(R.string.setting_datasetNo_defaultValue)
        )
        val lineNo = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo ?: 0
        //AnswerTableのチェックとメイン画面路線表示切替
        Tools.lineSwitcher(
            this,
            playerNo,
            datasetNo,
            lineNo,
            false,
            findViewById(R.id.lineHint),
            findViewById(R.id.lineRecord),
            findViewById(R.id.lineKana),
            findViewById(R.id.lineName),
            findViewById(R.id.lineRome),
            findViewById(R.id.lineSupplement1),
            findViewById(R.id.lineAnswer),
            findViewById(R.id.lineSupplement2),
            recyclerView
        )

        //TextViewのデフォルトカラーを保存する
        val tv: TextView = findViewById(R.id.lineRecord)
        defaultColorStateList = tv.textColors

        //comp時間の計算方法
        //起動後路線表示後にカウント開始
        //別の路線に切替、またはアプリ非表示で
        //カウント値を既存のcomp時間に足して書込み
        //comp中はカウントしない
        //comp後compが崩れたらカウント再開
        //compしたら駅の移動は出来なくしたら? それは面白くない?...
        //comp時間計測開始
        startTime = System.currentTimeMillis()

        //効果音ロード
        SoundAndVibrator.loadSound(this)

        //ここから広告
        MobileAds.initialize(this) { }
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
        //ここまで広告
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        //Log.d("onResume", "r1")

        //comp時間計測開始
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()

        //Log.d("onPause", "p1")

        //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
        val playerNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_playerNo_key),
            getString(R.string.setting_playerNo_defaultValue)
        )
        //Log.d("onPause", "2 $playerNo")
        val datasetNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_datasetNo_key),
            getString(R.string.setting_datasetNo_defaultValue)
        )
        //Log.d("onPause", "3 $datasetNo")
        val lineNo = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo
        //Log.d("onPause", "4 $lineNo")
        //終了時のcomp時間とoffset保存
        if (lineNo != null) {
            Tools.playerAnswerCompAndOffsetUpdate(
                this,
                playerNo,
                datasetNo,
                lineNo,
                startTime,
                recyclerView.computeVerticalScrollOffset()
            )
            val foo = recyclerView.computeVerticalScrollOffset()
            //Log.d("onPause", "5 $foo")
        }
    }

    override fun onStop() {
        super.onStop()
        //Log.d("onStop", "s1")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.d("onDestroy", "d1")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        //起動時だけoffset効かないので取り敢えずここで...
        //駅数が多く表示位置が下のほうだと表示がちょっと遅れる
        if (!first) {
            first = true
            //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
            val playerNo = Tools.getPrefInt(
                this,
                getString(R.string.setting_playerNo_key),
                getString(R.string.setting_playerNo_defaultValue)
            )
            val datasetNo = Tools.getPrefInt(
                this,
                getString(R.string.setting_datasetNo_key),
                getString(R.string.setting_datasetNo_defaultValue)
            )
            val lineNo = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo ?: 0
            val ans = AnswerMain.getAnswerTable(this, playerNo, datasetNo, lineNo)
            recyclerView.scrollBy(0, ans?.offset ?: 0)

            //本当は最初に路線選択で選択後に並べ替え画面がよかった
            //いまさらなので起動時は前回終了時の状態にする
            val fsl = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.finalSelLine ?: false
            //路線選択画面表示
            if (fsl)
                startGettingSelectLineResult.launch(Intent(this, SelectLineActivity::class.java))
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //メニューが選択された場合に発生
        return when (item.itemId) {
            R.id.menuPlayer -> {
                //プレーヤ回答情報表示
                val datasetNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_datasetNo_key),
                    getString(R.string.setting_datasetNo_defaultValue)
                )
                var pnMap: MutableMap<Int, String> = mutableMapOf()
                for (i in 1..5)
                    pnMap += i to getPlayerName(i)
//                AlertDialog.Builder(this)
//                    .setTitle("プレイ記録")
//                    .setMessage(Tools.getPlayStatus(this, datasetNo, pnMap))
//                    .setPositiveButton("OK", null)
//                    .show()
                val inflater = LayoutInflater.from(this)
                val layout = inflater.inflate(
                    R.layout.dialog_player,
                    findViewById<ConstraintLayout>(R.id.playLayout)
                )
                val record = Tools.getPlayStatus(this, datasetNo, pnMap)
                layout.findViewById<TextView>(R.id.playRecord1).text = record[0]
                layout.findViewById<TextView>(R.id.playRecord2).text = record[1]
                layout.findViewById<TextView>(R.id.player1a).text = record[2]
                layout.findViewById<TextView>(R.id.player1b).text = record[3]
                layout.findViewById<TextView>(R.id.player1c).text = record[4]
                layout.findViewById<TextView>(R.id.player2a).text = record[5]
                layout.findViewById<TextView>(R.id.player2b).text = record[6]
                layout.findViewById<TextView>(R.id.player2c).text = record[7]
                layout.findViewById<TextView>(R.id.player3a).text = record[8]
                layout.findViewById<TextView>(R.id.player3b).text = record[9]
                layout.findViewById<TextView>(R.id.player3c).text = record[10]
                layout.findViewById<TextView>(R.id.player4a).text = record[11]
                layout.findViewById<TextView>(R.id.player4b).text = record[12]
                layout.findViewById<TextView>(R.id.player4c).text = record[13]
                layout.findViewById<TextView>(R.id.player5a).text = record[14]
                layout.findViewById<TextView>(R.id.player5b).text = record[15]
                layout.findViewById<TextView>(R.id.player5c).text = record[16]
                AlertDialog.Builder(this)
                    .setTitle("プレイ記録")
                    .setView(layout)
                    .setPositiveButton("OK", null)
                    .show()

                true
            }
            R.id.menuSelectLine -> {
                //路線選択
                //preferenceよりplayerNo,datasetNo取得
                val playerNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_playerNo_key),
                    getString(R.string.setting_playerNo_defaultValue)
                )
                val datasetNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_datasetNo_key),
                    getString(R.string.setting_datasetNo_defaultValue)
                )
                //切替前の路線No取得
                val beforeSelLineTable = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)
                //切替前のcomp時間とoffset保存
                if (beforeSelLineTable != null) {
                    Tools.playerAnswerCompAndOffsetUpdate(
                        this,
                        playerNo,
                        datasetNo,
                        beforeSelLineTable.lineNo,
                        startTime,
                        recyclerView.computeVerticalScrollOffset()
                    )
                }
                //終了時路線選択画面フラグをtrueに
                AnswerMain.putPlayStateTableFinalSelLine(this, playerNo, datasetNo, true)
                //路線選択画面表示
                startGettingSelectLineResult.launch(Intent(this, SelectLineActivity::class.java))
                true
            }
            R.id.menuPlayer1, R.id.menuPlayer2, R.id.menuPlayer3, R.id.menuPlayer4, R.id.menuPlayer5 -> {
                //プレイヤーNo保存
                var playerNo = 0
                if (item.itemId == R.id.menuPlayer1)
                    playerNo = 1
                if (item.itemId == R.id.menuPlayer2)
                    playerNo = 2
                if (item.itemId == R.id.menuPlayer3)
                    playerNo = 3
                if (item.itemId == R.id.menuPlayer4)
                    playerNo = 4
                if (item.itemId == R.id.menuPlayer5)
                    playerNo = 5
                //プレーヤ選択前のプレーヤNoをpreferenceより取得
                val beforePlayerNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_playerNo_key),
                    getString(R.string.setting_playerNo_defaultValue)
                )
                //選択されたプレーヤが現状と違う場合は
                //プレーヤNoの書込みとメニュー、メイン画面の更新
                if (beforePlayerNo != playerNo) {
                    //プレーヤNo preferences書込
                    Tools.putPrefInt(this, getString(R.string.setting_playerNo_key), playerNo)
                    //プレーヤが変更されたのでメニュー更新onPrepareOptionsMenu
                    invalidateOptionsMenu()
                    //preferenceよりdatasetNo、dbよりlineNo取得
                    val datasetNo = Tools.getPrefInt(
                        this,
                        getString(R.string.setting_datasetNo_key),
                        getString(R.string.setting_datasetNo_defaultValue)
                    )
                    //切替前のcomp時間とoffset保存
                    val beforeLineNo =
                        AnswerMain.getPlayStateTable(this, beforePlayerNo, datasetNo)?.lineNo
                    if (beforeLineNo != null) {
                        Tools.playerAnswerCompAndOffsetUpdate(
                            this,
                            beforePlayerNo,
                            datasetNo,
                            beforeLineNo,
                            startTime,
                            recyclerView.computeVerticalScrollOffset()
                        )
                    }
                    //AnswerTableのチェックとメイン画面路線表示切替
                    val lineNo =
                        AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo ?: 0
                    Tools.lineSwitcher(
                        this,
                        playerNo,
                        datasetNo,
                        lineNo,
                        false,
                        findViewById(R.id.lineHint),
                        findViewById(R.id.lineRecord),
                        findViewById(R.id.lineKana),
                        findViewById(R.id.lineName),
                        findViewById(R.id.lineRome),
                        findViewById(R.id.lineSupplement1),
                        findViewById(R.id.lineAnswer),
                        findViewById(R.id.lineSupplement2),
                        recyclerView
                    )
                    //comp時間計測開始
                    startTime = System.currentTimeMillis()
                }
                true
            }
            R.id.menuSetting -> {
                //設定
                //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
                val playerNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_playerNo_key),
                    getString(R.string.setting_playerNo_defaultValue)
                )
                val datasetNo = Tools.getPrefInt(
                    this,
                    getString(R.string.setting_datasetNo_key),
                    getString(R.string.setting_datasetNo_defaultValue)
                )
                val lineNo =
                    AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo
                //切替前のcomp時間とoffset保存
                if (lineNo != null) {
                    Tools.playerAnswerCompAndOffsetUpdate(
                        this,
                        playerNo,
                        datasetNo,
                        lineNo,
                        startTime,
                        recyclerView.computeVerticalScrollOffset()
                    )
                }
                //設定画面表示
                startGettingSettingResult.launch(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //路線選択画面の表示開始と選択結果の取得
    private val startGettingSelectLineResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            //<-戻る、最初から、続きからで発生
            //preferenceよりplayerNo,datasetNo取得
            val playerNo = Tools.getPrefInt(
                this,
                getString(R.string.setting_playerNo_key),
                getString(R.string.setting_playerNo_defaultValue)
            )
            val datasetNo = Tools.getPrefInt(
                this,
                getString(R.string.setting_datasetNo_key),
                getString(R.string.setting_datasetNo_defaultValue)
            )

            if (result?.resultCode == Activity.RESULT_OK) {
                //最初から、続きからで発生、<-戻るでは発生しない
                result.data?.let { data: Intent ->
                    val btnName = data.getIntExtra("button", 0)
                    val lineNo = data.getIntExtra("lineNo", 0)
                    val lineName = data.getStringExtra("name")
                    //選択された路線NoのPlayStateTable書込
                    AnswerMain.putPlayStateTableLineNo(this, playerNo, datasetNo, lineNo)
                    //選択された路線NoのPlayStateTable書込（他項目を変更しないように1度読込）
//                    val before = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)
//                    AnswerMain.putPlayStateTable(
//                        this,
//                        PlayStateTable(playerNo, datasetNo, lineNo, before?.filterNo ?: 0)
//                    )
                    var init = false
                    //初めから
                    if (R.string.button_init_key == btnName) {
                        //debug
                        //Toast.makeText(this, "init $lineNo $staName ", Toast.LENGTH_LONG).show()
                        init = true
                    }
                    //続きから
                    if (R.string.button_cont_key == btnName) {
                        //debug
                        //Toast.makeText(this, "cont $lineNo $staName ", Toast.LENGTH_LONG).show()
                        init = false
                    }
                    //AnswerTableのチェックとメイン画面路線表示切替
                    Tools.lineSwitcher(
                        this,
                        playerNo,
                        datasetNo,
                        lineNo,
                        init,
                        findViewById(R.id.lineHint),
                        findViewById(R.id.lineRecord),
                        findViewById(R.id.lineKana),
                        findViewById(R.id.lineName),
                        findViewById(R.id.lineRome),
                        findViewById(R.id.lineSupplement1),
                        findViewById(R.id.lineAnswer),
                        findViewById(R.id.lineSupplement2),
                        recyclerView
                    )
                }
            }

            //終了時路線選択画面フラグをfalseに
            AnswerMain.putPlayStateTableFinalSelLine(this, playerNo, datasetNo, false)

            //comp時間計測開始
            startTime = System.currentTimeMillis()
        }

    //設定画面の表示開始と結果の取得
    private val startGettingSettingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            //設定画面から<-戻るで発生
            if (result?.resultCode == Activity.RESULT_OK) {
                //プレーヤ名変更の可能性があるのでメニュー更新onPrepareOptionsMenu
                invalidateOptionsMenu()

                result.data?.let { data: Intent ->
                    val beforeDatasetNo = data.getIntExtra("before", 0)
                    val datasetNo = data.getIntExtra("after", 0)
                    //データセットNoが変更されていたらメイン画面の更新
                    if (beforeDatasetNo != datasetNo) {
                        //preferenceよりplayerNo、dbよりlineNo取得
                        val playerNo = Tools.getPrefInt(
                            this,
                            getString(R.string.setting_playerNo_key),
                            getString(R.string.setting_playerNo_defaultValue)
                        )
                        //AnswerTableのチェックとメイン画面路線表示切替
                        val lineNo =
                            AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.lineNo ?: 0
                        Tools.lineSwitcher(
                            this,
                            playerNo,
                            datasetNo,
                            lineNo,
                            false,
                            findViewById(R.id.lineHint),
                            findViewById(R.id.lineRecord),
                            findViewById(R.id.lineKana),
                            findViewById(R.id.lineName),
                            findViewById(R.id.lineRome),
                            findViewById(R.id.lineSupplement1),
                            findViewById(R.id.lineAnswer),
                            findViewById(R.id.lineSupplement2),
                            recyclerView
                        )
                    }
                }
            }
            //comp時間計測開始
            startTime = System.currentTimeMillis()
        }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //ここではメニュー項目の変更をまとめて行う
        //androidDevによると実行時のメニュー変更はこのイベントですることになっている
        //このイベントはメニュー項目がアプリバーに表示されるときに発生する
        //具体的には、起動時と折り畳まれているメニューを開いた場合に発生している
        //他の箇所からinvalidateOptionsMenuで発生させることも出来る
        //invalidateOptionsMenuはプレーヤ選択後と設定画面から戻る時に実行している
        //プレーヤ選択では同じプレーヤを選択した場合は実行していない
        //設定ではプレーヤ名を変更していなくても発生
        //常時表示の「プレーヤ名」「路線選択」ではinvalidateOptionsMenuの実行は無し
        //メニューを開き何もせず閉じた場合も発生しない（メニュー以外をタッチで閉じる）

        //preferenceよりplayerNo取得
        val playerNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_playerNo_key),
            getString(R.string.setting_playerNo_defaultValue)
        )
        //メニューにプレーヤ名設定
        //プレーヤー1
//        val pn1 = Tools.getPrefStr(
//            this,
//            getString(R.string.setting_playerName1_key),
//            getString(R.string.setting_playerName1_defaultValue)
//        )
        val mi1: MenuItem = menu?.findItem(R.id.menuPlayer1) as MenuItem
        mi1.title = getPlayerName(1)
//        mi1.title = pn1
        //プレーヤー2
//        val pn2 = Tools.getPrefStr(
//            this,
//            getString(R.string.setting_playerName2_key),
//            getString(R.string.setting_playerName2_defaultValue)
//        )
        val mi2: MenuItem = menu?.findItem(R.id.menuPlayer2) as MenuItem
        mi2.title = getPlayerName(2)
//        mi2.title = pn2
        //プレーヤー3
//        val pn3 = Tools.getPrefStr(
//            this,
//            getString(R.string.setting_playerName3_key),
//            getString(R.string.setting_playerName3_defaultValue)
//        )
        val mi3: MenuItem = menu?.findItem(R.id.menuPlayer3) as MenuItem
        mi3.title = getPlayerName(3)
//        mi3.title = pn3
        //プレーヤー4
//        val pn4 = Tools.getPrefStr(
//            this,
//            getString(R.string.setting_playerName4_key),
//            getString(R.string.setting_playerName4_defaultValue)
//        )
        val mi4: MenuItem = menu?.findItem(R.id.menuPlayer4) as MenuItem
        mi4.title = getPlayerName(4)
//        mi4.title = pn4
        //プレーヤー5
//        val pn5 = Tools.getPrefStr(
//            this,
//            getString(R.string.setting_playerName5_key),
//            getString(R.string.setting_playerName5_defaultValue)
//        )
        val mi5: MenuItem = menu?.findItem(R.id.menuPlayer5) as MenuItem
        mi5.title = getPlayerName(5)
//        mi5.title = pn5
        //選択されているプレーヤ名にチェック、actionBarにプレーヤ名設定
//        var pn = ""
        when (playerNo) {
            1 -> {
                mi1.isChecked = true
//                pn = pn1
            }
            2 -> {
                mi2.isChecked = true
//                pn = pn2
            }
            3 -> {
                mi3.isChecked = true
//                pn = pn3
            }
            4 -> {
                mi4.isChecked = true
//                pn = pn4
            }
            5 -> {
                mi5.isChecked = true
//                pn = pn5
            }
        }
        //長いと他のメニューが表示されなり変更もできなくなるので10文字でカット
        //...の表示機能ないの? メニューにandroid:ellipsizeは効かないようだ
        var pn = getPlayerName(playerNo)
        if (pn.length <= 10)
            menu.findItem(R.id.menuPlayer).title = pn
        else
            menu.findItem(R.id.menuPlayer).title = pn.substring(0, 10) + "..."

        return true
    }

    private fun getPlayerName(playerNo: Int): String {
        //プレーヤ名取得
        when (playerNo) {
            1 -> {
                return Tools.getPrefStr(
                    this,
                    getString(R.string.setting_playerName1_key),
                    getString(R.string.setting_playerName1_defaultValue)
                )
            }
            2 -> {
                return Tools.getPrefStr(
                    this,
                    getString(R.string.setting_playerName2_key),
                    getString(R.string.setting_playerName2_defaultValue)
                )
            }
            3 -> {
                return Tools.getPrefStr(
                    this,
                    getString(R.string.setting_playerName3_key),
                    getString(R.string.setting_playerName3_defaultValue)
                )
            }
            4 -> {
                return Tools.getPrefStr(
                    this,
                    getString(R.string.setting_playerName4_key),
                    getString(R.string.setting_playerName4_defaultValue)
                )
            }
            5 -> {
                return Tools.getPrefStr(
                    this,
                    getString(R.string.setting_playerName5_key),
                    getString(R.string.setting_playerName5_defaultValue)
                )
            }
            else -> {
                return ""
            }
        }
    }

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.ACTION_STATE_IDLE
        ) {
            //復活 取り敢えずタッチ選択での駅交換機能はやめる
//            var selStatus = false //true:選択中
//            var beforePos = 0 //ひとつ前のitem位置
//            var downPos = 0 //down時item位置

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                //item選択時のイベント、itemをハイライト
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

//                    //復活 取り敢えずタッチ選択での駅交換機能はやめる
//                    //down位置保存
//                    downPos = viewHolder?.layoutPosition ?: 0

                    //markをデフォルト色に戻す
                    val tv: TextView = viewHolder?.itemView?.findViewById(R.id.staMark)
                        ?: findViewById(R.id.staMark)
                    tv.setTextColor(defaultColorStateList)
                    //tv.text = "〇"

                    //ハイライト
                    //ここを通らないのにハイライト位置が変わる? 0-15 4-19
                    viewHolder?.itemView?.alpha = 0.5f

                    //upで路線表示の更新が終わるまで上下方向の移動が出来ないよう設定
                    setDefaultDragDirs(ItemTouchHelper.ACTION_STATE_IDLE)

                    //Log.d("sc", downPos.toString())
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //item移動時のイベント
                val adapter = recyclerView.adapter as MainAdapter

                //layoutPositionで大丈夫な気がする?
                //val from = viewHolder.adapterPosition
                //val to = target.adapterPosition
                val from = viewHolder.layoutPosition
                val to = target.layoutPosition

//                Log.d("move1", from.toString())
//                Log.d("move2", to.toString())

//                //復活 取り敢えずタッチ選択での駅交換機能はやめる
//                //move開始時点で前回ハイライトitem解除
//                val beforeViewHolder = recyclerView.findViewHolderForAdapterPosition(beforePos)
//                if (beforeViewHolder != null && beforePos != from)
//                    beforeViewHolder.itemView.alpha = 1.0f

                //notifyItemMovedの前にAnswerTableの更新をしないと駄目なようだ
                //路線表示の切り替えはclearViewでまとめて行う
                moveItemAnswerStaUpdate(false, from, to)

                //adapterに変更通知、これをしないとDropが完了しない
                //notifyItemMovedはonMoveでしないと駄目なようだ
                adapter.notifyItemMoved(from, to)

//                //復活 取り敢えずタッチ選択での駅交換機能はやめる
//                //選択クリア
//                selStatus = false

                return true
            }

//            override fun onMoved(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                fromPos: Int,
//                target: RecyclerView.ViewHolder,
//                toPos: Int,
//                x: Int,
//                y: Int
//            ) {
//                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
//            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

//                //復活 取り敢えずタッチ選択での駅交換機能はやめる
//                val upPos = viewHolder.layoutPosition

                //Log.d("cv", upPos.toString())

                viewHolder.itemView.alpha = 1.0f //必要ないか?
                //路線表示の更新
                moveItemLineSwitcher()

//                //復活 取り敢えずタッチ選択での駅交換機能はやめる
//                if (downPos != upPos) {
//                    //downとupの位置が違うのでswapモードでは無い
//                    //item選択終了時のイベント、itemのハイライトを解除
//                    viewHolder.itemView.alpha = 1.0f //必要ないか?
//                    //路線表示の更新
//                    moveItemLineSwitcher()
//                    //選択クリア
//                    selStatus = false
//                } else {
//                    //選択状態on/off
//                    selStatus = !selStatus
//                    if (selStatus) {
//                        //ハイライト
//                        viewHolder.itemView.alpha = 0.5f
//
//                        //なぜか他のもう一か所ハイライトする?
//
//                        beforePos = upPos
//                    } else {
//                        if (beforePos != upPos) {
//                            //ひとつ前の位置と違うのでitem交換
//                            moveItemAnswerStaUpdate(true, beforePos, upPos)
//                            moveItemLineSwitcher()
//                        }
//                        viewHolder.itemView.alpha = 1.0f //必要ないか?
//                    }
//                }

                //adapter経由でイベントを発生させる場合はこんな感じ...
                //val adapter = recyclerView.adapter as MainAdapter
                //adapter.setMiListener(miListener)
                //adapter.moveItem(fromPos, toPos)

                //上下方向の移動停止を解除
                setDefaultDragDirs(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
                //Log.d("cv", "c3")
            }

            //長押しでup,downしたい場合はコメントにする
            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //横方向swipeのイベント、今回は使用しない
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        //recyclerViewでhandleがタッチされた場合にすぐドラッグできるように
        itemTouchHelper.startDrag(viewHolder)
    }

    //@RequiresApi(31)
    private fun moveItemAnswerStaUpdate(swap: Boolean, from: Int, to: Int) {
        //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
        val playerNo = Tools.getPrefInt(
            this@MainActivity,
            getString(R.string.setting_playerNo_key),
            getString(R.string.setting_playerNo_defaultValue)
        )
        val datasetNo = Tools.getPrefInt(
            this@MainActivity,
            getString(R.string.setting_datasetNo_key),
            getString(R.string.setting_datasetNo_defaultValue)
        )
        val lineNo =
            AnswerMain.getPlayStateTable(this@MainActivity, playerNo, datasetNo)?.lineNo ?: 0
        //AnswerTable更新
        val compStatus =
            Tools.playerAnswerStaUpdate(
                this,
                playerNo,
                datasetNo,
                lineNo,
                swap,
                from,
                to,
                startTime,
                getPlayerName(playerNo)
            )
        //startTimeのクリア判断
        //未comp->未comp  x クリアしない
        //未comp->comp    _ どちらでもよい
        //未comp->最速comp _ どちらでもよい
        //comp->未comp    c クリアする
        //comp->comp      _ 1駅ならありえるがその場合ここにこない
        //comp->最速compはあり得ない
        if (compStatus == Tools.EnumCompStatus.CompToNotComp)
            startTime = System.currentTimeMillis()
        //preferenceよりsoundNo、vibrationNo取得
        val soundNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_soundNo_key),
            getString(R.string.setting_soundNo_defaultValue)
        )
        val vibrationNo = Tools.getPrefInt(
            this,
            getString(R.string.setting_vibrationNo_key),
            getString(R.string.setting_vibrationNo_defaultValue)
        )
        //効果音、バイブレーション
        when (compStatus) {
            Tools.EnumCompStatus.NotCompToComp -> {
                //未comp->comp
                if (soundNo in 0..1)
                    SoundAndVibrator.playSoundComp()
                if (vibrationNo in 0..1)
                    SoundAndVibrator.runVibratorComp(this)
            }
            Tools.EnumCompStatus.NotCompToMinComp -> {
                //未comp->最速comp
                if (soundNo in 0..1)
                    SoundAndVibrator.playSoundRecord()
                if (vibrationNo in 0..1)
                    SoundAndVibrator.runVibratorRecord(this)
            }
            Tools.EnumCompStatus.NotCompToNotComp, Tools.EnumCompStatus.CompToNotComp, Tools.EnumCompStatus.CompToComp -> {
                //未comp->未comp,comp->未comp,comp->comp
                if (soundNo == 0)
                    SoundAndVibrator.playSoundMove()
                if (vibrationNo == 0)
                    SoundAndVibrator.runVibratorMove(this)
            }
            else -> {
            }
        }
    }

    private fun moveItemLineSwitcher() {
        //item移動時の路線再表示

        //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
        val playerNo = Tools.getPrefInt(
            this@MainActivity,
            getString(R.string.setting_playerNo_key),
            getString(R.string.setting_playerNo_defaultValue)
        )
        val datasetNo = Tools.getPrefInt(
            this@MainActivity,
            getString(R.string.setting_datasetNo_key),
            getString(R.string.setting_datasetNo_defaultValue)
        )
        val lineNo =
            AnswerMain.getPlayStateTable(this@MainActivity, playerNo, datasetNo)?.lineNo ?: 0

        Tools.lineSwitcher2(
            this,
            playerNo,
            datasetNo,
            lineNo,
            findViewById(R.id.lineHint),
            findViewById(R.id.lineRecord),
            findViewById(R.id.lineKana),
            findViewById(R.id.lineName),
            findViewById(R.id.lineRome),
            findViewById(R.id.lineSupplement1),
            findViewById(R.id.lineAnswer),
            findViewById(R.id.lineSupplement2),
            recyclerView
        )

    }

//    //adapter経由でイベントを発生する場合はこんな感じ...
//    private val miListener = object : MainAdapter.MiInterface {
//        override fun onMoveItem(from: Int, to: Int) {
//            super.onMoveItem(from, to)
//            //itemが移動した場合に発生
//            //debug
//            //Toast.makeText(this@MainActivity, "onMoveItem $from $to", Toast.LENGTH_LONG).show()
//
//            //preferenceよりplayerNo,datasetNo、dbよりlineNo取得
//            val playerNo = Tools.getPrefInt(
//                this@MainActivity,
//                getString(R.string.setting_playerNo_key),
//                getString(R.string.setting_playerNo_defaultValue)
//            )
//            val datasetNo = Tools.getPrefInt(
//                this@MainActivity,
//                getString(R.string.setting_datasetNo_key),
//                getString(R.string.setting_datasetNo_defaultValue)
//            )
//            val lineNo =
//                AnswerMain.getSelLineTable(this@MainActivity, playerNo, datasetNo)?.lineNo ?: 0
//
//            //AnswerTable更新とメイン画面表示
//            Tools.lineUpdateSwitcher(
//                this@MainActivity,
//                playerNo,
//                datasetNo,
//                lineNo,
//                findViewById(R.id.lineKana),
//                findViewById(R.id.lineName),
//                findViewById(R.id.lineRome),
//                findViewById(R.id.lineStatus),
//                recyclerView,
//                from,
//                to
//            )
//
//
//        }
//    }


}

