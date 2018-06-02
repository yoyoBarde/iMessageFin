package com.example.kent.imessage.model

data class ChatChannel(val userIds: MutableList<String>) { constructor() : this(mutableListOf()) }