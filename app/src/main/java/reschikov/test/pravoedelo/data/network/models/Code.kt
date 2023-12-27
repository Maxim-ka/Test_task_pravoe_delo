package reschikov.test.pravoedelo.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Code(@SerializedName("code") @Expose val code: String,
                @SerializedName("status") @Expose val status: String)