package com.pdf.modules.pdf


import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pdf.R
import com.pdf.modules.pdf.model.Item
import com.pdf.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PdfViewModel : ViewModel() {
    var pdfDataList = MutableLiveData<ArrayList<Item>>()
    var list = arrayListOf<Item>()
    var path = Environment.getExternalStorageDirectory().path + "/Download/Invoice.pdf"
    var file = File(path)

    fun isUserValid(name: String, contact: String): Int {
        return if (TextUtils.isEmpty(name))
            1
        else if (TextUtils.isEmpty(contact))
            2
        else
            0
    }

    fun addItemToList(item: Item) {
        list.add(item)
        pdfDataList.value = list
    }

    fun generatePdf(con: Context, customerName: String, contact: String) {
        if (list.size == 0) {
            Utils(con).showToast("Add at-least one item")
            return
        }
        var pdfDocument = PdfDocument()
        val pageHeight = 1120
        val pagewidth = 792


        var bmp = BitmapFactory.decodeResource(con.resources, R.drawable.top_bg);
        var scaledbmp = Bitmap.createScaledBitmap(bmp, 665, 140, false);

        val paint = Paint()
        val title = Paint()
        val tableItem = Paint()


        val mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create()

        val myPage: PdfDocument.Page = pdfDocument.startPage(mypageInfo)

        val canvas: Canvas = myPage.canvas

        canvas.drawBitmap(scaledbmp, 56f, 40f, paint)

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        title.setTextSize(19f)
        title.setTextAlign(Paint.Align.CENTER)
        title.setColor(ContextCompat.getColor(con, R.color.purple_200))
        canvas.drawText("Customer Name: " + customerName, 350f, 200f, title)
        canvas.drawText("Contact No.: " + contact, 355f, 220f, title)

        tableItem.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        tableItem.setColor(ContextCompat.getColor(con, R.color.purple_200))
        tableItem.setTextSize(15f)
        tableItem.setTextAlign(Paint.Align.CENTER)
        canvas.drawText("Invoice Detail", 340f, 275f, title)

        //product tab header
        canvas.drawText("Product", 90f, 300f, title)
        canvas.drawText("Quantity", 230f, 300f, title)
        canvas.drawText("Price", 440f, 300f, title)
        canvas.drawText("Total", 650f, 300f, title)
        var gap = 350f
//        var sumTotal = 0
        for (data in list) {
            canvas.drawText(data.productName, 90f, gap, tableItem)
            canvas.drawText(data.qty, 230f, gap, tableItem)
            canvas.drawText(data.price, 440f, gap, tableItem)
            canvas.drawText(data.total, 650f, gap, tableItem)
            gap += 20f
//            sumTotal += data.total.toInt()
        }

        pdfDocument.finishPage(myPage)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Utils(con).showToast("Pdf Generated")
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Utils(con).showToast("Something wrong: $e")
        }
        pdfDocument.close()
    }

}