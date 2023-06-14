package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val data: ArrayList<Course>) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    // 点击事件监听器接口
    interface OnItemClickListener {
        fun onItemClick(course: Course)
    }

    // 声明点击事件监听器变量
    private var onItemClickListener: OnItemClickListener? = null

    // 设置点击事件监听器
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    // 實作 RecyclerView.ViewHolder 來儲存 View
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        // 連結畫面中的元件
        val tv_courseNumber: TextView = v.findViewById(R.id.tv_courseNumber)
        val tv_courseName: TextView = v.findViewById(R.id.tv_courseName)
        val tv_teacher: TextView = v.findViewById(R.id.tv_teacher)
        val tv_recommend: TextView = v.findViewById(R.id.tv_recommend)

        init {
            // 设置点击事件监听器
            v.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            // 获取点击的项的位置
            val position = adapterPosition
            // 获取点击的课程对象
            val course = data[position]
            // 触发点击事件监听器的回调方法
            onItemClickListener?.onItemClick(course)
        }
    }

    // 回傳資料數量
    override fun getItemCount() = data.size

    // 建立 ViewHolder 與 Layout 並連結彼此
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_row, parent, false)
        return ViewHolder(v)
    }

    // 將資料指派給元件呈現
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_courseNumber.text = data[position].courseNumber
        holder.tv_courseName.text = data[position].courseName
        holder.tv_teacher.text = data[position].teacher
        holder.tv_recommend.text = data[position].recommend
    }
}
