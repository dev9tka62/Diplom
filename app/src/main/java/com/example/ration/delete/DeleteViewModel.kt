package com.example.ration.delete

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ration.ProductModel
import com.example.ration.calculate.DialogProductModel
import kotlinx.coroutines.launch

class DeleteViewModel(private val deleteRepository: DeleteRepository) : ViewModel() {

    val allProductInDB = MutableLiveData<List<ProductModel>>()
    val deletingProductName = MutableLiveData<String>()
    val listAllDialogProduct = MutableLiveData<List<DialogProductModel>>()

    fun onChangeCategory(category: String) {
        val list = mutableListOf<ProductModel>()
        allProductInDB.value?.forEach {
            if (it.product_сategory == category) {
                list.add(it)
            }
        }
        list.sortBy { it.name }
        listAllDialogProduct.value = list.map {
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
        }.toMutableList()
    }

    fun setDeleteName(name: String) {
        deletingProductName.value = name
    }

    fun deleteProduct() {
        viewModelScope.launch {
            deleteRepository.deleteProduct(deletingProductName.value ?: "")
            allProductInDB.value = deleteRepository.getAllProduct()
            listAllDialogProduct.value = allProductInDB.value?.map {
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
            }?.toMutableList()?: emptyList()
        }
    }

    fun getAllProduct() {
        viewModelScope.launch {
            allProductInDB.value = deleteRepository.getAllProduct()
            listAllDialogProduct.value = allProductInDB.value?.map {
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
            }?.toMutableList()?: emptyList()
        }
    }
}