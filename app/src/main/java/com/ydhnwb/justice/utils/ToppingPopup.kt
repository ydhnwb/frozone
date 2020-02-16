package com.ydhnwb.justice.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ydhnwb.justice.R
import com.ydhnwb.justice.adapters.ToppingAdapter
import com.ydhnwb.justice.models.Topping
import kotlinx.android.synthetic.main.popup_topping.view.*


class ToppingPopup : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.popup_topping, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toppings = mutableListOf<Topping>()
        toppings.add(Topping(1, "Oreo", "Minuman", 3000))
        toppings.add(Topping(3, "Mesis", "Minuman", 3000))
        toppings.add(Topping(4, "Milo", "Minuman", 3000))
        toppings.add(Topping(5, "Topping 1", "Minuman", 3000))
        toppings.add(Topping(6, "Topping 2", "Minuman", 3000))
        toppings.add(Topping(8, "Topping 3", "Minuman", 3000))
        toppings.add(Topping(9, "Topping 4", "Minuman", 3000))
        toppings.add(Topping(10, "Topping 5", "Minuman", 3000))
        toppings.add(Topping(11, "Topping 6", "Minuman", 3000))
        toppings.add(Topping(12, "Topping 7", "Minuman", 3000))
        view.rv_popup_topping.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ToppingAdapter(toppings, activity!!)
        }
        view.btn_submit_popup_topping.setOnClickListener {
            this.dialog?.dismiss()
        }
        this.dialog?.setTitle("Pilih topping:")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}