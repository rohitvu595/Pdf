package com.pdf.modules.pdf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pdf.R
import com.pdf.databinding.FragmentPdfBinding
import com.pdf.utils.Utils
import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Dialog

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.text.TextUtils
import android.view.Window
import android.widget.Button
import android.widget.EditText

import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdf.modules.pdf.model.Item


class PdfFragment : Fragment() {
    private val viewModel: PdfViewModel by viewModels()

    private lateinit var binding: FragmentPdfBinding

    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pdfViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // Setup a click listener for the Submit and Skip buttons.
        binding.btnAddItem.setOnClickListener { onAddItem(false) }
        binding.btnGenerate.setOnClickListener { onGeneratePdf() }

        var linearLayoutManager = LinearLayoutManager(
            this.requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding!!.rvPdfData.layoutManager = linearLayoutManager

        viewModel.pdfDataList.observe(this.requireActivity(), Observer {
            binding!!.rvPdfData.adapter = PdfAdapter(requireContext(), it)
        })
        if (checkPermission()) {
//            Utils(this.requireContext()).showToast(this.requireActivity(), "Permission Granted")
        } else {
            requestPermission();
        }
        // Update the UI

    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 =
            ContextCompat.checkSelfPermission(this.requireContext(), WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(this.requireContext(), READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this.requireActivity(),
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode === PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] === PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Utils(this.requireContext()).showToast(
                        "Permission Granted.."
                    )
                } else {
                    Utils(this.requireContext()).showToast(
                        "Permission Denined."
                    )
                }
            }
        }
    }

    private fun onAddItem(checker: Boolean) {
        val customerName = binding.etName.text.toString()
        val contact = binding.etPhone.text.toString()
        when (viewModel.isUserValid(customerName, contact)) {
            1 -> {
                Utils(this.requireContext()).showToast("Enter a valid name")
            }
            2 -> {
                Utils(this.requireContext()).showToast(
                    "Enter a valid contact number"
                )
            }
            else -> {
                if (!checker)
                    showDialog()
                else {
                    viewModel.generatePdf(this.requireContext(), customerName, contact)
                }
            }
        }
    }

    private fun onGeneratePdf() {
        if (checkPermission()) {
            onAddItem(true)
        } else {
            requestPermission();
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this.requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialogue_add_item)
        val et_product = dialog.findViewById(R.id.et_product) as EditText
        val et_qty = dialog.findViewById(R.id.et_qty) as EditText
        val et_price = dialog.findViewById(R.id.et_price) as EditText
        val btn_cancel = dialog.findViewById(R.id.btn_cancel) as Button
        val btn_add = dialog.findViewById(R.id.btn_add) as Button
        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_add.setOnClickListener {
            when {
                TextUtils.isEmpty(et_product.text) -> {
                    Utils(this.requireContext()).showToast(
                        "Enter a product name"
                    )
                }
                TextUtils.isEmpty(et_qty.text) -> {
                    Utils(this.requireContext()).showToast(
                        "Enter a quantity"
                    )
                }
                TextUtils.isEmpty(et_price.text) -> {
                    Utils(this.requireContext()).showToast("Enter a price")
                }
                else -> {
                    var tot = "" + et_qty.text.toString().toInt() * et_price.text.toString().toInt()
                    viewModel.addItemToList(
                        Item(
                            et_product.text.toString(),
                            et_qty.text.toString(),
                            et_price.text.toString(),
                            tot
                        )
                    )
                    Utils(this.requireContext()).showToast(
                        "Added successfully"
                    )
                    dialog.dismiss()
                }
            }

        }
        dialog.show()

    }

}