package com.ydhnwb.justice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.justice.R
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.utils.JusticeUtils
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.item_list_detail_order.view.*

class DetailOrderAdapter (private var selectedProducts : MutableList<Product>, private var context: Context, private var productViewModel: ProductViewModel) :
        RecyclerView.Adapter<DetailOrderAdapter.ViewHolder>(){

    fun updateList(updatedSelectedProduct : List<Product>){
        selectedProducts.clear()
        selectedProducts.addAll(updatedSelectedProduct)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_detail_order, parent, false))
    }

    override fun getItemCount() = selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =holder.bind(selectedProducts[position], context, productViewModel)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            var totalPrice : Int = product.price!!
            itemView.detail_order_name.text = product.name
            product.selectedToppings.isNotEmpty().let {
                val desc = StringBuilder()
                for(t in product.selectedToppings){
                    desc.append(" ${t.name},")
                    println(t.name)
                    totalPrice += t.price!!
                }
                itemView.detail_order_more.text = "toppings :$desc"
            }
            itemView.detail_order_price.text = JusticeUtils.setToIDR(totalPrice)
        }
    }
}