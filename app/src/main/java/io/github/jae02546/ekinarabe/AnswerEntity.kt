package io.github.jae02546.ekinarabe

import androidx.room.Entity
import java.time.Duration
import java.util.logging.Filter

//PlayerAnswer.db プレーヤ回答テーブル
//プレーヤ、データセット、路線毎に駅順の回答と最終のcomp時間
//また次回起動時に前回の表示状態を戻すためにoffset値を持つ
//プレーヤNo,データセットNo,路線No,回答リスト<駅No>,最終comp時間,offset
@Entity(primaryKeys = ["playerNo", "datasetNo", "lineNo"])
data class AnswerTable(
    val playerNo: Int = 0,
    val datasetNo: Int = 0,
    val lineNo: Int = 0,
    val answerList: List<Int> = listOf(),
//    val answerList: MutableList<Int> = mutableListOf(),
    val lastCompTime: Duration = Duration.ZERO,
    val offset: Int = 0
)

//PlayerAnswer.db 最速comp記録テーブル
//データセット、路線毎にcompまでの最速時間を保存するテーブル
//プレーヤNoとプレーヤ名を持つ、本来どちらかでよいがプレイ途中での
//プレーヤ名変更を考えるとcomp時点の名前の表示がよいかもしれない...
//データセットNo,路線No,プレーヤNo,プレーヤ名,最速comp時間
@Entity(primaryKeys = ["datasetNo", "lineNo"])
data class RecordTable(
    val datasetNo: Int = 0,
    val lineNo: Int = 0,
    val playerNo: Int = 0,
    val playerName: String = "",
    val recordTime: Duration = Duration.ZERO
)

//PlayerAnswer.db プレイ状態テーブル
//プレーヤ、データセット毎に前回終了時のプレイ状態を保存するテーブル
//プレーヤNo,データセットNo,路線No,フィルタNo,路線選択offset,終了時路線選択画面フラグ（true 路線選択画面で終了）
@Entity(primaryKeys = ["playerNo", "datasetNo"])
data class PlayStateTable(
    val playerNo: Int = 0,
    val datasetNo: Int = 0,
    val lineNo: Int = 0,
    val filterNo: Int = 0,
    val selLineOffset: Int = 0,
    val finalSelLine: Boolean
)
