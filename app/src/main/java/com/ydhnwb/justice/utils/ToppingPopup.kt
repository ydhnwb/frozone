package com.ydhnwb.justice.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.justice.R
import com.ydhnwb.justice.adapters.ToppingAdapter
import com.ydhnwb.justice.models.Product
import com.ydhnwb.justice.models.Topping
import com.ydhnwb.justice.viewmodels.ProductViewModel
import com.ydhnwb.justice.viewmodels.ToppingPopupState
import kotlinx.android.synthetic.main.popup_topping.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToppingPopup : DialogFragment(){
    private lateinit var productViewModel: ProductViewModel
    private var availableTopping = mutableListOf<Topping>()

    companion object {
        fun instance(product: Product) : ToppingPopup{
            val args = Bundle()
            args.putParcelable("product", product)
            return ToppingPopup().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.popup_topping, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product : Product = arguments?.getParcelable("product")!!
        productViewModel = ViewModelProvider(activity!!).get(ProductViewModel::class.java)
        view.rv_popup_topping.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ToppingAdapter(mutableListOf(), activity!!)
        }
        productViewModel.listenAllTopping().observe(viewLifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Default).launch{
                filterTopping(it)
            }
        })
        productViewModel.listenToppingPopupState().observe(viewLifecycleOwner, Observer {
            when(it){
                is ToppingPopupState.IsLoading -> {
                    if(it.state){
                        view.loading_popup.visibility = View.VISIBLE
                        view.rv_popup_topping.visibility = View.GONE
                        view.btn_submit_popup_topping.isEnabled = false
                    }else{
                        view.rv_popup_topping.visibility = View.VISIBLE
                        view.loading_popup.visibility = View.GONE
                        view.btn_submit_popup_topping.isEnabled = true
                    }
                }
                is ToppingPopupState.ShowToast -> Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        })
        productViewModel.fetchAllTopping()
        view.btn_submit_popup_topping.setOnClickListener {
            val selectedTopping = availableTopping.filter { topping ->
                topping.isChecked
            }
            product.selectedToppings = selectedTopping.toMutableList()
            productViewModel.betaAddSelectedProduct(product)
            println(product)
            this.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private suspend fun filterTopping(it : List<Topping>){
        val product : Product = arguments?.getParcelable("product")!!
        availableTopping = it.filter { topping ->
            topping.category.equals(product.category)
        }.toMutableList()
        attachToRecycler(availableTopping)
    }

    private suspend fun attachToRecycler(filteredToppings : MutableList<Topping>){
        withContext(Dispatchers.Main){
            view!!.rv_popup_topping.adapter?.let { adapter ->
                if(adapter is ToppingAdapter){
                    adapter.changeList(filteredToppings)
                }
            }
        }
    }
}