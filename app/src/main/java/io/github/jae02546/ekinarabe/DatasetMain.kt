package io.github.jae02546.ekinarabe

import android.util.Base64
import java.util.*
//import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DatasetMain {

    //gssデータクラス
    //GoogleSpreadSheetに入力されている元データに対応

    //gss路線データクラス
    //路線Noはユニークなこと
    //リリース後の路線No変更不可（ユーザ側保存データとの整合性のため）
    //路線選択での路線順は別リストなのでここでは路線NoをkeyとしたMutableMapとする
    //路線No,路線名,かな,ローマ字,補足,廃線,url,正解駅list<gss駅データ>
    data class GssLineDc(
        //val lineNo: Int, //路線Noはキーと被るので省略
        val name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        val url: String = "",
        val staList: MutableList<GssStaDc> = mutableListOf()
    )

    data class GssLineDc2(
        val lineNo: Int = 0, //路線Noはキーと被るので省略
        var name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        var url: String = "",
        val staList: MutableList<GssStaDc> = mutableListOf()
    )

    //gss駅データクラス
    //駅Noはユニークなこと
    //リリース後の駅No変更不可（ユーザ側保存データとの整合性のため）
    //元データとなるgss上の順序はゲームでの正解としたい駅順（駅No順ではない）
    //Datasetでは駅NoをkeyとしたMutableMapとしたいがMutableMapは順序保証が無いのでMutableListを使用
    //駅No,駅名,かな,ローマ字,補足,廃線,住所,url
    data class GssStaDc(
        val staNo: Int = 0,
        val name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        val address: String = "",
        val url: String = ""
    )

//    //gss路線データクラス
//    //路線Noはユニークなこと
//    //リリース後の路線No変更不可（ユーザ側保存データとの整合性のため）
//    //元データとなるgss上の順序は路線選択画面で表示したい順序
//    //Datasetでは路線NoをkeyとしたMutableMapとしたいがMutableMapは順序保証が無いのでMutableListを使用
//    //urlはwiki等の参照のため
//    //有効無効,路線No,路線名,かな,ローマ字,補足,廃線,フィルタlist<フィルタNo>,url,正解list<gss駅データ>
//    data class GssLineDc(
//        val enabled: Boolean,
//        val lineNo: Int,
//        val name: String,
//        val kana: String,
//        val rome: String,
//        val supplement: String,
//        val close: Boolean,
//        val filterList: MutableList<Int> = mutableListOf(),
//        val url: String,
//        val staList: MutableList<GssStaDc> = mutableListOf()
//    )
//
//    //gss駅データクラス
//    //駅Noはユニークなこと
//    //リリース後の駅No変更不可（ユーザ側保存データとの整合性のため）
//    //元データとなるgss上の順序はゲームでの正解としたい駅順
//    //Datasetでは駅NoをkeyとしたMutableMapとしたいがMutableMapは順序保証が無いのでMutableListを使用
//    //urlはwiki等の参照のため
//    //有効無効,駅No,駅名,かな,ローマ字,補足,廃線,住所,url
//    data class GssStaDc(
//        val enabled: Boolean,
//        val staNo: Int,
//        val name: String,
//        val kana: String,
//        val rome: String,
//        val supplement: String,
//        val close: Boolean,
//        val address: String,
//        val url: String
//    )

    //gameデータクラス
    //ゲーム内で使用するデータ

    //路線選択データクラス
    //路線選択画面で表示される路線データクラス
    //路線選択データでのMutableListはDatasetが表示したい順序となっているものとする
    //路線No,駅名,かな,ローマ字,補足,廃線,url,正解List<駅No>
    data class SelLineDc(
        val lineNo: Int = 0,
        val name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        val url: String = "",
        val staNoList: MutableList<Int> = mutableListOf()
    )

    //路線データクラス
    //メイン画面で表示される路線データクラス
    //路線No,路線名,かな,ローマ字,補足,廃線,url,正解List<駅データ>
    data class LineDc(
        val lineNo: Int = 0,
        val name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        val url: String = "",
        val staList: MutableList<StaDc> = mutableListOf()
    )

    //駅データクラス
    //メイン画面で表示される元となる駅データクラス
    //駅No,駅名,かな,ローマ字,補足,廃駅,住所,url,正解No(0-n),comp
    data class StaDc(
        val staNo: Int = 0,
        val name: String = "",
        val kana: String = "",
        val rome: String = "",
        val supplement: String = "",
        val close: Boolean = false,
        val address: String = "",
        val url: String = "",
        val correctNo: Int = 0, //Adapter引継用（駅毎の赤丸表示）
        val comp: Boolean = false //Adapter引継用（comp時ハンドル操作無効）
    )

    //路線データセット取得
    private fun getDatasetMap(datasetNo: Int): MutableMap<Int, GssLineDc> {
        //データセットが増えた場合は追加
        //var ds: MutableList<GssLineDc> = mutableListOf()
        var ds: MutableMap<Int, GssLineDc> = mutableMapOf()
        when (datasetNo) {
            0 -> {
                ds = Dataset000.lineList

//                for ((k, v) in Dataset0000a.lineList)
//                    ds += k to v
//                for ((k, v) in Dataset0000b.lineList)
//                    ds += k to v
//                for ((k, v) in Dataset0000c.lineList)
//                    ds += k to v
            }
            1 -> {
                ds = Dataset0010.lineList
            }
            2 -> {
                ds = Dataset0020.lineList
            }
            3 -> {
                //debug
                ds = Dataset9990.lineList
            }
            999 -> {
                //999はdebug用
                ds = Dataset9990.lineList
            }
        }
        return ds
    }

    //路線選択リスト取得
    fun getSelLineList(
        datasetNo: Int,
        filterNo: Int = 0,
        search: String? = ""
    ): MutableList<SelLineDc> {
        //ggs路線選択データセット
        val dsSelLineMap = DatasetSelLine.selLineMap
        //ggs路線データセット
        val dsLineMap = getDatasetMap(datasetNo)
        //条件による抽出後の路線選択リスト
        val selLineList: MutableList<SelLineDc> = mutableListOf()

        if (dsSelLineMap.containsKey(filterNo)) {
            for (v in dsSelLineMap[filterNo] ?: mutableListOf()) {
                if (dsLineMap.containsKey(v)) {
                    val foo = dsLineMap[v] ?: GssLineDc()
                    if (search != null) {
                        if (search == ""
                            || Regex(search.lowercase()).containsMatchIn(foo.name.lowercase())
                            || Regex(search.lowercase()).containsMatchIn(foo.kana.lowercase())
                            || Regex(search.lowercase()).containsMatchIn(foo.rome.lowercase())
                        ) {
                            val sta: MutableList<Int> = mutableListOf()
                            for (v2 in foo.staList) {
                                sta += v2.staNo
                            }
                            selLineList += SelLineDc(
                                v,
                                foo.name,
                                foo.kana,
                                foo.rome,
                                foo.supplement,
                                foo.close,
                                foo.url,
                                sta
                            )
                        }
                    }
                }
            }
        }

        return selLineList
    }

    //路線データ取得
    fun getLine(datasetNo: Int, lineNo: Int): LineDc {
        //ggs路線データセット
        val dsLineMap = getDatasetMap(datasetNo)

        var line = LineDc()
        if (dsLineMap.containsKey(lineNo)) {
            val v = dsLineMap[lineNo]
            val staDc: MutableList<StaDc> = mutableListOf()
            if (v != null) {
                for ((i, v2) in v.staList.withIndex()) {
                    staDc += StaDc(
                        v2.staNo,
                        v2.name,
                        v2.kana,
                        v2.rome,
                        v2.supplement,
                        v2.close,
                        v2.address,
                        v2.url,
                        i,
                        false //ここでは未使用
                    )
                }
                line = LineDc(lineNo, v.name, v.kana, v.rome, v.supplement, v.close, v.url, staDc)
            }
        }

        return line
    }

    //路線データ駅No取得
    fun getLineStaNo(datasetNo: Int, lineNo: Int): MutableList<Int> {
        //ggs路線データセット
        val dsLineMap = getDatasetMap(datasetNo)

        val staNo: MutableList<Int> = mutableListOf()
        if (dsLineMap.containsKey(lineNo)) {
            val v = dsLineMap[lineNo]
            if (v != null) {
                for (v2 in v.staList)
                    staNo += v2.staNo
            }
        }

        return staNo
    }

