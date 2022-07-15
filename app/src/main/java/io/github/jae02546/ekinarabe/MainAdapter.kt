package io.github.jae02546.ekinarabe

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(
    private val data: MutableList<DatasetMain.StaDc>,
) :
    RecyclerView.Adapter<MainHolder>() {

    override fun getItemCount(): Int = data.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.activity_main_item, parent, false)
        val viewHolder = MainHolder(listItem)
        viewHolder.run {

            //ここだと頻度は少なくなるがやはり起きる、テキストでも発生する場合あり
            viewHolder.setIsRecyclable(false);

//            //itemがタッチされたら発生
//            mainLayout.setOnTouchListener { v, event ->
//                //Toast.makeText(init.context, kana.text, Toast.LENGTH_SHORT).show()
//                itemClickListener?.onItemClick(viewHolder)
////                //こちらもACTION_DOWN以外はまともに使えない?
////                if(event.actionMasked== MotionEvent.ACTION_UP){
////                }
////                Log.d("あいうえお Handle.setOnTouchListener", event.actionMasked.toString())
//                return@setOnTouchListener true
//            }

            // 1. handleView に `OnTouchListener` を実装。
            viewHolder.handle.setOnTouchListener { view, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    // 2. タッチダウンを検出したら、先ほど用意した `startDragging(...)` を呼びます。
                    val ma = parent.context as MainActivity
                    ma.startDragging(viewHolder)
                }
                //ACTION_DOWN以外はまともに使えない?
                //ほぼcancelで終了するのでupとcancelとする
                //upするまえにcancelが発生する、またこれだとhandlr以外は発生しないわ
                //if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                //    val ma = parent.context as MainActivity
                //    //ma.lineSwitcher()
                //}
                return@setOnTouchListener true
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        //これが無いとRecyclerViewのリサイクルで他のitemのハイライトが残ってしまう
        //しかしこれだとスクロールで見えなくなってしまうと再表示でハイライトが解除されてしまう
        //holder.itemView.alpha = 1.0f
        //これもうまくいかないね...やっぱりやめる...やめない
        holder.setIsRecyclable(false)

        val sta = data[position]
        holder.staNo = sta.staNo //引継ぎ用
        if (sta.url != "") {
//            holder.hint.text =
//                HtmlCompat.fromHtml(
//                    "<a href=" + sta.url + ">Wikipedia</a>",
//                    HtmlCompat.FROM_HTML_MODE_COMPACT
//                )
            holder.hint.text =
                HtmlCompat.fromHtml(
                    "<a href=" + sta.url + ">" + holder.hint.text + "</a>",
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            holder.hint.movementMethod = LinkMovementMethod.getInstance();
        } else
            holder.hint.text = ""

        var info = if (sta.close)
            "廃駅<br>"
        else
            "<br>"
        //住所から都道府県名部分を検索し表示
        val ken = listOf("県", "府", "都", "道")
        for (v in ken) {
            val pos = sta.address.indexOf(v)
            if (pos in 2..3) {
                info += sta.address.substring(0, pos + 1)
                break
            }
        }

        //Log.d("abc", sta.name + " -> " + sta.address + " -> " + info)


        holder.info.text = HtmlCompat.fromHtml(info, HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.name.text = sta.name
        holder.kana.text = sta.kana
        holder.rome.text = sta.rome

        if (position == sta.correctNo) {
            //テキストは大丈夫な感じ、いやテキストでも発生
            //holder.mark.text = "〇"
            holder.mark.setTextColor(Color.RED)
            //背景画像でもはやり発生
            //holder.infoFrame.setBackgroundResource(R.drawable.ic_launcher_foreground)
        }

        //comp済ならfalseとして移動できないようにする
        if (sta.comp)
            holder.handle.visibility = View.INVISIBLE
        //holder.handle.isEnabled = false


//        //なぜか3以外の部分も赤く表示される場合がある?
//        if (position == 3) {
////            holder.mainLayout.alpha = 0.1f
////            holder.mainLayout.setBackgroundColor(Color.parseColor("#ff0000"))
//            holder.name.setBackgroundColor(Color.parseColor("#ff0000"))
//            Log.d("bind2", position.toString() + " " + sta.name)
//        }


    }

//    //クリックリスナー
//    var itemClickListener: OnItemClickListener? = null
//
//    interface OnItemClickListener {
//        fun onItemClick(holder: MainHolder)
//    }
//
//
//    //以下item移動イベント関連
//
//    private var miListener: MiInterface? = null
//
//    fun setMiListener(miListener: MiInterface) {
//        this.miListener = miListener
//    }
//
//    fun moveItem(from: Int, to: Int) {
//        if (from != to)
//            miListener?.onMoveItem(from, to)
//    }
//
//    interface MiInterface {
//        //fun onSuccess(result: Int) {}
//        //fun onDivZero() {}
//        fun onMoveItem(from: Int, to: Int) {}
//    }


}




