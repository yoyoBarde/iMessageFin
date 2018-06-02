package com.example.kent.imessage.model




data class User(val name: String, val email: String,val password:String , val contactNumber: String,
                val imagePath: String?, val registrationTokens: MutableList<String>) {
    constructor(): this("", "", "","", null, mutableListOf())
}
