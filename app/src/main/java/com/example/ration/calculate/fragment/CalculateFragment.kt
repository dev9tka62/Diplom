package com.example.ration.calculate.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ration.ProductModel
import com.example.ration.R
import com.example.ration.calculate.CalculateViewModel
import com.example.ration.calculate.CalculeteAdapter
import com.example.ration.calculate.OnItemListener
import com.example.ration.databinding.FragmentCalculateBinding
import com.example.ration.ration.Constants
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CalculateFragment : Fragment() {

    private var _binding: FragmentCalculateBinding? = null
    private val binding get() = _binding!!
    private val calculateVM: CalculateViewModel by sharedViewModel()
    private var adapter: CalculeteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculateBinding.inflate(inflater, container, false)
        if (context?.getSharedPreferences(Constants.NAME_SP, Context.MODE_PRIVATE)
                ?.getBoolean("addedDefaultProduct", false) == false
        ) {
            calculateVM.addDefaultProductsToDB(requireContext())
            context?.getSharedPreferences(Constants.NAME_SP, Context.MODE_PRIVATE)
                ?.edit()?.putBoolean("addedDefaultProduct", true)?.apply()
        }
        calculateVM.listAllDialogProduct.value?.clear()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addProductButton.setOnClickListener {
           ChoseProductFromDBDialogFragment().show(requireFragmentManager(),"chooseProduct")
        }
        if (adapter == null) {
            adapter = CalculeteAdapter(object : OnItemListener {
                override fun onChangeWeight(name: String, weight: Int) {
                    calculateVM.onChangeWeight(name, weight)
                    calculateVM.calculatingCPFC()
                }

                override fun onClickDelete(productModel: ProductModel) {
                    calculateVM.removeProductFromCurrentList(productModel)
                    calculateVM.calculatingCPFC()
                }
            })
        }
        calculateVM.listChoosedProduct.observe(viewLifecycleOwner) {
            adapter?.setData(it.toList())
            adapter?.notifyDataSetChanged()
            calculateVM.calculatingCPFC()
        }
        calculateVM.onDeleteProduct()
        calculateVM.calories.observe(viewLifecycleOwner) {
            binding.calculateCaloriesTextView.text =
                String.format("%s %.0f", getString(R.string.calories), it.toFloat())
        }
        calculateVM.fat.observe(viewLifecycleOwner) {
            binding.calculateFatTextView.text = String.format(
                "%s %.0f",
                getString(R.string.fat),
                it.toFloat()
            )
        }
        calculateVM.carbohydrate.observe(viewLifecycleOwner) {
            binding.calculateCarbohydrateTextView.text = String.format(
                "%s %.0f",
                getString(R.string.carbohydrate),
                it.toFloat()
            )
        }
        calculateVM.protein.observe(viewLifecycleOwner) {
            binding.calculateProteinTextView.text = String.format(
                "%s %.0f",
                getString(R.string.protein),
                it.toFloat()
            )
        }
        binding.addedProductRecyclerView.adapter = adapter
        binding.addedProductRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }
}