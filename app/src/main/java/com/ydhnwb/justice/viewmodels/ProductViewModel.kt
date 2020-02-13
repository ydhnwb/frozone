package com.ydhnwb.justice.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.justice.models.Category
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.models.Topping
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
    private var searchResultProduct = MutableLiveData<List<Product>>()
    private var api = JustApi.instance()
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()
    private var allCategory = MutableLiveData<List<Category>>()
    private var allTopping = MutableLiveData<List<Topping>>()
    var hasFetched = MutableLiveData<Boolean>().apply { value = false }


    fun fetchAllProduct(){
        try{
            state.value = ProductState.IsLoading(true)
            api.getAllProduct().enqueue(object : Callback<WrappedListResponse<Product>>{
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
            many?.let { m -> selected.put(product, m+1)
            }
        }else{
            selected[product] = 1
        }
        selectedProducts.value = selected
    }

    fun fetchAllCategory(){
        try{
            state.value = ProductState.IsLoading(true)
            api.getAllCategory().enqueue(object: Callback<WrappedListResponse<Category>>{
                override fun onFailure(call: Call<WrappedListResponse<Category>>, t: Throwable) {
                    println("onFailure -> ${t.message}")
                    state.value = ProductState.IsLoading(false)
                    state.value = ProductState.ShowToast(t.message.toString())
                }

                override fun onResponse(call: Call<WrappedListResponse<Category>>, response: Response<WrappedListResponse<Category>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedListResponse<Category>
                        body.data?.let {
                            allCategory.postValue(it)
                            hasFetched.value = true
                        }
                    }else{
                        println(response.body())
                        state.value = ProductState.ShowToast("Something went wrong :(")
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })
        }catch (e:Exception){
            println("Exception -> ${e.message}")
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }

    fun fetchAllTopping(){
        try{
            state.value = ProductState.IsLoading(true)
            api.getAllTopping().enqueue(object : Callback<WrappedListResponse<Topping>>{
                override fun onFailure(call: Call<WrappedListResponse<Topping>>, t: Throwable) {
                    println("OnFailure -> ${t.message}")
                    state.value = ProductState.IsLoading(false)
                    state.value = ProductState.ShowToast(t.message.toString())
                }

                override fun onResponse(call: Call<WrappedListResponse<Topping>>, response: Response<WrappedListResponse<Topping>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedListResponse<Topping>
                        if(body.status!!){
                            body.data?.let {
                                allTopping.postValue(it)
                            }
                        }
                    }else{
                        state.value = ProductState.ShowToast("Cannot get topping")
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })
        }catch (e:Exception){
            println(e.message)
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }


    fun searchProduct(query : String){
        try{
            api.searchProduct(query).enqueue(object : Callback<WrappedListResponse<Product>>{
                override fun onFailure(call: Call<WrappedListResponse<Product>>, t: Throwable) {
                    state.value = ProductState.IsLoading(false)
                    state.value = ProductState.ShowToast(t.message.toString())
                    println(t.message)
                }
                override fun onResponse(call: Call<WrappedListResponse<Product>>, response: Response<WrappedListResponse<Product>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedListResponse<Product>
                        if(body.status!!){
                            body.data?.let {
                                searchResultProduct.postValue(it)
                            }
                        }else{
                            searchResultProduct.postValue(mutableListOf())
                        }
                    }else{
                        state.value = ProductState.ShowToast("Something went wrong with status code: "+response.code())
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })
        }catch (e: Exception){
            println(e.message)
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }

    fun listenSelectedProduct() = selectedProducts
    fun listenAllProduct() = allProduct
    fun listenState() = state
    fun listenAllcategory() = allCategory
    fun listenSearchResultProduct() = searchResultProduct
}

sealed class ProductState {
    data class IsLoading(var state: Boolean = false) : ProductState()
    data class ShowToast(var message : String) : ProductState()
}