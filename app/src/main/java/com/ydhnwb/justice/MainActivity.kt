package com.ydhnwb.justice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.justice.fragments.BookmenuFragment
import com.ydhnwb.justice.models.Category
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
        if(productViewModel.hasFetched.value == false){
            productViewModel.fetchAllCategory()
        }
        productViewModel.listenState().observe(this, Observer { it -> handleUIState(it) })
        productViewModel.listenSelectedProduct().observe(this, Observer {
            val totalQuantity: Int = it.map { h->  h.value }.sum()
            val totalPrice : Int = it.map { h -> h.key.price!!*h.value }.sum()
            tv_item_indicator.text = "$totalQuantity items"
            tv_total_price.text = "Rp.$totalPrice"
        })

        productViewModel.listenAllcategory().observe(this, Observer {
            setupTab(it)
        })
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
        if(state){ loading.visibility = View.VISIBLE }else{ loading.visibility = View.GONE }
    }
    private fun handleUIState(it : ProductState){
        when(it){
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.IsLoading -> isLoading(it.state)
        }
    }
    private fun setupTab(categories : List<Category>){
        val fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        for (c in categories){
            println(c.name)
            fragmentAdapter.addFragment(BookmenuFragment.instance(c), c.name.toString())
        }
        fragmentAdapter.addFragment(BookmenuFragment.instance(null), "Hasil Pencarian")
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        productViewModel.fetchAllProduct()
    }
}
