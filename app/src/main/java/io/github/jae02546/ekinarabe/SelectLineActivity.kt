package io.github.jae02546.ekinarabe

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class SelectLineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var first = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ActionBar設定
        supportActionBar?.setTitle(R.string.menu_selectLine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //preferenceよりplayerNo,datasetNo、dbよりfilterNoを取得
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
        val filterNo = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.filterNo ?: 0
        //PlayerAnswer.dbよりプレーヤ回答取得
        val ans: MutableMap<Int, AnswerMain.LineAnswerDc> =
            AnswerMain.getLineAnswerMap(this, playerNo, datasetNo)
        //路線選択dataset取得
        val ds: MutableList<DatasetMain.SelLineDc> =
            DatasetMain.getSelLineList(datasetNo, filterNo)
        //路線選択画面表示
        setContentView(R.layout.activity_selectline)
        layoutManager = LinearLayoutManager(this)
        val adapter = SelectLineAdapter(ds, ans)
        recyclerView = findViewById<RecyclerView>(R.id.selLineRecyclerView).also {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }

        //リスナ
        adapter.initClickListener = object : SelectLineAdapter.OnInitClickListener {
            override fun onItemClick(holder: SelectLineHolder) {
                //「最初から」で路線が選択された
//                //recyclerViewのoffsetをプレイ状態テーブルに書込
//                AnswerMain.putPlayStateTableSelLineOffset(
//                    this@SelectLineActivity,
//                    playerNo,
//                    datasetNo,
//                    recyclerView.computeVerticalScrollOffset()
//                )
                if (holder.cont.isEnabled) {
                    //回答がある場合は削除してよいか確認
                    AlertDialog.Builder(this@SelectLineActivity)
                        .setTitle(holder.name.text)
                        .setMessage(if (holder.comp) "コンプ済みですがシャッフルしますか?" else "プレイ途中ですがシャッフルしますか?")
                        .setPositiveButton("OK") { _, _ ->
                            //画面を閉じる
                            val intent = Intent().apply {
                                putExtra("button", R.string.button_init_key)
                                putExtra("lineNo", holder.lineNo)
                                putExtra("name", holder.name.text)
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        .setNegativeButton("No") { _, _ ->
                            //何もしない
                        }
                        .show()
                } else {
                    //画面を閉じる
                    val intent = Intent().apply {
                        putExtra("button", R.string.button_init_key)
                        putExtra("lineNo", holder.lineNo)
                        putExtra("name", holder.name.text)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
        adapter.contClickListener = object : SelectLineAdapter.OnContClickListener {
            override fun onItemClick(holder: SelectLineHolder) {
                //「続きから」で路線が選択された
//                //recyclerViewのoffsetをプレイ状態テーブルに書込
//                AnswerMain.putPlayStateTableSelLineOffset(
//                    this@SelectLineActivity,
//                    playerNo,
//                    datasetNo,
//                    recyclerView.computeVerticalScrollOffset()
//                )
                //画面を閉じる
                val intent = Intent().apply {
                    putExtra("button", R.string.button_cont_key)
                    putExtra("lineNo", holder.lineNo)
                    putExtra("name", holder.name.text)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
//        //未使用
//        adapter.nameClickListener = object : SelectLineAdapter.OnNameClickListener {
//            override fun onItemClick(holder: SelectLineHolder) {
//                //路線が選択されたら画面を閉じる
////                val intent = Intent().apply {
////                    putExtra("button", R.string.button_cont_key)
////                    putExtra("lineNo", holder.lineNo)
////                    putExtra("name", holder.name.text)
////                }
////                setResult(Activity.RESULT_OK, intent)
////                finish()
//            }
//        }

        //ここから広告
        MobileAds.initialize(this) { }
        val mAdView = findViewById<AdView>(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
        //ここまで広告
    }

    override fun onStart() {
        super.onStart()

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
        //表示位置を前回終了時に戻す
        val slOffset = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.selLineOffset ?: 0
        recyclerView.scrollToPosition(slOffset)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

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
        //recyclerViewのoffsetをプレイ状態テーブルに書込
        //検索結果を表示していた場合、次回に開くときは検索されていない状態なので
        //おかしな位置になる場合がある、検索時は0にすべき...
//        AnswerMain.putPlayStateTableSelLineOffset(
//            this@SelectLineActivity,
//            playerNo,
//            datasetNo,
//            recyclerView.computeVerticalScrollOffset()
//        )
        val llm = recyclerView.layoutManager as LinearLayoutManager
        AnswerMain.putPlayStateTableSelLineOffset(
            this@SelectLineActivity,
            playerNo,
            datasetNo,
            llm.findFirstVisibleItemPosition()
        )
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_selectline, menu)


        //そもそもrecyclerView.scrollToPositionは起動時効くのか?
        //recyclerView.scrollToPosition(2)

//
//        //起動時だけoffset効かないので取り敢えずここで...
//        //路線数が多く表示位置が下のほうだと表示がちょっと遅れる
//        if (!first) {
//            first = true
//            //preferenceよりplayerNo,datasetNo、dbよりselLineOffsetを取得
//            val playerNo = Tools.getPrefInt(
//                this,
//                getString(R.string.setting_playerNo_key),
//                getString(R.string.setting_playerNo_defaultValue)
//            )
//            val datasetNo = Tools.getPrefInt(
//                this,
//                getString(R.string.setting_datasetNo_key),
//                getString(R.string.setting_datasetNo_defaultValue)
//            )
//            val slOffset =
//                AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.selLineOffset ?: 0
//            //offsetを戻す
//            recyclerView.scrollBy(0, slOffset)
//        }
//


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.menuSearch).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = true //icon化で表示
            isSubmitButtonEnabled = false //検索ボタンは表示しない

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //検索文字確定後enterで発生するイベント

//                    //設定されたフィルタでデータセット取得
//                    val sp = PreferenceManager.getDefaultSharedPreferences(context)
//                    val playerStr = sp.getString(
//                        getString(R.string.setting_playerNo_key),
//                        getString(R.string.setting_playerNo_defaultValue)
//                    ).toString()
//                    val playerNo = try {
//                        playerStr.toInt()
//                    } catch (e: NumberFormatException) {
//                        1
//                    }
//                    val datasetStr = sp.getString(
//                        getString(R.string.setting_datasetNo_key),
//                        getString(R.string.setting_datasetNo_defaultValue)
//                    ).toString()
//                    val datasetNo = try {
//                        datasetStr.toInt()
//                    } catch (e: NumberFormatException) {
//                        0
//                    }
//                    val filterStr = sp.getString(
//                        getString(R.string.setting_filterNo_key),
//                        getString(R.string.setting_filterNo_defaultValue)
//                    ).toString()
//                    val filterNo = try {
//                        filterStr.toInt()
//                    } catch (e: NumberFormatException) {
//                        0
//                    }

                    //preferenceよりplayerNo,datasetNo、dbよりfilterNoを取得
                    val playerNo = Tools.getPrefInt(
                        context,
                        getString(R.string.setting_playerNo_key),
                        getString(R.string.setting_playerNo_defaultValue)
                    )
                    val datasetNo = Tools.getPrefInt(
                        context,
                        getString(R.string.setting_datasetNo_key),
                        getString(R.string.setting_datasetNo_defaultValue)
                    )
                    val filterNo =
                        AnswerMain.getPlayStateTable(context, playerNo, datasetNo)?.filterNo ?: 0

                    //PlayerAnswer.dbよりプレーヤ回答取得
                    val ans: MutableMap<Int, AnswerMain.LineAnswerDc> =
                        AnswerMain.getLineAnswerMap(context, playerNo, datasetNo)
                    //路線選択dataset取得
                    val ds: MutableList<DatasetMain.SelLineDc> =
                        DatasetMain.getSelLineList(datasetNo, filterNo, query)
//                    if (ds.size <= 0)
//                        Toast.makeText(context, "一致する路線は見つかりませんでした", Toast.LENGTH_LONG).show()
                    //if (ds.size == 0)
                    //    Toast.makeText(context, "路線数 ${ds.size}件", Toast.LENGTH_LONG).show()

                    //取得結果を反映
                    val adapter = SelectLineAdapter(ds, ans)
                    recyclerView.adapter = adapter
//                    adapter.initClickListener = object : SelectLineAdapter.OnInitClickListener {
//                        override fun onItemClick(holder: SelectLineHolder) {
//                            //路線が選択されたら画面を閉じる
//                            val intent = Intent().apply {
//                                putExtra("button", R.string.button_init_key)
//                                putExtra("lineNo", holder.lineNo)
//                                putExtra("name", holder.name.text)
//                            }
//                            setResult(Activity.RESULT_OK, intent)
//                            finish()
//                        }
//                    }
                    adapter.initClickListener = object : SelectLineAdapter.OnInitClickListener {
                        override fun onItemClick(holder: SelectLineHolder) {
                            if (holder.cont.isEnabled) {
                                //回答がある場合は削除してよいか確認
                                AlertDialog.Builder(this@SelectLineActivity)
                                    .setTitle(holder.name.text)
                                    .setMessage(if (holder.comp) "コンプ済みですがシャッフルしますか?" else "プレイ途中ですがシャッフルしますか?")
                                    .setPositiveButton("OK") { _, _ ->
                                        //画面を閉じる
                                        val intent = Intent().apply {
                                            putExtra("button", R.string.button_init_key)
                                            putExtra("lineNo", holder.lineNo)
                                            putExtra("name", holder.name.text)
                                        }
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    }
                                    .setNegativeButton("No") { _, _ ->
                                        //何もしない
                                    }
                                    .show()
                            } else {
                                //画面を閉じる
                                val intent = Intent().apply {
                                    putExtra("button", R.string.button_init_key)
                                    putExtra("lineNo", holder.lineNo)
                                    putExtra("name", holder.name.text)
                                }
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                    }
                    adapter.contClickListener = object : SelectLineAdapter.OnContClickListener {
                        override fun onItemClick(holder: SelectLineHolder) {
                            //路線が選択されたら画面を閉じる
                            val intent = Intent().apply {
                                putExtra("button", R.string.button_cont_key)
                                putExtra("lineNo", holder.lineNo)
                                putExtra("name", holder.name.text)
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }

                    //検索後searchViewを隠す場合はコメントを外す
                    //onActionViewCollapsed() //SearchViewを隠す
                    clearFocus() //Focusを外す（imeを閉じる）
                    //isIconified = false //虫眼鏡アイコンを隠す

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //検索文字入力途中で発生するイベント
                    //フィルタのメニューを操作していても発生する? その場合newTextは空文字列
                    return false
                }
            })
        }

//        //こちらは使わない
//        if (Intent.ACTION_SEARCH == intent.action) {
//            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
//                doLineSearch(query)
//            }
//            finish()
//            return true
//        }

        return true
    }

//    private fun doLineSearch(query: String) {
//        //検索文字確定語、Enterでここに来る
//        Toast.makeText(this, "$query", Toast.LENGTH_LONG).show()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menuSearch -> {
                //ここは未使用
                return true
            }
            R.id.menuFilter0, R.id.menuFilter1, R.id.menuFilter2, R.id.menuFilter3,
            R.id.menuFilter4, R.id.menuFilter5, R.id.menuFilter6, R.id.menuFilter7,
            R.id.menuFilter8 -> {
                //フィルタNo保存
                var fno = 0
                if (item.itemId == R.id.menuFilter0)
                    fno = 0
                if (item.itemId == R.id.menuFilter1)
                    fno = 1
                if (item.itemId == R.id.menuFilter2)
                    fno = 2
                if (item.itemId == R.id.menuFilter3)
                    fno = 3
                if (item.itemId == R.id.menuFilter4)
                    fno = 4
                if (item.itemId == R.id.menuFilter5)
                    fno = 5
                if (item.itemId == R.id.menuFilter6)
                    fno = 6
                if (item.itemId == R.id.menuFilter7)
                    fno = 7
                if (item.itemId == R.id.menuFilter8)
                    fno = 8
//                //フィルタNo preferences書込
//                Tools.putPrefInt(this, getString(R.string.setting_filterNo_key), fno)
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
                //選択されたフィルタNoのPlayStateTable書込
                AnswerMain.putPlayStateTableFilterNo(this, playerNo, datasetNo, fno)
//                //選択されたフィルタNoのPlayStateTable書込（他項目を変更しないように1度読込）
//                val before = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)
//                AnswerMain.putPlayStateTable(
//                    this,
//                    PlayStateTable(playerNo, datasetNo, before?.lineNo ?: 0, fno)
//                )

                //アクションバーフィルタ名設定（onPrepareOptionsMenu）
                invalidateOptionsMenu()
                //PlayerAnswer.dbよりプレーヤ回答取得
                val ans: MutableMap<Int, AnswerMain.LineAnswerDc> =
                    AnswerMain.getLineAnswerMap(this, playerNo, datasetNo)
                //路線選択dataset取得
                val ds: MutableList<DatasetMain.SelLineDc> =
                    DatasetMain.getSelLineList(datasetNo, fno)
                //if (ds.size == 0)
                //    Toast.makeText(this, "路線数 ${ds.size}件", Toast.LENGTH_LONG).show()

                //取得結果を反映
                val adapter = SelectLineAdapter(ds, ans)
                recyclerView.adapter = adapter
//                adapter.initClickListener = object : SelectLineAdapter.OnInitClickListener {
//                    override fun onItemClick(holder: SelectLineHolder) {
//                        //路線が選択されたら画面を閉じる
//                        val intent = Intent().apply {
//                            putExtra("button", R.string.button_init_key)
//                            putExtra("lineNo", holder.lineNo)
//                            putExtra("name", holder.name.text)
//                        }
//                        setResult(Activity.RESULT_OK, intent)
//                        finish()
//                    }
//                }
                adapter.initClickListener = object : SelectLineAdapter.OnInitClickListener {
                    override fun onItemClick(holder: SelectLineHolder) {
                        if (holder.cont.isEnabled) {
                            //回答がある場合は削除してよいか確認
                            AlertDialog.Builder(this@SelectLineActivity)
                                .setTitle(holder.name.text)
                                .setMessage(if (holder.comp) "コンプ済みですがシャッフルしますか?" else "プレイ途中ですがシャッフルしますか?")
                                .setPositiveButton("OK") { _, _ ->
                                    //画面を閉じる
                                    val intent = Intent().apply {
                                        putExtra("button", R.string.button_init_key)
                                        putExtra("lineNo", holder.lineNo)
                                        putExtra("name", holder.name.text)
                                    }
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                .setNegativeButton("No") { _, _ ->
                                    //何もしない
                                }
                                .show()
                        } else {
                            //画面を閉じる
                            val intent = Intent().apply {
                                putExtra("button", R.string.button_init_key)
                                putExtra("lineNo", holder.lineNo)
                                putExtra("name", holder.name.text)
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
                adapter.contClickListener = object : SelectLineAdapter.OnContClickListener {
                    override fun onItemClick(holder: SelectLineHolder) {
                        //路線が選択されたら画面を閉じる
                        val intent = Intent().apply {
                            putExtra("button", R.string.button_cont_key)
                            putExtra("lineNo", holder.lineNo)
                            putExtra("name", holder.name.text)
                        }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //アクションバーメニュー設定
//        val sp = PreferenceManager.getDefaultSharedPreferences(this)
//        val str = sp.getString(
//            getString(R.string.setting_filterNo_key),
//            getString(R.string.setting_filterNo_defaultValue)
//        ).toString()
//        val fno = try {
//            str!!.toInt()
//        } catch (e: NumberFormatException) {
//            0
//        }
        //preferenceよりplayerNo,datasetNo、dbよりfilterNoを取得
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
        val fno = AnswerMain.getPlayStateTable(this, playerNo, datasetNo)?.filterNo ?: 0
        //フィルタメニュー選択
        when (fno) {
            0 -> {
                menu!!.findItem(R.id.menuFilter0).isChecked = true
            }
            1 -> {
                menu!!.findItem(R.id.menuFilter1).isChecked = true
            }
            2 -> {
                menu!!.findItem(R.id.menuFilter2).isChecked = true
            }
            3 -> {
                menu!!.findItem(R.id.menuFilter3).isChecked = true
            }
            4 -> {
                menu!!.findItem(R.id.menuFilter4).isChecked = true
            }
            5 -> {
                menu!!.findItem(R.id.menuFilter5).isChecked = true
            }
            6 -> {
                menu!!.findItem(R.id.menuFilter6).isChecked = true
            }
            7 -> {
                menu!!.findItem(R.id.menuFilter7).isChecked = true
            }
            8 -> {
                menu!!.findItem(R.id.menuFilter8).isChecked = true
            }
        }
        //フィルタ名設定
        val item = menu?.findItem(R.id.menuFilter)
        val array = resources.getStringArray(R.array.menu_filter_title)
        if (array.size > fno)
            item?.title = array[fno]
        else
            item?.title = array[0]

        return true
    }


}