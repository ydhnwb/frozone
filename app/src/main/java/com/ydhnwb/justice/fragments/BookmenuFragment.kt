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
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_bookmenu.view.*

class BookmenuFragment : Fragment(R.layout.fragment_bookmenu){
    private lateinit var productViewModel : ProductViewModel

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
                productViewModel.listenSearchResultProduct().observe(it, Observer {
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
                productViewModel.listenAllProduct().observe(it, Observer {updatedProducts ->
                    view.rv_product.adapter?.let { a->
                        if(a is ProductAdapter){
                            arguments?.let { arg ->
                                view.status_view.visibility = View.GONE
                                val category : Category? = arg.getParcelable("category")
                                category?.let {cat ->
                                    val filteredProducts = updatedProducts.filter { product ->
                                        product.category.equals(cat.name)
                                    }
                                    a.updateList(filteredProducts)
                                } ?: kotlin.run {
                                    a.updateList(listOf())
                                }
                            }
                        }
                    }
                })
            }
        }
    }
}