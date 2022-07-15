package io.github.jae02546.ekinarabe

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration


/**
 * Adapter の実装（データを結びつけ、ViewHolder の生成とデータ反映を行う）
 */
class SelectLineAdapter(
    private val data: MutableList<DatasetMain.SelLineDc>,
    private val ans: MutableMap<Int, AnswerMain.LineAnswerDc>
) :
    RecyclerView.Adapter<SelectLineHolder>() {
    /** 表示用データの要素数（ここでは IntArray のサイズ） */
    override fun getItemCount(): Int = data.size

    /** 新しく ViewHolder オブジェクトを生成するための実装 */
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectLineHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        return SelectLineHolder(inflater.inflate(R.layout.activity_selectlineitem2, parent, false))
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectLineHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.activity_selectline_item, parent, false)
        val viewHolder = SelectLineHolder(listItem)
        viewHolder.run {
            init.setOnClickListener {
                //Toast.makeText(init.context, kana.text, Toast.LENGTH_SHORT).show()
                initClickListener?.onItemClick(viewHolder)
            }
            cont.setOnClickListener {
                contClickListener?.onItemClick(viewHolder)
                //Toast.makeText(cont.context, name.text, Toast.LENGTH_SHORT).show()
            }


            name.setOnClickListener {
                nameClickListener?.onItemClick(viewHolder)
                //Toast.makeText(cont.context, name.text, Toast.LENGTH_SHORT).show()
            }


        }
        return viewHolder
    }

    /** position の位置のデータを使って、表示内容を適切に設定（更新）する */
    override fun onBindViewHolder(holder: SelectLineHolder, position: Int) {
        val ds = data[position]
        holder.lineNo = ds.lineNo //引継ぎ用
        //駅数が1以上でcompしている場合はcomp
        if (ds.staNoList.size >= 1 && ds.staNoList == ans[ds.lineNo]?.answerList ?: AnswerMain.LineAnswerDc()) {
            holder.comp = true
            holder.cont.text = "コンプ済"
        } else {
            holder.comp = false
            holder.cont.text = "続きから"
        }

        if (ds.url != "") {
//            holder.hint.text =
//                HtmlCompat.fromHtml("<a href=" + ds.url + ">Wikipedia</a>", FROM_HTML_MODE_COMPACT)
            holder.hint.text =
                HtmlCompat.fromHtml(
                    "<a href=" + ds.url + ">" + holder.hint.text + "</a>",
                    FROM_HTML_MODE_COMPACT
                )
            holder.hint.movementMethod = LinkMovementMethod.getInstance();
        } else
            holder.hint.text = ""
        holder.name.text = ds.name
        holder.kana.text = ds.kana
        holder.rome.text = ds.rome
        //回答状況
        if (ans.containsKey(ds.lineNo)) {
            //回答有り
            val lineStatus = Tools.getLineStatus(ds, ans[ds.lineNo] ?: AnswerMain.LineAnswerDc())
            if (lineStatus[0] == "") {
                holder.supplement.visibility = View.GONE
                holder.supplement.text = ""
            } else {
                holder.supplement.visibility = View.VISIBLE
                holder.supplement.text = HtmlCompat.fromHtml(lineStatus[0], FROM_HTML_MODE_COMPACT)
            }
            holder.answer.text = HtmlCompat.fromHtml(lineStatus[2], FROM_HTML_MODE_COMPACT)
            holder.record.text = HtmlCompat.fromHtml(lineStatus[3], FROM_HTML_MODE_COMPACT)
            //回答有りでlastCompTimeが0以外なら初回では無い
            if (ans[ds.lineNo]?.lastCompTime ?: Duration.ZERO != Duration.ZERO) {
                holder.init.isEnabled = true
                holder.cont.isEnabled = true
            } else {
                holder.init.isEnabled = true
                holder.cont.isEnabled = false
            }
        } else {
            //回答無し
            val lineStatus = Tools.getLineStatus(
                ds,
                AnswerMain.LineAnswerDc(0, mutableListOf(), Duration.ZERO, 0, 0, "", Duration.ZERO)
            )
            if (lineStatus[0] == "") {
                holder.supplement.visibility = View.GONE
                holder.supplement.text = ""
            } else {
                holder.supplement.visibility = View.VISIBLE
                holder.supplement.text = HtmlCompat.fromHtml(lineStatus[0], FROM_HTML_MODE_COMPACT)
            }
            holder.answer.text = HtmlCompat.fromHtml(lineStatus[2], FROM_HTML_MODE_COMPACT)
            holder.record.text = HtmlCompat.fromHtml(lineStatus[3], FROM_HTML_MODE_COMPACT)
            holder.init.isEnabled = true
            holder.cont.isEnabled = false
        }
    }

    //最初からクリックリスナー
    var initClickListener: OnInitClickListener? = null

    interface OnInitClickListener {
        fun onItemClick(holder: SelectLineHolder)
    }

    //続きからクリックリスナー
    var contClickListener: OnContClickListener? = null

    interface OnContClickListener {
        fun onItemClick(holder: SelectLineHolder)
    }


    var nameClickListener: OnNameClickListener? = null

    interface OnNameClickListener {
        fun onItemClick(holder: SelectLineHolder)
    }


}


