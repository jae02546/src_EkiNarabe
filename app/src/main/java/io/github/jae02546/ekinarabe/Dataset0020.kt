package io.github.jae02546.ekinarabe

import io.github.jae02546.ekinarabe.DatasetMain.GssLineDc
import io.github.jae02546.ekinarabe.DatasetMain.GssStaDc

object Dataset0020 {
    //GssLineDc:路線No,路線名,かな,ローマ字,補足,廃線,url,正解駅list<gss駅データ>
    //GssStaDc:駅No,駅名,かな,ローマ字,補足,廃線,住所,url
    val lineList: MutableMap<Int, GssLineDc> = mutableMapOf(
        34080 to GssLineDc(
            "東急こどもの国線",
            "とうきゅうこどものくにんせん",
            "Tokyu Kodomonokuni Line",
            "",
            false,
            "https://ja.m.wikipedia.org/wiki/%E6%9D%B1%E6%80%A5%E3%81%93%E3%81%A9%E3%82%82%E3%81%AE%E5%9B%BD%E7%B7%9A",
            mutableListOf(
                GssStaDc(1, "長津田", "ながつた", "Nagatsuta", "", false, "", ""),
                GssStaDc(2, "恩田", "おんだ", "Onda", "", false, "", ""),
                GssStaDc(3, "こどもの国", "こどものくに", "Kodomonokuni", "", false, "", ""),
            )
        )
    )


}
