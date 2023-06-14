package com.example.finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CourseActivity : AppCompatActivity() {
    private lateinit var adapter: TableAdapter
    private val course = ArrayList<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        // 获取传递的数据
        val text = intent.getStringExtra("text")
        val link = intent.getStringExtra("link")

        // 获取并设置 classNo TextView 的文本
        val classNoTextView = findViewById<TextView>(R.id.classNo)
        classNoTextView.text = text

        // 使用协程在后台线程执行网络请求
        GlobalScope.launch(Dispatchers.Main) {
            val document: Document = withContext(Dispatchers.IO) {
                Jsoup.connect(link).get()
            }
            val tableRows: Elements = document.select("tbody tr")

            // 从第三个 <tr> 开始读取
            for (i in 2 until tableRows.size) {
                val row: Element = tableRows[i]
                val tds: Elements = row.select("td")
                if (tds.size >= 8) {
                    val courseNumber: String = tds[0].text().trim()
                    val courseName: String = tds[1].select("a").text().trim()
                    val teacher: String = tds[6].select("a").text().trim()
                    val url: String = "https://aps.ntut.edu.tw/course/tw/" + tds[19].select("a").attr("href").trim()
                    if (courseName != "班週會及導師時間") {
                        val courseItem = Course(courseNumber, courseName, teacher, "", url)
                        course.add(courseItem)
                    }
                }
            }

            // 将数据设置给适配器
            adapter = TableAdapter(course)

            // 设置点击事件监听器
            adapter.setOnItemClickListener(object : TableAdapter.OnItemClickListener {
                override fun onItemClick(course: Course) {
                    // 打开课程的网页
                    openCourseWebsite(course.url)
                }
            })

            // 将适配器设置给 RecyclerView
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter

            // 设置布局管理器
            recyclerView.layoutManager = LinearLayoutManager(this@CourseActivity)
        }
    }

    // 打开课程的网页
    private fun openCourseWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "找不到可以处理链接的应用程序", Toast.LENGTH_SHORT).show()
        }
    }
}

data class Course(
    val courseNumber: String, // 課號
    val courseName: String, // 課名
    val teacher: String, // 教師
    val recommend: String, // 推薦色塊
    val url: String // 课程网页链接
)
