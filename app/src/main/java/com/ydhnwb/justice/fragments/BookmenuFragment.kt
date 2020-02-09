package com.ydhnwb.justice.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ydhnwb.justice.R
import com.ydhnwb.justice.adapters.ProductAdapter
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.fragment_bookmenu.view.*


class BookmenuFragment : Fragment(R.layout.fragment_bookmenu){
    private var productViewModel : ProductViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            productViewModel = ViewModelProvider(it).get(ProductViewModel::class.java)
            productViewModel!!.fetchAllProduct()
            view.rv_product.apply {
                layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    GridLayoutManager(it, 2)
                }else{
                    GridLayoutManager(it, 4)
                }
                adapter = ProductAdapter(mutableListOf(), it, productViewModel!!)
            }
            productViewModel!!.getAllProduct().observe(it, Observer {updatedProducts ->
                view.rv_product.adapter?.let { a->
                    if(a is ProductAdapter){
                        a.updateList(updatedProducts)
                    }
                }
            })
        }
    }
}