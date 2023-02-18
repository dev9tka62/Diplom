package com.example.ration.ration.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ration.R
import com.example.ration.calculate.ChoseProductAdapter
import com.example.ration.calculate.DialogProductModel
import com.example.ration.calculate.OnDialogItemClick
import com.example.ration.databinding.DialogChooseProductBinding
import com.example.ration.ration.Constants
import com.example.ration.ration.RationViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeProductDialogFragment : DialogFragment() {

    private val rationVM by sharedViewModel<RationViewModel>()
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
                        rationVM.changeRationElement(name)
                        Toast.makeText(
                            context,
                            getString(R.string.product_changed),
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.cancel()
                    }
                })
            }
            listView.adapter = adapter
            listView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            parentFragment?.viewLifecycleOwner?.let {
                rationVM.productListOnCategory.observe(it) {
                    adapter?.setData(it.map {
                        DialogProductModel(
                            it.name,
                            String.format(
                                "%sкKal Белки:%sг Жири:%sг Углеводы:%sг",
                                it.calories,
                                it.protein,
                                it.fat,
                                it.carbohydrate
                            )
                        )
                    })
                }
            }
            binding.buttonBreakfast.setOnClickListener {
                rationVM.onChangeCategory(Constants.BREAKFAST)
            }
            binding.buttonSecond.setOnClickListener {
                rationVM.onChangeCategory(Constants.CATEGORY_SECOND)
            }
            binding.buttonSoup.setOnClickListener {
                rationVM.onChangeCategory(Constants.CATEGORY_HOTTER)
            }
            binding.buttonSalats.setOnClickListener {
                rationVM.onChangeCategory(Constants.CATEGORY_SALADS)
            }
            binding.buttonDrinks.setOnClickListener {
                rationVM.onChangeCategory(Constants.CATEGORY_DRINKS)
            }
            rationVM.setProducts()
            val searchView = binding.searchEditText
            searchView.addTextChangedListener(filterTextWatcher)
            builder.setView(binding.root)
            builder.setTitle(R.string.choose_product)
            builder.create()
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