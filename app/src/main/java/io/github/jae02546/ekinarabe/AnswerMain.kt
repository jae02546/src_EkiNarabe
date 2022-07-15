package io.github.jae02546.ekinarabe

import android.content.Context
import java.time.Duration

object AnswerMain {

    //路線回答データクラス
    //offsetまでAnswerTable、それ以降RecordTable
    //プレーヤNoとプレーヤ名を持つ、本来どちらかでよいがプレイ途中での
    //プレーヤ名変更を考えるとcomp時点の名前の表示がよいかもしれない...
    //路線No,回答リスト<駅No>,最終comp時間,offset値,最速compプレーヤNo,最速compプレーヤ名,最速comp時間
    data class LineAnswerDc(
        val lineNo: Int = 0,
//        var answerList: List<Int> = listOf(),
        var answerList: MutableList<Int> = mutableListOf(),
        val lastCompTime: Duration = Duration.ZERO,
        val offset: Int = 0,
        val recordPlayerNo: Int = 0,
        val recordPlayerName: String = "",
        val recordTime: Duration = Duration.ZERO
    )

//    //最速comp時間データクラス
//    //プレーヤNoとプレーヤ名を持つ、本来どちらかでよいがプレイ途中での
//    //プレーヤ名変更を考えるとcomp時点の名前の表示がよいかもしれない...
//    //路線No,最速compプレーヤNo,最速compプレーヤ名,最速comp時間
//    private data class RecordDc(
//        val lineNo: Int = 0,
//        val playerNo: Int = 0,
//        val playerName: String = "",
//        val recordTime: Duration = Duration.ZERO
//    )

    //路線回答テーブルの全レコード取得
    fun getAnswerTable(context: Context): List<AnswerTable>? {
        //該当レコードが無い場合nullとなる
        val db = AnswerDatabase.getInstance(context)
        val ansDao = db.answerDao()
        return ansDao.getAll()
    }

    //路線回答テーブルのレコード取得
    fun getAnswerTable(context: Context, playerNo: Int, datasetNo: Int, lineNo: Int): AnswerTable? {
        //該当レコードが無い場合nullとなる
        val db = AnswerDatabase.getInstance(context)
        val ansDao = db.answerDao()
        return ansDao.getAnswer(playerNo, datasetNo, lineNo)
    }

    //路線回答テーブルのレコード書込
    fun putAnswerTable(context: Context, answerTable: AnswerTable) {
        val db = AnswerDatabase.getInstance(context)
        val ansDao = db.answerDao()
        val ans =
            ansDao.getAnswer(answerTable.playerNo, answerTable.datasetNo, answerTable.lineNo)
        if (ans == null) {
            //追加
            ansDao.insert(answerTable)
        } else {
            //更新
            ansDao.update(answerTable)
        }
    }

    //最速comp時間テーブルのレコード取得（データセットNo）
    fun getRecordTable(context: Context, datasetNo: Int): List<RecordTable>? {
        //該当レコードが無い場合nullとなる
        val db = AnswerDatabase.getInstance(context)
        val minDao = db.recordDao()
        return minDao.getRecord(datasetNo)
    }

    //最速comp時間テーブルのレコード取得（データセットNo, 路線No）
    fun getRecordTable(context: Context, datasetNo: Int, lineNo: Int): RecordTable? {
        //該当レコードが無い場合nullとなる
        val db = AnswerDatabase.getInstance(context)
        val minDao = db.recordDao()
        return minDao.getRecord(datasetNo, lineNo)
    }

    //最速comp時間テーブルのレコード書込
    fun putRecordTable(context: Context, recordTable: RecordTable) {
        val db = AnswerDatabase.getInstance(context)
        val minDao = db.recordDao()
        val min = minDao.getRecord(recordTable.datasetNo, recordTable.lineNo)
        if (min == null) {
            //追加
            minDao.insert(recordTable)
        } else {
            //更新
            minDao.update(recordTable)
        }
    }

    //プレイ状態テーブルのレコード取得
    fun getPlayStateTable(context: Context, playerNo: Int, datasetNo: Int): PlayStateTable? {
        //該当レコードが無い場合nullとなる
        val db = AnswerDatabase.getInstance(context)
        val playStateDao = db.playStateDao()
        return playStateDao.getPlayState(playerNo, datasetNo)
    }

//    //プレイ状態テーブルのレコード書込
//    fun putPlayStateTable(context: Context, playStateTable: PlayStateTable) {
//        val db = AnswerDatabase.getInstance(context)
//        val playStateDao = db.playStateDao()
//        val slt = playStateDao.getPlayState(playStateTable.playerNo, playStateTable.datasetNo)
//        if (slt == null) {
//            //追加
//            playStateDao.insert(playStateTable)
//        } else {
//            //更新
//            playStateDao.update(playStateTable)
//        }
//    }

