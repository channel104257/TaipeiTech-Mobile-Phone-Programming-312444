package com.example.finalproject

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val extractedTextList = ArrayList<LinkData>()
    private val unitList = ArrayList<LinkData>()
    private lateinit var spinnerDepartment: Spinner
    private lateinit var spinnerUnit: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerDepartment = findViewById(R.id.spinner_department)
        spinnerUnit = findViewById(R.id.spinner_unit)

        fetchAndParseHTML()

        spinnerDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDepartment = parent?.getItemAtPosition(position) as LinkData
                val url = "https://aps.ntut.edu.tw/course/tw/" + selectedDepartment.link
                fetchUnitData(url)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 当没有选择项时的处理
            }
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val selectedUnit = spinnerUnit.selectedItem as LinkData
            val text = selectedUnit.text
            val link = "https://aps.ntut.edu.tw/course/tw/" + selectedUnit.link

            // 创建 Intent 对象并将数据添加为 Extra
            val intent = Intent(this, CourseActivity::class.java)
            intent.putExtra("text", text)
            intent.putExtra("link", link)

            // 启动新的 Activity
            startActivity(intent)
        }
    }

    // 在需要的位置调用这个方法来执行网络请求和HTML解析
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAndParseHTML() {
        val currentYear = LocalDate.now().year - 1911
        HTMLParsingTask().execute("https://aps.ntut.edu.tw/course/tw/Subj.jsp?format=-2&year=" + currentYear + "&sem=1")
    }

    // 异步任务来执行网络请求和HTML解析
    private inner class HTMLParsingTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            val url = params[0]

            try {
                val document: Document = Jsoup.connect(url).get()
                val tableCells: Elements = document.select("table td a[href^=Subj.jsp]")

                for (cell: Element in tableCells) {
                    val text: String = cell.text()
                    val link: String = cell.attr("href")
                    val linkData = LinkData(text, link)
                    extractedTextList.add(linkData)
                    Log.d(TAG, "Extracted text: $text")
                    Log.d(TAG, "Extracted link: $link")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, extractedTextList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDepartment.adapter = adapter
        }
    }

    private fun fetchUnitData(url: String) {
        UnitDataParsingTask().execute(url)
    }

    // 异步任务来执行网络请求和HTML解析，用于获取单位数据
    private inner class UnitDataParsingTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            val url = params[0]

            try {
                val document: Document = Jsoup.connect(url).get()
                val unitElements: Elements = document.select("p:has(img[src='../image/or_ball.gif']) a[href^=Subj.jsp]")

                unitList.clear()

                for (unitElement: Element in unitElements) {
                    val unitText: String = unitElement.text()
                    val unitLink: String = unitElement.attr("href")
                    val unitData = LinkData(unitText, unitLink)
                    unitList.add(unitData)
                    Log.d(TAG, "Extracted unit text: $unitText")
                    Log.d(TAG, "Extracted unit link: $unitLink")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, unitList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnit.adapter = adapter
        }
    }

    data class LinkData(val text: String, val link: String) {
        override fun toString(): String {
            return text
        }
    }
}
