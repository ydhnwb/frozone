package com.ydhnwb.justice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.justice.R
import com.ydhnwb.justice.models.Branch
import com.ydhnwb.justice.utils.BranchPopup
import com.ydhnwb.justice.utils.JusticeUtils
import com.ydhnwb.justice.viewmodels.SettingViewModel
import kotlinx.android.synthetic.main.item_list_branch.view.*

class BranchAdapter(private var branches: MutableList<Branch>, private var context: Context, private var dialog: BranchPopup, private var settingViewModel: SettingViewModel
) : RecyclerView.Adapter<BranchAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(branch: Branch, context: Context, d : BranchPopup, svm : SettingViewModel){
            itemView.branch_name.text = branch.name
            itemView.setOnClickListener {
                JusticeUtils.setBranch(context, branch.id!!)
                JusticeUtils.setBranchName(context, branch.name.toString())
                svm.updateBranchName(branch.name.toString())
                d.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_branch, parent, false))

    override fun getItemCount() = branches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(branches[position], context, dialog, settingViewModel)
}