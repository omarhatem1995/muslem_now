package com.zaker.ui.sidemenu

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.zaker.R
import com.zaker.ui.fragments.home.HomeViewModel
import android.os.ParcelFileDescriptor

import android.graphics.pdf.PdfRenderer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import android.graphics.Bitmap
import com.zaker.databinding.ActivitySahihBukharyBinding


class SahihBukharyActivity : AppCompatActivity() {
    lateinit var binding: ActivitySahihBukharyBinding
    private val sahihBukharyViewModel: HomeViewModel by viewModels()

    private var FILENAME = "sahih_bukhary_arabic.pdf"

    private val pageIndex = 0
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sahih_bukhary)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sahih_bukhary)
        binding.sahihBukharyViewModel = sahihBukharyViewModel

        FILENAME = if(intent.extras?.getString("bukharyOr40s").equals("bukhary")) {

            if (sahihBukharyViewModel.preference.getLanguage().equals("ar"))
                "sahih_bukhary_arabic.pdf"
            else
                "sahih_bukhari_english.pdf"
        }else{
            "the_40s.pdf"
        }

        binding.nextPageButton.setOnClickListener { onNextDocClick() }
        binding.previousPageButton.setOnClickListener { onPreviousDocClick() }
    }

    override fun onStart() {
        super.onStart()
        super.onStart()
        try {
            openRenderer(applicationContext)
            showPage(pageIndex)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            closeRenderer()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onStop()
    }
    fun onPreviousDocClick() {
        showPage(currentPage!!.index - 1)
    }
    fun onNextDocClick() {
        showPage(currentPage!!.index + 1)
    }

    @Throws(IOException::class)
    private fun openRenderer(context: Context) {
        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            val asset: InputStream = context.assets.open(FILENAME)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (asset.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            asset.close()
            output.close()
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        // This is the PdfRenderer we use to render the PDF.
        if (parcelFileDescriptor != null) {
            pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
        }
    }
    @Throws(IOException::class)
    private fun closeRenderer() {
        currentPage?.close()
        pdfRenderer!!.close()
        parcelFileDescriptor!!.close()
    }
    private fun showPage(index: Int) {
        if (pdfRenderer!!.pageCount <= index) {
            return
        }
        // Make sure to close the current page before opening another one.
        currentPage?.close()
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer!!.openPage(index)
        // Important: the destination bitmap must be ARGB (not RGB).
        val bitmap = Bitmap.createBitmap(
            currentPage!!.width, currentPage!!.height,
            Bitmap.Config.ARGB_8888
        )
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        // We are ready to show the Bitmap to user.
        binding.bookPageImageView.setImageBitmap(bitmap)
        updateUi()
    }
    private fun updateUi() {
        val index = currentPage!!.index
        val pageCount = pdfRenderer!!.pageCount
        binding.previousPageButton.isEnabled = 0 != index
        binding.nextPageButton.isEnabled = index + 1 < pageCount
    }
    fun getPageCount(): Int {
        return pdfRenderer!!.pageCount
    }
}