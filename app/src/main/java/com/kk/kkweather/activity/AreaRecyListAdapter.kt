package com.kk.kkweather.activity

import android.content.Context
import android.view.LayoutInflater
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.kk.kkweather.R
import com.kk.kkweather.util.LogUtil
import kotlinx.android.synthetic.main.area_item.view.*
import java.security.AccessController.getContext

/**
 * Created by xxnfd on 25/03/2018.
 */
class AreaRecyListAdapter(areaAllList:MutableList<AreaItem>, inctx: Context?) : RecyclerView.Adapter<AreaRecyListAdapter.AreaViewHolder>(), View.OnClickListener{

    var areaList = areaAllList
    var ctx = inctx //初始化的时候有可能是null
    private var mAreaItemClickListener: OnAreaItemClickListener? = null

    init {
        LogUtil.i("KW-AreaRecyListAdapter", "AreaRecyListAdapter onCreate")
    }


    fun setOnAreaItemClickListener(listener: OnAreaItemClickListener) {
        mAreaItemClickListener = listener
    }

    override fun onClick(v: View) {
        if (mAreaItemClickListener != null) {
            mAreaItemClickListener!!.onAreaItemClick(v, v.tag as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        //在这里进行更新cnx，必须更新
//        ctx = getContext()

        val v: View = LayoutInflater.from(ctx).inflate(R.layout.area_item, parent, false)
        val vh = AreaViewHolder(v)
//        vh.setIsRecyclable(false)
//        viewType可以实现比较花哨的UI

        LogUtil.i("CreateViewHolder", "itemNmae:${vh.itemName}")

        v.setOnClickListener(this)
        return vh
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {

        val areaItem = areaList[position]

        holder.itemImage.setImageResource(areaItem.imageId)
        holder.itemName.text = areaItem.name
        holder.itemView.tag = position  //把位置信息进行保存

        LogUtil.i("BindViewHolder", "itemNmae:${areaItem.name}" + "      position: ${position}"+"      ${areaItem.name}")
    }

    override fun getItemCount(): Int = areaList.size

    class AreaViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val itemImage = v.area_item_image
        val itemName = v.area_item_name
    }
}