    //プレイ状態テーブルlineNo書込
    fun putPlayStateTableLineNo(context: Context, playerNo: Int, datasetNo: Int, lineNo: Int) {
        val db = AnswerDatabase.getInstance(context)
        val playStateDao = db.playStateDao()
        //他の項目を上書きしないよう一旦リードする
        val slt = playStateDao.getPlayState(playerNo, datasetNo)
        val playStateTable =
            PlayStateTable(
                playerNo,
                datasetNo,
                lineNo,
                slt?.filterNo ?: 0,
                slt?.selLineOffset ?: 0,
                slt?.finalSelLine ?: false
            )
        if (slt == null) {
            //追加
            playStateDao.insert(playStateTable)
        } else {
            //更新
            playStateDao.update(playStateTable)
        }
    }

    //プレイ状態テーブルfilterNo書込
    fun putPlayStateTableFilterNo(context: Context, playerNo: Int, datasetNo: Int, filterNo: Int) {
        val db = AnswerDatabase.getInstance(context)
        val playStateDao = db.playStateDao()
        //他の項目を上書きしないよう一旦リードする
        val slt = playStateDao.getPlayState(playerNo, datasetNo)
        val playStateTable =
            PlayStateTable(
                playerNo,
                datasetNo,
                slt?.lineNo ?: 0,
                filterNo,
                slt?.selLineOffset ?: 0,
                slt?.finalSelLine ?: false
            )
        if (slt == null) {
            //追加
            playStateDao.insert(playStateTable)
        } else {
            //更新
            playStateDao.update(playStateTable)
        }
    }

    //プレイ状態テーブルselLineOffset書込
    fun putPlayStateTableSelLineOffset(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        selLineOffset: Int
    ) {
        val db = AnswerDatabase.getInstance(context)
        val playStateDao = db.playStateDao()
        //他の項目を上書きしないよう一旦リードする
        val slt = playStateDao.getPlayState(playerNo, datasetNo)
        val playStateTable =
            PlayStateTable(
                playerNo,
                datasetNo,
                slt?.lineNo ?: 0,
                slt?.filterNo ?: 0,
                selLineOffset,
                slt?.finalSelLine ?: false
            )
        if (slt == null) {
            //追加
            playStateDao.insert(playStateTable)
        } else {
            //更新
            playStateDao.update(playStateTable)
        }
    }

    //プレイ状態テーブルselLineOffset書込
    fun putPlayStateTableFinalSelLine(
        context: Context,
        playerNo: Int,
        datasetNo: Int,
        finalSelLine: Boolean
    ) {
        val db = AnswerDatabase.getInstance(context)
        val playStateDao = db.playStateDao()
        //他の項目を上書きしないよう一旦リードする
        val slt = playStateDao.getPlayState(playerNo, datasetNo)
        val playStateTable =
            PlayStateTable(
                playerNo,
                datasetNo,
                slt?.lineNo ?: 0,
                slt?.filterNo ?: 0,
                slt?.selLineOffset ?: 0,
                finalSelLine ?: false
            )
        if (slt == null) {
            //追加
            playStateDao.insert(playStateTable)
        } else {
            //更新
            playStateDao.update(playStateTable)
        }
    }

    //PlayerAnswer.dbから路線回答データmapを取得する
    //使用タイミング 路線選択画面起動時、地域選択時（フィルタ）、検索文字列入力時
    //順序は関係ないのでMapでok
    fun getLineAnswerMap(
        context: Context, playerNo: Int, datasetNo: Int
    ): MutableMap<Int, LineAnswerDc> {
        val db = AnswerDatabase.getInstance(context)
        val ansDao = db.answerDao()
        val minDao = db.recordDao()
        val ans = ansDao.getAnswer(playerNo, datasetNo)
        val min = minDao.getRecord(datasetNo)
        val ret: MutableMap<Int, LineAnswerDc> = mutableMapOf()
        //路線Noはans,minを合わせたものとする
        //ans基準の場合playerNoに該当するものだけが対象に
        //min基準の場合comp済みだけが対象になってしまうため
        val lineSet: MutableSet<Int> = mutableSetOf()
        val ansMap: MutableMap<Int, Int> = mutableMapOf()
        val minMap: MutableMap<Int, Int> = mutableMapOf()
        if (ans != null)
            for ((i, v) in ans.withIndex()) {
                lineSet += v.lineNo
                ansMap += v.lineNo to i
            }
        if (min != null)
            for ((i, v) in min.withIndex()) {
                lineSet += v.lineNo
                minMap += v.lineNo to i
            }
        for (lNo in lineSet) {
            val sta: MutableList<Int> = mutableListOf()
            var lct = Duration.ZERO
            var os = 0
            if (ansMap.isNotEmpty() && ansMap.containsKey(lNo)) {
                for (sNo in ans?.get(ansMap[lNo] ?: 0)?.answerList ?: mutableListOf())
                    sta += sNo
                lct = ans?.get(ansMap[lNo] ?: 0)?.lastCompTime ?: Duration.ZERO
                os = ans?.get(ansMap[lNo] ?: 0)?.offset ?: 0
            }
            var pNo = 0
            var pName = ""
            var mct = Duration.ZERO
            if (minMap.isNotEmpty() && minMap.containsKey(lNo)) {
                pNo = min?.get(minMap[lNo] ?: 0)?.playerNo ?: 0
                pName = min?.get(minMap[lNo] ?: 0)?.playerName ?: ""
                mct = min?.get(minMap[lNo] ?: 0)?.recordTime ?: Duration.ZERO
            }
            ret += lNo to LineAnswerDc(lNo, sta, lct, os, pNo, pName, mct)
        }


//        //val minMap: MutableMap<Int, MinCompTimeDc> = mutableMapOf()
//        //回答データ作成
//        if (min != null) {
//            for (v in min) {
//                if (ansMap.containsKey(v.lineNo)) {
//                    val sta: MutableList<Int> = mutableListOf()
//                    for (v2 in ans?.get(ansMap[v.lineNo] ?: 0)?.answerList ?: mutableListOf())
//                        sta += v2
//                    ret += v.lineNo to LineAnswerDc(
//                        v.lineNo,
//                        sta,
//                        ans?.get(ansMap[v.lineNo] ?: 0)?.lastCompTime ?: Duration.ZERO,
//                        v.playerNo,
//                        v.playerName,
//                        v.minCompTime,
//                        ans?.get(ansMap[v.lineNo] ?: 0)?.offset ?: 0
//                    )
//                } else {
//                    ret += v.lineNo to LineAnswerDc(
//                        v.lineNo,
//                        mutableListOf(),
//                        Duration.ZERO,
//                        v.playerNo,
//                        v.playerName,
//                        v.minCompTime,
//                        0
//                    )
//                }
//            }
//        }

//        if (ans != null)
//            for (v in ans) {
//                val sta: MutableList<Int> = mutableListOf()
//                for (v2 in v.answerList)
//                    sta += v2
//                ret += if (minMap.containsKey(v.lineNo))
//                    v.lineNo to LineAnswerDc(
//                        v.lineNo,
//                        sta,
//                        v.lastCompTime,
//                        minMap[v.lineNo]?.playerNo ?: 0,
//                        minMap[v.lineNo]?.playerName ?: "",
//                        minMap[v.lineNo]?.minCompTime ?: Duration.ZERO,
//                        v.offset
//                    )
//                else
//                    v.lineNo to LineAnswerDc(
//                        v.lineNo,
//                        sta,
//                        v.lastCompTime,
//                        0,
//                        "",
//                        Duration.ZERO,
//                        v.offset
//                    )
//            }
        return ret
    }

