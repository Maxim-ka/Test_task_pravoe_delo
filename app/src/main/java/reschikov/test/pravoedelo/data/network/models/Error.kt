package reschikov.test.pravoedelo.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Error(@SerializedName("error") @Expose val error: String)
