package com.ydhnwb.justice.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order (
    @SerializedName("name") var name : String? = null,
    @SerializedName("branch") var branch : Int? = null,
    @SerializedName("products") var products : List<Product>
) : Parcelable