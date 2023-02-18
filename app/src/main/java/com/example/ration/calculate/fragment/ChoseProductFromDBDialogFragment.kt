package com.example.ration.calculate.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ration.R
import com.example.ration.calculate.CalculateViewModel
import com.example.ration.calculate.ChoseProductAdapter
import com.example.ration.calculate.OnDialogItemClick
import com.example.ration.databinding.DialogChooseProductBinding
import com.example.ration.ration.Constants
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ChoseProductFromDBDialogFragment : DialogFragment() {

    private val productVM by sharedViewModel<CalculateViewModel>()
    private var adapter: ChoseProductAdapter? = null
    private lateinit var binding: DialogChooseProductBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogChooseProductBinding.inflate(layoutInflater)
        val dialog = activity?.let { actvt ->
            val builder = AlertDialog.Builder(actvt)
            val listView = binding.listChooseProductItem
            if (adapter == null) {
                adapter = ChoseProductAdapter(object : OnDialogItemClick {
                    override fun onClick(name: String) {
                        productVM.addProductToCurrentList(name)
                        Toast.makeText(
                            context,
                            getString(R.string.added_product),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            listView.adapter = adapter
            listView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            parentFragment?.viewLifecycleOwner?.let {
                productVM.listAllDialogProduct.observe(it) {
                    adapter?.setData(it)
                }
            }
            productVM.getAllProducts()
            val searchView = binding.searchEditText
            searchView.addTextChangedListener(filterTextWatcher)
            builder.setView(binding.root)
            builder.setTitle(R.string.choose_products)
            builder.create()
        }
        binding.buttonBreakfast.setOnClickListener {
            productVM.onChangeCategory(Constants.BREAKFAST)
        }
        binding.buttonSecond.setOnClickListener {
            productVM.onChangeCategory(Constants.CATEGORY_SECOND)
        }
        binding.buttonSoup.setOnClickListener {
            productVM.onChangeCategory(Constants.CATEGORY_HOTTER)
        }
        binding.buttonSalats.setOnClickListener {
            productVM.onChangeCategory(Constants.CATEGORY_SALADS)
        }
        binding.buttonDrinks.setOnClickListener {
            productVM.onChangeCategory(Constants.CATEGORY_DRINKS)
        }
        return dialog ?: throw IllegalStateException("Activity cannot be null")
    }

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            adapter?.filter?.filter(s)
        }
    }
}