//    fun aaa()
//    {
//        var foo: String ="abc".encrypt()
//        var bar: String = "abc".decrypt()
//    }

    fun String.encrypt(initializationKey: String, secretKey: String): String {
        //それぞれ16文字にする
        //val initializationKey = "ABCDEFGHIJKLMNOP"
        //val secretKey = "abcdefghijklmnop"
        val iv = IvParameterSpec(initializationKey.toByteArray())
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)

        val byteResult = cipher.doFinal(this.toByteArray())

        return Base64.encodeToString(byteResult, Base64.NO_WRAP)
    }

    fun String.decrypt(initializationKey: String, secretKey: String): String {
        //それぞれ16文字にする
        //val initializationKey = "abcdefghijklmnop"
        //val secretKey = "ABCDEFGHIJKLMNOP"
        val iv = IvParameterSpec(initializationKey.toByteArray())
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        val byteResult = cipher.doFinal(Base64.decode(this, Base64.NO_WRAP))

        return String(byteResult)
    }


//    fun String.decrypt(): String {
//        //それぞれ16文字にする
////        val secretKey = "abcdefghijklmnop"
////        val initializationKey = "ABCDEFGHIJKLMNOP"
//        val secretKey = "ABCDEFGHIJKLMNOP"
//        val initializationKey = "abcdefghijklmnop"
//        val key = SecretKeySpec(secretKey.toByteArray(), "AES")
//        val iv = IvParameterSpec(initializationKey.toByteArray())
//
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        cipher.init(Cipher.DECRYPT_MODE, key, iv)
//
//        //var foo = Base64.decode(this, Base64.NO_WRAP)
//
////        val b64Decode =
////            Base64.getDecoder().decode(this.toByteArray()).toString(Charsets.UTF_8)
//        val b64Decode = Base64.getDecoder().decode(this.toByteArray())
//
//        val byteResult = cipher.doFinal(b64Decode)
////        val byteResult = cipher.doFinal(Base64.decode(this, Base64.NO_WRAP))
//
//        return String(byteResult)
//    }


}