package io.github.jae02546.ekinarabe

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectLineHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var lineNo: Int = 0 //値引継ぎ用
    var comp: Boolean = false //値引継ぎ用

    val hint: TextView by lazy {
        view.findViewById(R.id.selLineHint)
    }
    val record: TextView by lazy {
        view.findViewById(R.id.selLineRecord)
    }
    val name: TextView by lazy {
        view.findViewById(R.id.selLineName)
    }
    val kana: TextView by lazy {
        view.findViewById(R.id.selLineKana)
    }
    val rome: TextView by lazy {
        view.findViewById(R.id.selLineRome)
    }
    val supplement: TextView by lazy {
        view.findViewById(R.id.selLineSupplement)
    }
    val answer: TextView by lazy {
        view.findViewById(R.id.selLineAnswer)
    }
    val init: Button by lazy {
        view.findViewById(R.id.selLineInit)
    }
    val cont: Button by lazy {
        view.findViewById(R.id.selLineCont)
    }
}