    //PlayerAnswer.dbから路線回答データを取得する
    fun getLineAnswer(
        context: Context, playerNo: Int, datasetNo: Int, lineNo: Int
    ): LineAnswerDc {
        val db = AnswerDatabase.getInstance(context)
        val ansDao = db.answerDao()
        val minDao = db.recordDao()
        val ans = ansDao.getAnswer(playerNo, datasetNo, lineNo)
        val min = minDao.getRecord(datasetNo, lineNo)

        val sta: MutableList<Int> = mutableListOf()
        if (ans != null)
            for (v in ans.answerList)
                sta += v

        if (ans != null && min != null) {
            return LineAnswerDc(
                lineNo,
                sta,
                ans.lastCompTime,
                ans.offset,
                min.playerNo,
                min.playerName,
                min.recordTime
            )
        } else if (ans != null && min == null) {
            return LineAnswerDc(
                lineNo,
                sta,
                ans.lastCompTime,
                ans.offset,
                0,
                "",
                Duration.ZERO
            )
        } else if (ans == null && min != null) {
            return LineAnswerDc(
                lineNo,
                sta,
                Duration.ZERO,
                0,
                min.playerNo,
                min.playerName,
                min.recordTime
            )
        } else {
            return LineAnswerDc()
        }
    }


//    fun testAnswerDao(context: Context): List<AnswerTable>? {
//
//        val database = AnswerDatabase.getInstance(context)
//        val answerDao = database.answerDao()
//        //追加
//        val answerList: MutableList<Int> = mutableListOf(7777701, 7777702, 0, 7777704)
//        val lastCompTime: Duration = Duration.parse("PT0H0M8S") //PnDTnHnMn.nS PT1H2M3.000000004S
//        val cnt = answerDao.insert(
//            AnswerTable(
//                1,
//                0,
//                77777,
//                answerList,
//                lastCompTime
//            )
//        )
////        val cnt = answerDao.update(
////            AnswerEntity.PlayerAnswer(
////                1,
////                2,
////                233,
////                answerList,
////                lastCompTime,
////                minCompTime
////            )
////        )
////        val cnt = answerDao.delete(
////            AnswerEntity.PlayerAnswer(
////                1,
////                2,
////                233,
////                answerList,
////                lastCompTime,
////                minCompTime
////            )
////        )
//        //一覧の取得
//        return answerDao.getAll()
//    }
//
//    fun testMinCompTimeDao(context: Context): List<RecordTable>? {
//
//        val database = AnswerDatabase.getInstance(context)
//        val minCompTimeDao = database.recordDao()
//        //追加
//        val cnt =
//            minCompTimeDao.insert(
//                RecordTable(
//                    0,
//                    77777,
//                    2,
//                    "siojyake007",
//                    Duration.parse("PT1H33M5S")
//                )
//            )
//        //一覧の取得
//        return minCompTimeDao.getAll()
//    }

}