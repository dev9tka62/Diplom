package com.example.ration.delete

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ration.R
import com.example.ration.calculate.DialogProductModel
import com.example.ration.calculate.OnDialogItemClick
import com.example.ration.databinding.DialogChooseProductBinding
import com.example.ration.databinding.FragmentDeleteProductBinding
import com.example.ration.ration.Constants
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeleteProductFromDBFragment : Fragment(), OnDialogItemClick {

    private lateinit var binding: FragmentDeleteProductBinding
    private var adapter: DeleteProductAdapter? = null
    private val deleteViewModel by sharedViewModel<DeleteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter == null) {
            adapter = DeleteProductAdapter(this)
        }
        binding.listChooseProductItem.adapter = adapter
        binding.listChooseProductItem.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.buttonBreakfast.setOnClickListener {
            deleteViewModel.onChangeCategory(Constants.BREAKFAST)
        }
        binding.buttonSecond.setOnClickListener {
            deleteViewModel.onChangeCategory(Constants.CATEGORY_SECOND)
        }
        binding.buttonSoup.setOnClickListener {
            deleteViewModel.onChangeCategory(Constants.CATEGORY_HOTTER)
        }
        binding.buttonSalats.setOnClickListener {
            deleteViewModel.onChangeCategory(Constants.CATEGORY_SALADS)
        }
        binding.buttonDrinks.setOnClickListener {
            deleteViewModel.onChangeCategory(Constants.CATEGORY_DRINKS)
        }
        deleteViewModel.listAllDialogProduct.observe(viewLifecycleOwner) { list ->
            adapter?.setData(list)
        }
        deleteViewModel.getAllProduct()
        binding.searchEditText.addTextChangedListener(filterTextWatcher)
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

    override fun onClick(name: String) {
        deleteViewModel.setDeleteName(name)
        ChooseDialog().show(requireFragmentManager(), "chooseDialog")
    }
}