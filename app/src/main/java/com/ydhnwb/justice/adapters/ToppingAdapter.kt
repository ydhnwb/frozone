package com.ydhnwb.justice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.justice.R
import com.ydhnwb.justice.models.Topping
import com.ydhnwb.justice.utils.JusticeUtils
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.item_list_topping.view.*

class ToppingAdapter (private var toppings : MutableList<Topping>, private var context: Context) :
    RecyclerView.Adapter<ToppingAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(topping : Topping, context: Context){
            itemView.topping_cheklist.isChecked = topping.isChecked
            itemView.topping_name.text = topping.name
            itemView.topping_price.text = JusticeUtils.setToIDR(topping.price!!)
            itemView.setOnClickListener {
                topping.isChecked = !topping.isChecked
                itemView.topping_cheklist.isChecked = topping.isChecked
            }
        }
    }

    fun changeList(_toppings : MutableList<Topping>){
        toppings.clear()
        toppings.addAll(_toppings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_topping, parent, false))

    override fun getItemCount() = toppings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(toppings[position], context)
}