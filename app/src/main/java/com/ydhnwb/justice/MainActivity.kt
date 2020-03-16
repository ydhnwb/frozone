package com.ydhnwb.justice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mancj.materialsearchbar.MaterialSearchBar
import com.ydhnwb.justice.activities.IntroActivity
import com.ydhnwb.justice.activities.PromptPinActivity
import com.ydhnwb.justice.adapters.DetailOrderAdapter
import com.ydhnwb.justice.fragments.BookmenuFragment
import com.ydhnwb.justice.models.Category
import com.ydhnwb.justice.models.Order
import com.ydhnwb.justice.utils.CustomFragmentPagerAdapter
import com.ydhnwb.justice.utils.JusticeUtils
import com.ydhnwb.justice.viewmodels.ProductState
import com.ydhnwb.justice.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_detail_order.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var fragmentAdapter : CustomFragmentPagerAdapter
    private lateinit var bottomSheet : BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        Thread(Runnable {
            if (JusticeUtils.isFirstTime(this@MainActivity)) {
                runOnUiThread { startActivity(Intent(this@MainActivity, IntroActivity::class.java).also {
                    finish()
                })}
            }
        }).start()
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        setupUIComponent()

        if(productViewModel.listenHasFetched().value == false){ productViewModel.fetchAllCategory() }
        productViewModel.listenState().observe(this, Observer { handleUIState(it) })
        productViewModel.betaListenSelectedProducts().observe(this, Observer{
            val totalQuantity : Int = it.size
            val totalPrice : Int = if (it.isEmpty()){ 0
            }else{
                it.sumBy {p ->
                    var totalPriceTemp : Int = p.price!!
                    if(p.selectedToppings.isNotEmpty()){
                        var toppingPrice = 0
                        for(t in p.selectedToppings){
                            toppingPrice += t.price!!
                        }
                        totalPriceTemp += toppingPrice
                    }
                    totalPriceTemp
                }
            }
            tv_item_indicator.text = "$totalQuantity items"
            tv_total_price.text = JusticeUtils.setToIDR(totalPrice)
            rv_detail_order.adapter?.let {adapter ->
                if(adapter is DetailOrderAdapter){
                    adapter.updateList(it)
                }
            }
            if(it.isEmpty()){ bottomsheet_empty_view.visibility = View.VISIBLE }else{ bottomsheet_empty_view.visibility = View.GONE }
        })

        productViewModel.listenAllCategory().observe(this, Observer {
            setupTab(it)
        })
        setupSearchBar()
        btn_checkout.setOnClickListener {
            productViewModel.betaListenSelectedProducts().value?.let{
                if(it.isNotEmpty()){
                    val selectedProducts = productViewModel.betaListenSelectedProducts().value
                    selectedProducts?.let {products ->
                        val branch = JusticeUtils.getCurrentBranch(this)
                        if(branch == 0){
                            showAlert(resources.getString(R.string.configure_app))
                        }else{
                            val deviceId = JusticeUtils.getDeviceId(this)
                            val order = Order(deviceId ,branch, products = products)
                            showBeforeOrder(order)
                        }
                    }
                }else{
                    toast(resources.getString(R.string.info_please_choose_product_first))
                }
            } ?: run { toast(resources.getString(R.string.info_please_choose_product_first)) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, PromptPinActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toast(mess : String?) = Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    private fun isLoading(state : Boolean){ if(state){ loading.visibility = View.VISIBLE }else{ loading.visibility = View.GONE } }
    private fun handleUIState(it : ProductState){
        when(it){
            is ProductState.ShowToast -> toast(it.message)
            is ProductState.IsLoading -> isLoading(it.state)
            is ProductState.ShowAlert -> showAlert(it.message)
            is ProductState.SuccessOrder -> {
                showAlert(resources.getString(R.string.info_success_order))
                productViewModel.clearSelectedProduct()
            }
        }
    }

    private fun setupTab(categories : List<Category>){
        fragmentAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        for (c in categories){ fragmentAdapter.addFragment(BookmenuFragment.instance(c), c.name.toString()) }
        fragmentAdapter.addFragment(BookmenuFragment.instance(null), resources.getString(R.string.info_search_result))
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        if(JusticeUtils.getCurrentBranch(this@MainActivity) == 0 && !JusticeUtils.isFirstTime(this)){
            showAlert(resources.getString(R.string.info_no_branch_selected))
        }
        productViewModel.fetchAllProduct()
    }

    private fun setupSearchBar(){
        search_bar.inflateMenu(R.menu.menu_main)
        search_bar.menu.setOnMenuItemClickListener { item ->
            when(item?.itemId){
                R.id.action_settings -> {
                    startActivity(Intent(this@MainActivity, PromptPinActivity::class.java))
                    true
                }
                else-> true
            }
        }
        search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {}
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    if(text.isNotEmpty()){
                        val size = fragmentAdapter.count
                        if(size != 0){
                            productViewModel.searchProduct(text.toString())
                            viewPager.setCurrentItem(size-1, true)
                            search_bar.clearFocus()
                        }
                    }
                }
            }
        })
    }

    private fun setupUIComponent(){
        rv_detail_order.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DetailOrderAdapter(mutableListOf(), this@MainActivity, productViewModel)
        }
        bottomSheet = BottomSheetBehavior.from(bottomsheet_detail_order)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        detail.setOnClickListener {
            if(bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN){
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun showAlert(message : String){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }

    private fun showBeforeOrder(order: Order){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.info_before_order))
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, which ->
                productViewModel.createOrder(order)
                dialog.dismiss()
                toast(resources.getString(R.string.info_loading))
            }
            setNegativeButton(resources.getString(R.string.info_cancel)){dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }

    override fun onBackPressed() {
        if(bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }else{
            super.onBackPressed()
        }
    }
}