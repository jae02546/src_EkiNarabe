package io.github.jae02546.ekinarabe

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration

object Tools {

//    var flg = false

    //preferences読込Int
    fun getPrefInt(context: Context, key: String, default: String): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val str = sp.getString(key, default)
        return str?.toInt() ?: default.toInt()
    }

    //preferences読込String
    fun getPrefStr(context: Context, key: String, default: String): String {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getString(key, default) ?: ""
    }

    //preferences読込Boolean
    fun getPrefBool(context: Context, key: String, default: String): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val str = sp.getString(key, default)
        return str?.toBoolean() ?: default.toBoolean()
    }

    //preferences書込Int
    fun putPrefInt(context: Context, key: String, value: Int) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putString(key, value.toString())
        editor.apply()
    }

    //preferences書込String
    fun putPrefStr(context: Context, key: String, value: String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
    }

    //preferences書込Boolean
    fun putPrefBool(context: Context, key: String, value: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putString(key, value.toString())
        editor.apply()
    }

    //PlayerAnswer.db AnswerTable 存在チェック、整合性チェック
    private fun playerAnswerCheck(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        lineNo: Int,
        init: Boolean
    ) {
        //DataSetの路線データとPlayerAnswer.dbのプレーヤ回答データの
        //駅No、駅数をチェックし整合性が無い場合は修正し書込む
        //整合性が無い可能性としては、新駅、廃駅、単なるバグ、何らかの異常終了等が考えられる
        //回答データが無い場合は新規に作成する
        if (lineNo <= 0)
            return //路線Noが0なら抜ける
        //路線データ駅No取得
        val lineStaNoList = DatasetMain.getLineStaNo(datasetNo, lineNo)
        //路線回答データ取得
        val ansTable = AnswerMain.getAnswerTable(context, playerNo, datasetNo, lineNo)
        //路線回答データnullチェック
        if (ansTable == null || init) {
            //路線回答データが無い、または最初から
            var sno: MutableList<Int> = mutableListOf()
            for (v in lineStaNoList)
                sno += v
            if (sno.size >= 2) {
                //最初なのでシャッフル
                //シャッフル後compしていた場合を考え10回まで繰り返す
                for (i in 0..9) {
                    sno.shuffle()
                    if (sno != lineStaNoList)
                        break
                    //else
                    //    continue
                }
                //10回シャッフルしてもcompしていた場合は最初と最後を入れ替える（1駅しかないとあり得る）
                if (sno == lineStaNoList) {
                    val first = sno[0]
                    val last = sno[sno.size - 1]
                    sno[0] = first
                    sno[sno.size - 1] = last
                }
            }
            //PlayerAnswer.dbに書込み
            val at = AnswerTable(playerNo, datasetNo, lineNo, sno, Duration.ZERO, 0)
            AnswerMain.putAnswerTable(context, at)
            if (ansTable != null)
                Toast.makeText(context, "シャッフルしました", Toast.LENGTH_SHORT).show()
        } else {
            //路線データの駅が路線回答データに全て存在するかチェック
            var err: Boolean = false
            for (v in lineStaNoList) {
                if (!ansTable.answerList.contains(v)) {
                    err = true
                    break
                }
            }
            //全て存在する場合は件数をチェック
            if (!err) {
                //件数が同じなら整合性ok
                if (lineStaNoList.size == ansTable.answerList.size)
                    return
            }
            //整合性が無いので修正
            val staNoList: MutableList<Int> = mutableListOf()
            //路線データに無い駅Noを削除して詰める（廃駅）
            for (v in ansTable.answerList)
                if (lineStaNoList.contains(v))
                    staNoList += v
            //回答データに無い駅Noを最後に追加（新駅）
            for (v in lineStaNoList)
                if (!staNoList.contains(v))
                    staNoList += v
            //PlayerAnswer.dbに書込み
            val at = AnswerTable(
                ansTable.playerNo,
                ansTable.datasetNo,
                ansTable.lineNo,
                staNoList,
                ansTable.lastCompTime,
                ansTable.offset
            )
            AnswerMain.putAnswerTable(context, at)
            //ここで回答データが変更されたのでcomp情報の変更が必要かと思ったが
            //最速compは追加削除があっても取り消しはされないため関係無い
            //プレイヤ毎のcompは毎回確認しなおしているので大丈夫
        }
    }

    //回答駅リスト作成
    private fun getAnswerStaDc(
        line: DatasetMain.LineDc,
        lineAnswer: AnswerMain.LineAnswerDc
    ): MutableList<DatasetMain.StaDc> {
        //line:路線データ
        //lineAnswer:路線回答データ

        //comp判断
        var comp = false
        val foo: MutableList<Int> = mutableListOf()
        for (v in line.staList)
            foo += v.staNo
        if (foo == lineAnswer.answerList)
            comp = true

        //逆引きindex作成
        val index: MutableMap<Int, Int> = mutableMapOf()
        for ((i, v) in line.staList.withIndex()) {
            index += v.staNo to i
        }

        val staList: MutableList<DatasetMain.StaDc> = mutableListOf()
        for (v in lineAnswer.answerList)
            if (index.containsKey(v)) {
                staList += DatasetMain.StaDc(
                    v,
                    line.staList[index[v]!!].name ?: "",
                    line.staList[index[v]!!].kana ?: "",
                    line.staList[index[v]!!].rome ?: "",
                    line.staList[index[v]!!].supplement ?: "",
                    line.staList[index[v]!!].close ?: false,
                    line.staList[index[v]!!].address ?: "",
                    line.staList[index[v]!!].url ?: "",
                    line.staList[index[v]!!].correctNo,
                    comp
                )
            } else {
                //無いはず...
                staList += DatasetMain.StaDc(v, "", "", "", "", false, "", "", 0, false)
            }
        return staList
    }

    enum class EnumCompStatus {
        None,
        NotCompToNotComp, //未comp->未comp
        NotCompToComp,    //未comp->comp
        NotCompToMinComp, //未comp->最速comp
        CompToNotComp,    //comp->未comp
        CompToComp        //comp->comp
    }

    //PlayerAnswer.db AnswerTable の駅順入替
    //compの場合はlastCompTime設定
    fun playerAnswerStaUpdate(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        lineNo: Int,
        swap: Boolean,
        from: Int,
        to: Int,
        startTime: Long,
        playerName: String
    ): EnumCompStatus {
        //戻り値 true:startTimeをクリアする
        //事前にplayerAnswerCheckが実行されているはずなので
        //回答が存在しない整合性が無い場合は無いはず
        //item移動時のAnswerTableの駅順入れ替え
        if (lineNo <= 0)
            return EnumCompStatus.None //路線Noが0なら抜ける
        //路線回答データ取得 nullなら抜ける
        val ans = AnswerMain.getAnswerTable(context, playerNo, datasetNo, lineNo)
            ?: return EnumCompStatus.None
        //formをtoの位置に入れる
        val sno: MutableList<Int> = mutableListOf()
        if (!swap) {
            //ドッラグで移動
            for (i in ans.answerList.indices) {
                //from>toの場合toの位置にきたらfromを入れる
                if (from > to && i == to)
                    sno += ans.answerList[from]
                //fromの位置は飛ばす
                if (i != from)
                    sno += ans.answerList[i]
                //from<toの場合toの位置にきたらfromを入れる
                if (from < to && i == to)
                    sno += ans.answerList[from]
            }
        } else {
            //タッチで交換
            for (i in ans.answerList.indices) {
                //from位置にきたらtoを入れる
                //to位置にきたらfromを入れる
                sno += when (i) {
                    from -> ans.answerList[to]
                    to -> ans.answerList[from]
                    else -> ans.answerList[i]
                }
            }
        }
        //入替前後のcomp状態
        val before = isComplete(datasetNo, lineNo, ans.answerList as MutableList<Int>)
        val after = isComplete(datasetNo, lineNo, sno)
        //未compからcompならlastCompTimeに加算
        var ct = ans.lastCompTime
        if (!before && after)
            ct = ct.plusMillis(System.currentTimeMillis() - startTime)
        //PlayerAnswer.dbに書込み
        val at = AnswerTable(
            ans.playerNo,
            ans.datasetNo,
            ans.lineNo,
            sno,
            ct,
            ans.offset
        )
        AnswerMain.putAnswerTable(context, at)
        //compで最速ならMinCompTimeTable更新
        var afterMin = false
        if (!before && after) {
            val min = AnswerMain.getRecordTable(context, ans.datasetNo, ans.lineNo)
            if (min == null || min.recordTime > ct) {
                AnswerMain.putRecordTable(
                    context,
                    RecordTable(
                        ans.datasetNo,
                        ans.lineNo,
                        ans.playerNo,
                        playerName,
                        ct
                    )
                )
                afterMin = true
            }
        }
        //startTimeのクリア判断
        //未comp->未comp  x クリアしない
        //未comp->comp    _ どちらでもよい
        //未comp->最速comp _ どちらでもよい
        //comp->未comp    c クリアする
        //comp->comp      _ 1駅ならありえるがその場合ここにこない
        //comp->最速compはあり得ない
        return if (!before) {
            if (!after)
                EnumCompStatus.NotCompToNotComp
            else if (!afterMin)
                EnumCompStatus.NotCompToComp
            else
                EnumCompStatus.NotCompToMinComp
        } else {
            if (!after)
                EnumCompStatus.CompToNotComp
            else
                EnumCompStatus.CompToComp
        }
    }

    //PlayerAnswer.db AnswerTable lastCompTimeとoffset更新
    //路線切替前のcomp時間の加算とoffsetの保存のため
    fun playerAnswerCompAndOffsetUpdate(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        lineNo: Int,
        startTime: Long,
        offset: Int
    ) {
        //事前にplayerAnswerCheckが実行されているはずなので
        //回答が存在しない整合性が無い場合は無いはず
        if (lineNo <= 0)
            return //路線Noが0なら抜ける
        //路線回答データ取得 nullなら抜ける
        val ans = AnswerMain.getAnswerTable(context, playerNo, datasetNo, lineNo) ?: return
        //compならlastCompTimeはそのまま
        var ct = ans.lastCompTime
        if (!isComplete(datasetNo, lineNo, ans.answerList as MutableList<Int>))
            ct = ct.plusMillis(System.currentTimeMillis() - startTime)
        //PlayerAnswer.dbに書込み
        val at = AnswerTable(
            ans.playerNo,
            ans.datasetNo,
            ans.lineNo,
            ans.answerList,
            ct,
            offset
        )
        AnswerMain.putAnswerTable(context, at)
    }

    private fun isComplete(datasetNo: Int, lineNo: Int, answerList: MutableList<Int>): Boolean {
        //戻り値 true:complete
        val staList = DatasetMain.getLineStaNo(datasetNo, lineNo)
        return staList == answerList
    }

    //路線切替時の処理（メイン画面表示）
    fun lineSwitcher(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        lineNo: Int,
        init: Boolean,
        hint: TextView,
        record: TextView,
        kana: TextView,
        name: TextView,
        rome: TextView,
        supplement1: TextView,
        answer: TextView,
        supplement2: TextView,
        recyclerView: RecyclerView
    ): MainAdapter {
        //使用タイミング
        //起動時、プレーヤ変更時、データセット変更時、路線変更時
        //戻り値使用箇所無し?

        //PlayerAnswer.db AnswerTable 存在チェック、整合性チェック
        playerAnswerCheck(context, playerNo, datasetNo, lineNo, init)

        //路線表示、駅表示
        var mainAdapter = MainAdapter(mutableListOf())
        if (lineNo == 0) {
            //路線Noが0の場合
            hint.text = ""
            record.text = ""
            kana.text = ""
            name.text = "路線を選択してください"
            rome.text = ""
            supplement1.text = ""
            answer.text = ""
            supplement2.text = ""
            //駅一覧表示
            recyclerView.adapter = mainAdapter
        } else {
            //路線データ取得
            val lineDs = DatasetMain.getLine(datasetNo, lineNo)
            //路線回答データ取得
            val lineAnswerDs = AnswerMain.getLineAnswer(context, playerNo, datasetNo, lineNo)
            //路線ステータス取得
            val lineStatus = getLineStatus(lineDs, lineAnswerDs)

            if (lineDs.url != "") {
//                hint.text =
//                    HtmlCompat.fromHtml(
//                        "<a href=" + lineDs.url + ">" + hint.text + "</a>",
//                        HtmlCompat.FROM_HTML_MODE_COMPACT
//                    )
                //strings.htmlで定義した意味がなくなってしまうが...
                hint.text =
                    HtmlCompat.fromHtml(
                        "<a href=" + lineDs.url + ">" + "Hint" + "</a>",
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                hint.movementMethod = LinkMovementMethod.getInstance();
            } else
                hint.text = ""
            record.text = HtmlCompat.fromHtml(lineStatus[3], HtmlCompat.FROM_HTML_MODE_COMPACT)
            kana.text = lineDs.kana
            name.text = lineDs.name
            rome.text = lineDs.rome
            if (lineStatus[0] == "") {
                supplement1.visibility = View.GONE
                supplement1.text = ""
            } else {
                supplement1.visibility = View.VISIBLE
                supplement1.text =
                    HtmlCompat.fromHtml(lineStatus[0], HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            answer.text = HtmlCompat.fromHtml(lineStatus[2], HtmlCompat.FROM_HTML_MODE_COMPACT)
            supplement2.text = HtmlCompat.fromHtml(lineStatus[1], HtmlCompat.FROM_HTML_MODE_COMPACT)
            //駅一覧表示
            mainAdapter = MainAdapter(getAnswerStaDc(lineDs, lineAnswerDs))
            recyclerView.adapter = mainAdapter
            //再表示前の位置に戻す
            //起動時だけなぜか戻らない?
            recyclerView.scrollBy(0, lineAnswerDs.offset)
            //こっちは効くね?
            //recyclerView.scrollToPosition(2)
            //起動時はoffset効かないが、起動後の路線切り替えでは効くの
            //取り敢えず良しとするか、それとも多少表示位置はづれるが
            //scrollToPositionで行くか?
        }
        //recyclerView.invalidate() //recyclerViewリフレッシュ

        return mainAdapter
    }

    //駅順入れ替え時メイン画面再表示
    fun lineSwitcher2(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        lineNo: Int,
        hint: TextView,
        record: TextView,
        kana: TextView,
        name: TextView,
        rome: TextView,
        supplement1: TextView,
        answer: TextView,
        supplement2: TextView,
        recyclerView: RecyclerView
    ) {
        //路線表示、駅表示
        if (lineNo == 0) {
            //路線Noが0の場合（ここにはこないはず）
            hint.text = ""
            record.text = ""
            kana.text = ""
            name.text = "路線を選択してください"
            rome.text = ""
            answer.text = ""
            supplement2.text = ""
            //駅一覧表示
            recyclerView.adapter = MainAdapter(mutableListOf())
        } else {
            //路線データ取得
            val lineDs = DatasetMain.getLine(datasetNo, lineNo)
            //路線回答データ取得
            val lineAnswerDs = AnswerMain.getLineAnswer(context, playerNo, datasetNo, lineNo)
            //路線ステータス取得
            val lineStatus = getLineStatus(lineDs, lineAnswerDs)

            if (lineDs.url != "") {
//                hint.text =
//                    HtmlCompat.fromHtml(
//                        "<a href=" + lineDs.url + ">" + hint.text + "</a>",
//                        HtmlCompat.FROM_HTML_MODE_COMPACT
//                    )
                //strings.htmlで定義した意味がなくなってしまうが...
                hint.text =
                    HtmlCompat.fromHtml(
                        "<a href=" + lineDs.url + ">" + "Hint" + "</a>",
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                hint.movementMethod = LinkMovementMethod.getInstance();
            } else
                hint.text = ""
            record.text = HtmlCompat.fromHtml(lineStatus[3], HtmlCompat.FROM_HTML_MODE_COMPACT)
            kana.text = lineDs.kana
            name.text = lineDs.name
            rome.text = lineDs.rome
            if (lineStatus[0] == "") {
                supplement1.visibility = View.GONE
                supplement1.text = ""
            } else {
                supplement1.visibility = View.VISIBLE
                supplement1.text =
                    HtmlCompat.fromHtml(lineStatus[0], HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            answer.text = HtmlCompat.fromHtml(lineStatus[2], HtmlCompat.FROM_HTML_MODE_COMPACT)
            supplement2.text = HtmlCompat.fromHtml(lineStatus[1], HtmlCompat.FROM_HTML_MODE_COMPACT)
            //駅一覧表示
            //再表示前のoffset取得
            val offset = recyclerView.computeVerticalScrollOffset()
            //adapter再設定
            val mainAdapter = MainAdapter(getAnswerStaDc(lineDs, lineAnswerDs))
            //Log.d("ls", "l1")
            recyclerView.adapter = mainAdapter
            //Log.d("ls", "l2")
            //再表示前の位置に戻す
            recyclerView.scrollBy(0, offset)
        }
        //recyclerView.invalidate() //recyclerViewリフレッシュ

        return
    }

    //路線状況作成（メイン画面）
    private fun getLineStatus(
        line: DatasetMain.LineDc,
        lineAnswer: AnswerMain.LineAnswerDc
    ): List<String> {
        val snl: MutableList<Int> = mutableListOf()
        for ((k, v) in line.staList) {
            snl += k
        }
        val sld = DatasetMain.SelLineDc(
            line.lineNo,
            line.name,
            line.kana,
            line.rome,
            line.supplement,
            line.close,
            line.url,
            snl
        )
        return getLineStatus(sld, lineAnswer)
    }

    //路線状況作成（路線選択）
    fun getLineStatus(
        line: DatasetMain.SelLineDc,
        lineAnswer: AnswerMain.LineAnswerDc
    ): List<String> {
        //List[0]:supplement1
        //  ローマ字の下の情報
        //  廃線、愛称、事業者名等
        //List[1]:supplement2
        //  メイン画面の右側の情報、路線選択では表示されない
        //  廃線時期、廃線区間、移管情報等
        //List[2]:answer
        //  complete 00:00:00 m/n駅
        //  compはcomp時のみ赤で表示
        //List[3]:record
        //  recordTimeが0以上の場合以下の表示
        //   record
        //  00:00:00
        //  プレーヤ名

        //supplement
        var supplement = line.supplement.split("|")
        //supplement1
        var supplement1 = ""
        if (line.close)
            supplement1 += "廃線 "
        if (supplement.isNotEmpty() && supplement[0] != "")
            supplement1 += supplement[0]
        //supplement2
        var supplement2 = ""
        if (supplement.size >= 2 && supplement[1] != "")
            supplement2 += supplement[1]
        //answer
        var mSta: Int = 0 //正解駅数
        var nSta: Int = line.staNoList.size //路線駅数
        val comp = "<font color=#ff0000>complete</font>" //comp文字 赤
        var lct = "" //最終回答時間 赤
        //正解数カウント
        for (i in 0 until line.staNoList.size)
            if (i < lineAnswer.answerList.size && (line.staNoList[i] == lineAnswer.answerList[i]))
                mSta++
        //最終回答時間
        var lSec = 0L
        if (lineAnswer.lastCompTime.toMillis() >= 1000)
            lSec = lineAnswer.lastCompTime.seconds
        else if (lineAnswer.lastCompTime.toMillis() > 0)
            lSec = 1 //1sec以下でのcompもあり得るがその場合1secと表示する
        val lh = lSec / 3600
        val lm = (lSec - lh * 3600) / 60
        val ls = (lSec - lh * 3600 - lm * 60)
        lct = "<font color=#ff0000>" + lh.toString().padStart(2, '0') + ":" + lm.toString()
            .padStart(2, '0') + ":" + ls.toString().padStart(2, '0') + "</font>"
        var answer = ""
        //駅数が1以上でcompしている場合はcomp表示
        if (line.staNoList.size >= 1 && line.staNoList == lineAnswer.answerList)
            answer += "$comp $lct "
        answer += mSta.toString() + "/" + nSta.toString() + "駅"
        //record
        var mSec = 0L
        if (lineAnswer.recordTime.toMillis() >= 1000)
            mSec = lineAnswer.recordTime.seconds
        else if (lineAnswer.recordTime.toMillis() > 0)
            mSec = 1 //1sec以下でのcompもあり得るがその場合1secと表示する
        val mh = mSec / 3600
        val mm = (mSec - mh * 3600) / 60
        val ms = (mSec - mh * 3600 - mm * 60)
        var record = ""
        if (mSec > 0L)
            record = "最速コンプ<br>" +
                    mh.toString().padStart(2, '0') + ":" +
                    mm.toString().padStart(2, '0') + ":" +
                    ms.toString().padStart(2, '0') + "<br>" +
                    lineAnswer.recordPlayerName

        return listOf(supplement1, supplement2, answer, record)
    }

    //プレイ状態取得
    fun getPlayStatus(
        context: Context,
        datasetNo: Int,
        playerName: MutableMap<Int, String>
    ): MutableList<String> {
        //引数
        //datasetNo:対象のデータセットNo
        //playerName:現状のプレーヤ名
        data class PlayStatus(
            //val playerNo: Int, //keyと被るので省略
            var name: MutableSet<String>,
            var numComp: Int,
            var numMinComp: Int
        )
        //全件取得してもしょうがない? 取り敢えず全件で...
        val ds = DatasetMain.getSelLineList(datasetNo)
        val ans = AnswerMain.getAnswerTable(context)
        val rec = AnswerMain.getRecordTable(context, datasetNo)
        //逆引きmap<lineNo,i>
        val dsIndex = mutableMapOf<Int, Int>()
        for (i in 0 until ds.size) {
            if (!dsIndex.containsKey(ds[i].lineNo)) {
                dsIndex += ds[i].lineNo to i
            }
        }
        //路線数、comp数
        val numLine = ds.size
        val numComp = rec?.size ?: 0
        //<プレーヤNo,PlayStatus(プレーヤ名,comp数,最速comp数)>
        val ps: MutableMap<Int, PlayStatus> = mutableMapOf()
        //ansをloopしてプレーヤ毎のcomp数をカウント
        if (ans != null) {
            for (v in ans) {
                if (v.datasetNo == datasetNo && dsIndex.containsKey(v.lineNo)) {
                    if (v.answerList == ds[dsIndex[v.lineNo] ?: 0].staNoList)
                        if (!ps.containsKey(v.playerNo)) {
                            ps += v.playerNo to PlayStatus(
                                mutableSetOf(playerName[v.playerNo] ?: ""), //最初に現状のプレーヤ名を表示
                                1,
                                0
                            )
                        } else {
                            ps[v.playerNo]?.numComp = ps[v.playerNo]?.numComp?.plus(1) ?: 0
                        }
                }
            }
        }
        //recをloopしてプレーヤ毎の最速comp数をカウント
        if (rec != null) {
            for (v in rec) {
                if (v.datasetNo == datasetNo) {
                    if (!ps.containsKey(v.playerNo)) {
                        ps += v.playerNo to PlayStatus(mutableSetOf(v.playerName), 0, 1)
                    } else {
                        ps[v.playerNo]?.name?.add(v.playerName)
                        ps[v.playerNo]?.numMinComp = ps[v.playerNo]?.numMinComp?.plus(1) ?: 0
                    }
                }
            }
        }

        //全路線数:nnn
        //comp路線数:nnn
        //プレイヤー1
        //  comp路線数:nnn
        //  最速comp路線数:nnn
        //プレイヤー2
        //  comp路線数:nnn
        //  最速comp路線数:nnn
        //プレイヤー3
        //  comp路線数:nnn
        //  最速comp路線数:nnn
        //プレイヤー4
        //  comp路線数:nnn
        //  最速comp路線数:nnn
        //プレイヤー5
        //  comp路線数:nnn
        //  最速comp路線数:nnn

        val ret: MutableList<String> = mutableListOf()
        ret += "全路線数 $numLine"
        ret += "コンプ数 $numComp"
        for (i in 1..5) {
            var pRet = ""
            if (ps.containsKey(i)) {
                pRet += "プレイヤー$i（"
                for (v in ps[i]?.name ?: PlayStatus(mutableSetOf(), 0, 0).name)
                    pRet += "$v,"
                pRet = pRet.trimEnd(',')
                pRet += "）"
                ret += pRet
                ret += "コンプ数 " + (if (ps[i]?.numComp != null) ps[i]?.numComp.toString() else "0")
                ret += "最速コンプ数 " + (if (ps[i]?.numMinComp != null) ps[i]?.numMinComp.toString() else "0")
            } else {
                //コンプが無かった場合は現状のプレーヤ名を表示
                ret += "プレイヤー$i" + (if (playerName.containsKey(i)) "（" + playerName[i] + "）" else "")
                ret += "コンプ数 0"
                ret += "最速コンプ数 0"
            }
        }

        return ret
    }


}