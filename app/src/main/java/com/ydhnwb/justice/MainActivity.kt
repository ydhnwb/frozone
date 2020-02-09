package com.ydhnwb.justice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.justice.fragments.BookmenuFragment
import com.ydhnwb.justice.utils.CustomFragmentPagerAdapter
import com.ydhnwb.justice.viewmodels.ProductState
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        productViewModel.getState().observe(this, Observer {
            handleUIState(it)
        })
        productViewModel.getSelectedProduct().observe(this, Observer {
            val totalQuantity: Int = it.map { h->  h.value }.sum()
            val totalPrice : Int = it.map { h -> h.key.price!!*h.value }.sum()
            tv_item_indicator.text = "$totalQuantity items"
            tv_total_price.text = "Rp.$totalPrice"
        })
        setupTab()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toast(mess : String?) = Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    private fun isLoading(state : Boolean){
        if(state){
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }
    private fun handleUIState(it : ProductState){
        when(it){
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.IsLoading -> isLoading(it.state)
        }
    }
    private fun setupTab(){
        val fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(BookmenuFragment(), "Makanan")
        fragmentAdapter.addFragment(BookmenuFragment(), "Minuman")
        fragmentAdapter.addFragment(BookmenuFragment(), "Lainnya")
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
