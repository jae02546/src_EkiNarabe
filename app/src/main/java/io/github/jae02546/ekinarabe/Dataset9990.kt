package io.github.jae02546.ekinarabe

import io.github.jae02546.ekinarabe.DatasetMain.GssLineDc
import io.github.jae02546.ekinarabe.DatasetMain.GssStaDc

object Dataset9990 {
    //このデータセットはdebug用

    //GssLineDc:路線No,有効無効,フィルタリスト<フィルタNo>,路線名,かな,ローマ字,正解順駅リスト<駅No,gss駅データ>
    //GssStaDc :駅No,有効無効,駅名,かな,ローマ字
    val lineList: MutableMap<Int, GssLineDc> = mutableMapOf()

    init {
        for (i in 1..100) {
            val sta: MutableList<GssStaDc> = mutableListOf()
            for (j in 1..20) {
                val staNo = i * 10 + j
                sta += GssStaDc(
                    staNo,
                    "駅$staNo",
                    "えき$staNo",
                    "station$staNo",
                    "",
                    false,
                    "",
                    ""
                )
            }
            lineList += i to GssLineDc(
                "路線$i", "ろせん$i", "line$i", "", false, "", sta
            )
        }
    }


}
