package com.example.ration.calculate

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ration.ProductModel
import com.example.ration.data_base.DatabaseHelper
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_CALORIES
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_CARBOHYDRATE
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_FAT
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_ID
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_NAME
import com.example.ration.data_base.DatabaseHelper.Companion.COLUMN_PROTEIN
import com.example.ration.data_base.DatabaseHelper.Companion.TABLE_BREAKFAST
import com.example.ration.data_base.DatabaseHelper.Companion.TABLE_DRINKS
import com.example.ration.data_base.DatabaseHelper.Companion.TABLE_HOTTER
import com.example.ration.data_base.DatabaseHelper.Companion.TABLE_SALADS
import com.example.ration.data_base.DatabaseHelper.Companion.TABLE_SECOND
import com.example.ration.ration.Constants
import kotlinx.coroutines.launch


class CalculateViewModel(private val repository: CalculateRepository) : ViewModel() {

    val calories = MutableLiveData<Double>()
    val protein = MutableLiveData<Double>()
    val fat = MutableLiveData<Double>()
    val carbohydrate = MutableLiveData<Double>()

    val listChoosedProduct = MutableLiveData<MutableList<ProductModel>>()
    val listAllProduct = MutableLiveData<MutableList<ProductModel>>()
    var listAllDialogProduct = MutableLiveData<MutableList<DialogProductModel>>()

    fun onChangeCategory(category: String) {
        val list = mutableListOf<ProductModel>()
        listAllProduct.value?.forEach {
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

    fun onChangeWeight(name: String, weight: Int) {
        listChoosedProduct.value?.forEach {
            if (name == it.name) {
                it.weight = weight
            }
        }
    }

    fun onDeleteProduct() {
        val list = listChoosedProduct.value
        listAllProduct.value?.forEach {
            if (list?.contains(it) != false) {
                list?.remove(it)
            }
        }
        listChoosedProduct.value = list ?: emptyList<ProductModel>().toMutableList()
    }

    fun calculatingCPFC() {
        calories.value = 0.0
        protein.value = 0.0
        fat.value = 0.0
        carbohydrate.value = 0.0
        listChoosedProduct.value?.forEach {
            calories.value = it.calories * it.weight / 100 + calories.value!!
            protein.value = it.protein * it.weight / 100 + protein.value!!
            fat.value = it.fat * it.weight / 100 + fat.value!!
            carbohydrate.value = it.carbohydrate / 100 * it.weight + carbohydrate.value!!
        }
    }

    fun getAllProducts() {
        fat.value = 0.0
        carbohydrate.value = 0.0
        calories.value = 0.0
        protein.value = 0.0
        viewModelScope.launch {
            val list = repository.getAllProducts().toMutableList()
            val list1 = mutableListOf<DialogProductModel>()
            listAllProduct.value = repository.getAllProducts().toMutableList()
            list.forEach {
                val element = DialogProductModel(
                    it.name,
                    String.format(
                        "%sкKal Белки:%sг Жири:%sг Углеводы:%sг",
                        it.calories,
                        it.protein,
                        it.fat,
                        it.carbohydrate
                    )
                )
                if (!list1.contains(element)) {
                    list1.add(element)
                }
            }
            list1.sortBy { it.title }
            listAllDialogProduct.value = list1
        }
    }

    fun addProductToCurrentList(name: String) {
        if (listChoosedProduct.value == null) {
            listChoosedProduct.value = mutableListOf()
        }
        viewModelScope.launch {
            val list = listChoosedProduct.value
            val product = repository.getProductByName(name)
            val listName = list?.map { it.name }
            if (listName?.contains(name) == false) {
                list.add(product)
            }
            listChoosedProduct.value = list ?: mutableListOf()
        }
    }

    fun removeProductFromCurrentList(productModel: ProductModel) {
        val list = listChoosedProduct.value
        list?.remove(productModel)
        listChoosedProduct.value = list ?: mutableListOf()
    }

    fun addNewProductToDB(productModel: ProductModel) {
        viewModelScope.launch { repository.addProductToDB(productModel) }
    }

    private fun addDefaultProductsToTable(context: Context, table: String) {
        val databaseHelper = DatabaseHelper(context);
        databaseHelper.create_db();
        val db = databaseHelper.open()
        val userCursor = db.rawQuery("select * from $table", null);
        viewModelScope.launch {
            while (userCursor.moveToNext()) {
                val productModel =
                    ProductModel(
                        userCursor.getString(userCursor.getColumnIndex(COLUMN_NAME) ?: 0),
                        userCursor.getString(
                            userCursor.getColumnIndex(
                                COLUMN_CALORIES
                            ) ?: 0
                        ).toDouble(),
                        userCursor.getString(
                            userCursor.getColumnIndex(
                                COLUMN_PROTEIN
                            ) ?: 0
                        ).toDouble(),
                        userCursor.getString(
                            userCursor.getColumnIndex(
                                COLUMN_FAT
                            ) ?: 0
                        ).toDouble(),
                        userCursor.getString(
                            userCursor.getColumnIndex(
                                COLUMN_CARBOHYDRATE
                            ) ?: 0
                        ).toDouble(), table, 0
                    )
                repository.addProductToDB(productModel)
            }
            userCursor.close()
            db.close()
            databaseHelper.close()
        }
    }

    fun addDefaultProductsToDB(context: Context) {
        addDefaultProductsToTable(context, TABLE_BREAKFAST)
        addDefaultProductsToTable(context, TABLE_DRINKS)
        addDefaultProductsToTable(context, TABLE_HOTTER)
        addDefaultProductsToTable(context, TABLE_SECOND)
        addDefaultProductsToTable(context, TABLE_SALADS)
    }
}