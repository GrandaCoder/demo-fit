package com.example.demo_fit

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Snapshot(var id: String = "",
                    var title: String,
                    var photoURL:String ="",
                    var likeList: Map<String,Boolean> = mutableMapOf())