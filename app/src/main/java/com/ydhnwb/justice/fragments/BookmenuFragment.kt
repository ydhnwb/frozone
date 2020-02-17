package com.ydhnwb.justice.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.justice.R
import com.ydhnwb.justice.adapters.ProductAdapter
import com.ydhnwb.justice.models.Category
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_bookmenu.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//For now, we use only one fragment.
//After we get all category, we instance this fragment of each categories
//and set the category via argument,
//after that, we filter all product by category that passed via argument.
//because, filtering is so heavy on mainthread, we will use kotlin coroutine
class BookmenuFragment : Fragment(R.layout.fragment_bookmenu){
    private lateinit var productViewModel : ProductViewModel

    //an static method, if we passed the category, then its a product tab
    //if not, then it must be "search result" tab
    companion object {
        fun instance(category: Category?) : BookmenuFragment {
            return if(category == null){
                BookmenuFragment()
            }else{
                val args = Bundle()
                args.putParcelable("category", category)
                BookmenuFragment().apply {
                    arguments = args
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            //create an instance of ViewModel from the activity, so this fragment
            //and the host activity who hold this fragment
            //will listen to a same data. its like:
            //           yourData
            //             /   \
            //       activity  fragment
            productViewModel = ViewModelProvider(it).get(ProductViewModel::class.java)
            view.rv_product.apply {
                layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    GridLayoutManager(it, 2)
                }else{
                    GridLayoutManager(it, 4)
                }
                adapter = ProductAdapter(mutableListOf(), it, productViewModel)
            }
            if(arguments == null ){
                view.status_view.visibility = View.VISIBLE
                productViewModel.listenSearchResultProduct().observe(viewLifecycleOwner, Observer {
                    view.rv_product.adapter?.let { adapter ->
                        if(adapter is ProductAdapter){
                            if(it.isEmpty()){
                                view.status_view.visibility = View.VISIBLE
                                view.status_view.text = "Tidak ada hasil. Tap pada field di atas untuk mencari"
                            }else{
                                view.status_view.visibility = View.GONE
                            }
                            adapter.updateList(it)
                        }
                    }
                })
            }else{
                productViewModel.listenAllProduct().observe(viewLifecycleOwner, Observer {updatedProducts ->
                    arguments?.let { arg ->
                        view.status_view.visibility = View.GONE
                        val category : Category? = arg.getParcelable("category")
                        category?.let {cat ->
                            //there are three type of dispatcher
                            //1. Main (for work on mainThread)
                            //2. IO (for input-output process)
                            //3. Default (for heavy computation)
                            CoroutineScope(Dispatchers.Default).launch {
                                filterProductsByCategory(cat, updatedProducts)
                            }
                        }
                    }
                })
            }
        }
    }

    //instance a new job for heavy computational like filtering etc on background thread
    private suspend fun filterProductsByCategory(categoryAttached : Category, products : List<Product>) {
        val filtered =  products.filter { product -> product.category.equals(categoryAttached.name) }
        attachToRecycler(filtered as MutableList<Product>)
    }


    //because, the UI is on mainthread, and the filtering process is on background thread,
    //we need to dispatch the result of coroutine to UI/mainthread using
    //withContext(Main) syntax
    private suspend fun attachToRecycler(filteredProducts : MutableList<Product>){
        withContext(Main){
            view?.rv_product?.adapter?.let {
                if(it is ProductAdapter){
                    it.updateList(filteredProducts)
                }
            }
        }
    }
}
