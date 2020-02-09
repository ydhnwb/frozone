package com.ydhnwb.justice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.ydhnwb.justice.R
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.item_list_products.view.*

class ProductAdapter (private var products : MutableList<Product>, private var context: Context, private var productViewModel: ProductViewModel)
    : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_products, parent, false))

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], context, productViewModel)

    fun updateList(updatedProducts: List<Product>){
        products.clear()
        products.addAll(updatedProducts)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            itemView.product_name.text = product.name
            itemView.product_image.load("https://cms.sehatq.com/public/img/article_img/tidak-membuat-gemuk-ini-9-manfaat-jus-alpukat-bagi-kesehatan-1573445329.jpg") {
                transformations(RoundedCornersTransformation(15.0F))
            }
            itemView.product_price.text = "Rp.${product.price}"
            itemView.setOnClickListener {
                println(product.name)
                productViewModel.addSelectedProduct(product)
            }
        }
    }
}