package io.github.jae02546.ekinarabe

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class MainHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var staNo: Int = 0

    val mainLayout: ConstraintLayout by lazy {
        view.findViewById(R.id.staLayout)
    }
    val infoFrame: ConstraintLayout by lazy {
        view.findViewById(R.id.staInfoFrame)
    }
    val mark: TextView by lazy {
        view.findViewById(R.id.staMark)
    }
    val hint: TextView by lazy {
        view.findViewById(R.id.staHint)
    }
    val info: TextView by lazy {
        view.findViewById(R.id.staInfo)
    }
    val name: TextView by lazy {
        view.findViewById(R.id.staName)
    }
    val kana: TextView by lazy {
        view.findViewById(R.id.staKana)
    }
    val rome: TextView by lazy {
        view.findViewById(R.id.staRome)
    }
    val handle: androidx.appcompat.widget.AppCompatImageButton by lazy {
        view.findViewById(R.id.staHandle)
    }
}

