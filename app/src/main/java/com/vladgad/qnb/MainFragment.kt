package com.vladgad.qnb

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vladgad.qnb.model.EmvCard

class MainFragment : Fragment(R.layout.frag_main) {
    private lateinit var dataList: ArrayList<DataQNB>
    private lateinit var lv: ListView
    private lateinit var statusText: TextView
    private lateinit var qnbAdapter: QnbAdapter

    //адаптер
    private class QnbAdapter(public val dataList: ArrayList<DataQNB>, context: Context) :
        BaseAdapter() {
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(p0: Int): Any {
            return dataList.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.main_item, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }
            // задание свойств
            // vh.holderName.text = listCard[position].holderName
            when(dataList[position].type){
                0 ->{
                    vh.mainItem.text = dataList[position].qrData.toString()
                }
                1 ->{
                    vh.mainItem.text = dataList[position].emvCard.toString()
                }
                2 ->{
                    vh.mainItem.text = dataList[position].scanRecord.toString()
                }
            }
            return view
        }

        private class ListRowHolder(row: View?) {
            //описание элемента
            public val mainItem: TextView
            init {
                this.mainItem = row?.findViewById(R.id.dataText) as TextView
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataList = AppSingleton.qnb
        init(view)
    }
    private fun init(view: View) {
        lv = view.findViewById(R.id.mainList) as ListView
        qnbAdapter = QnbAdapter(dataList, requireContext())
        lv.adapter = qnbAdapter
        statusText = view.findViewById(R.id.statusMain) as TextView

    }
}