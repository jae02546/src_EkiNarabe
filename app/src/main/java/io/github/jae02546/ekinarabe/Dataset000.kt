package io.github.jae02546.ekinarabe

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import io.github.jae02546.ekinarabe.DatasetMain.decrypt
import io.github.jae02546.ekinarabe.DatasetMain.encrypt

//2022-05-29 21:20:23 駅並べ路線データジェネレータ ver.2.0.0.11 (c) 2022 jae02546@gmail.com
object Dataset000 {

    var lineList: MutableMap<Int, DatasetMain.GssLineDc> = mutableMapOf()

    init {

        val iv = "RUtJTkFSQUJFMDAx"
        val key = "ZWtpbmFyYWJlMDAx"
        val listType: Type = object : TypeToken<MutableMap<Int, DatasetMain.GssLineDc>>() { }.type
        lineList = Gson().fromJson(b64str.decrypt(iv, key), listType)

        //Base64のみの場合
        //val b64Decode =
        //    Base64.getDecoder().decode(b64str.toByteArray()).toString(Charsets.UTF_8)
        //val listType: Type = object : TypeToken<MutableMap<Int, DatasetMain.GssLineDc>>() {}.type
        //lineList = Gson().fromJson(b64Decode, listType)
    }


}