package com.ydhnwb.justice.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order (
    @SerializedName("branch") var branch : Int? = null,
    @SerializedName("products") var products : List<Product>
) : Parcelable