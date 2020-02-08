package com.ydhnwb.justice.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.utils.SingleLiveEvent
import com.ydhnwb.justice.webservices.JustApi
import com.ydhnwb.justice.webservices.WrappedListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProductViewModel : ViewModel(){
    private var allProduct = MutableLiveData<List<Product>>()
    private var selectedProducts = MutableLiveData<HashMap<Product, Int>>()
    private var api = JustApi.instance()
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()


    fun fetchAllProduct(){
        try{
            state.value = ProductState.IsLoading(true)
            api.products().enqueue(object : Callback<WrappedListResponse<Product>>{
                override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                    state.value = ProductState.IsLoading(false)
                    state.value = ProductState.ShowToast(t.message.toString())
                    println(t.message)
                }
                override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedListResponse<Product>
                        body.data?.let {
                            allProduct.postValue(it)
                        }
                    }else{
                        state.value = ProductState.ShowToast("Something went wrong with status code: "+response.code())
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })
        }catch (e : Exception){
            println("Exception -> ${e.message}")
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }

    fun addSelectedProduct(product: Product){
        val selected : HashMap<Product, Int> = if (selectedProducts.value != null) {selectedProducts.value!! }
        else {HashMap()}

        if(selected.containsKey(product)){
            val many : Int? = selected[product]
            many?.let { m ->
                selected.put(product, m+1)
            }
        }else{
            selected[product] = 1
        }
        selectedProducts.value = selected
    }

    fun getSelectedProduct() = selectedProducts
    fun getAllProduct() = allProduct
    fun getState() = state
}

sealed class ProductState {
    data class IsLoading(var state: Boolean = false) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    object Success : ProductState()
    object Reset : ProductState()
}