package io.github.jae02546.ekinarabe


import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


object Dataset0010 {

    var lineList: MutableMap<Int, DatasetMain.GssLineDc> = mutableMapOf()

    init {

        val foo: MutableMap<Int, DatasetMain.GssLineDc> = mutableMapOf(
            10 to DatasetMain.GssLineDc(
                "北海道新幹線",
                "ほっかいどうしんかんせん",
                "Hokkaido Shinkansen",
                "",
                false,
                "https://ja.wikipedia.org/wiki/%E5%8C%97%E6%B5%B7%E9%81%93%E6%96%B0%E5%B9%B9%E7%B7%9A",
                mutableListOf(
                    DatasetMain.GssStaDc(
                        10,
                        "新青森",
                        "しんあおもり",
                        "Shin-Aomori",
                        "",
                        false,
                        "青森県",
                        "https://ja.wikipedia.org/wiki/%E6%96%B0%E9%9D%92%E6%A3%AE%E9%A7%85"
                    ),
                    DatasetMain.GssStaDc(
                        20,
                        "奥津軽いまべつ",
                        "おくつがるいまべつ",
                        "Okutsugaru-Imabetsu",
                        "",
                        false,
                        "青森県",
                        "https://ja.wikipedia.org/wiki/%E5%A5%A5%E6%B4%A5%E8%BB%BD%E3%81%84%E3%81%BE%E3%81%B9%E3%81%A4%E9%A7%85"
                    ),
                    DatasetMain.GssStaDc(
                        30,
                        "木古内",
                        "きこない",
                        "Kikonai",
                        "",
                        false,
                        "北海道",
                        "https://ja.wikipedia.org/wiki/%E6%9C%A8%E5%8F%A4%E5%86%85%E9%A7%85"
                    ),
                    DatasetMain.GssStaDc(
                        40,
                        "新函館北斗",
                        "しんはこだてほくと",
                        "Shin-Hakodate-Hokuto",
                        "",
                        false,
                        "北海道",
                        "https://ja.wikipedia.org/wiki/%E6%96%B0%E5%87%BD%E9%A4%A8%E5%8C%97%E6%96%97%E9%A7%85"
                    ),
                )
            ),
            34080 to DatasetMain.GssLineDc(
                "東急こどもの国線",
                "とうきゅうこどものくにんせん",
                "Tokyu Kodomonokuni Line",
                "",
                false,
                "https://ja.m.wikipedia.org/wiki/%E6%9D%B1%E6%80%A5%E3%81%93%E3%81%A9%E3%82%82%E3%81%AE%E5%9B%BD%E7%B7%9A",
                mutableListOf(
                    DatasetMain.GssStaDc(1, "長津田", "ながつた", "Nagatsuta", "", false, "", ""),
                    DatasetMain.GssStaDc(2, "恩田", "おんだ", "Onda", "", false, "", ""),
                    DatasetMain.GssStaDc(3, "こどもの国", "こどものくに", "Kodomonokuni", "", false, "", ""),
                )
            )
        )

        val json: String = Gson().toJson(foo)
        Log.d("json", json)

        val b64Encode: String = Base64.getEncoder().encodeToString(json.toByteArray())
        Log.d("b64Encode", b64Encode)

        val b64Decode = Base64.getDecoder().decode(b64Encode.toByteArray()).toString(Charsets.UTF_8)
        Log.d("b64Decode", b64Decode)

        val listType: Type = object : TypeToken<MutableMap<Int, DatasetMain.GssLineDc>>() {}.type
        lineList = Gson().fromJson(b64Decode, listType)

        lineList += mutableListOf(99999 to DatasetMain.GssLineDc())

        val json2: String = Gson().toJson(lineList)
        Log.d("json2", json2)



    }


}

