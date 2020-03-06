package com.ydhnwb.justice.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ydhnwb.justice.models.Category
import com.ydhnwb.justice.models.Order
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.models.Topping
import com.ydhnwb.justice.utils.SingleLiveEvent
import com.ydhnwb.justice.webservices.JustApi
import com.ydhnwb.justice.webservices.WrappedListResponse
import com.ydhnwb.justice.webservices.WrappedResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProductViewModel : ViewModel(){
    private var allProduct = MutableLiveData<List<Product>>()
    private var searchResultProduct = MutableLiveData<List<Product>>()
    private var api = JustApi.instance()
    private var state : SingleLiveEvent<ProductState> = SingleLiveEvent()
    private var toppingPopupState : SingleLiveEvent<ToppingPopupState> = SingleLiveEvent()
    private var allCategory = MutableLiveData<List<Category>>()
    private var allTopping = MutableLiveData<List<Topping>>()
    private var betaSelectedProducts = MutableLiveData<List<Product>>()
    private var hasFetched = MutableLiveData<Boolean>().apply { value = false }

    //init

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


    fun betaDeleteSelectedProduct(pos : Int) {
        val products = if(betaSelectedProducts.value == null){ mutableListOf() } else { betaSelectedProducts.value as MutableList<Product> }
        products.removeAt(pos)
        betaSelectedProducts.value = products
    }

    fun betaAddSelectedProduct(product: Product){
       val selectedProducts = if(betaSelectedProducts.value == null){
           mutableListOf()
       } else {
           betaSelectedProducts.value as MutableList<Product>
       }
        selectedProducts.add(product)
        betaSelectedProducts.value = selectedProducts
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
            toppingPopupState.value = ToppingPopupState.IsLoading(true)
            api.getAllTopping().enqueue(object : Callback<WrappedListResponse<Topping>>{
                override fun onFailure(call: Call<WrappedListResponse<Topping>>, t: Throwable) {
                    println("OnFailure -> ${t.message}")
                    toppingPopupState.value = ToppingPopupState.IsLoading(false)
                    toppingPopupState.value = ToppingPopupState.ShowToast(t.message.toString())
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
                        toppingPopupState.value = ToppingPopupState.ShowToast("Cannot get topping")
                    }
                    toppingPopupState.value = ToppingPopupState.IsLoading(false)
                }
            })
        }catch (e:Exception){
            println(e.message)
            toppingPopupState.value = ToppingPopupState.IsLoading(false)
            toppingPopupState.value = ToppingPopupState.ShowToast(e.message.toString())
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

    fun createOrder(order: Order){
        try{
            state.value = ProductState.IsLoading(true)
            val gson = Gson().toJson(order)
            api.createOrder(gson).enqueue(object : Callback<WrappedResponse<Order>>{
                override fun onFailure(call: Call<WrappedResponse<Order>>, t: Throwable) {
                    println("onFailure -> ${t.message}")
                    state.value = ProductState.IsLoading(false)
                    state.value = ProductState.ShowToast(t.message.toString())
                    state.value = ProductState.ShowAlert("Tidak dapat membuat pesanan.")

                }

                override fun onResponse(call: Call<WrappedResponse<Order>>, response: Response<WrappedResponse<Order>>) {
                    if(response.isSuccessful){
                        val body = response.body() as WrappedResponse<Order>
                        if (body.status){
                            state.value = ProductState.SuccessOrder
                        }else{
                            state.value = ProductState.ShowToast("Gagal membuat order")
                        }
                    }else{
                        state.value = ProductState.ShowToast("Response is not successfull")
                        state.value = ProductState.ShowAlert("Gagal saat membuat pesanan")
                    }
                    state.value = ProductState.IsLoading(false)
                }
            })

        }catch (e: Exception){
            println("Exception -> ${e.message}")
            state.value = ProductState.IsLoading(false)
            state.value = ProductState.ShowToast(e.message.toString())
        }
    }

    fun clearSelectedProduct(){ betaSelectedProducts.value = mutableListOf() }

    fun listenAllProduct() = allProduct
    fun listenState() = state
    fun listenAllCategory() = allCategory
    fun listenSearchResultProduct() = searchResultProduct
    fun listenHasFetched() = hasFetched
    fun betaListenSelectedProducts() = betaSelectedProducts
    fun listenAllTopping() = allTopping
    fun listenToppingPopupState() = toppingPopupState
}

sealed class ProductState {
    data class IsLoading(var state: Boolean = false) : ProductState()
    data class ShowToast(var message : String) : ProductState()
    data class ShowAlert(var message : String) : ProductState()
    object SuccessOrder : ProductState()
}

sealed class ToppingPopupState{
    data class IsLoading(var state : Boolean) : ToppingPopupState()
    data class ShowToast(var message : String) : ToppingPopupState()
}