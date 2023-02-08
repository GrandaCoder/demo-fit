package com.example.demo_fit

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//Se crea la data class, con los parametros que va a recibir la informacion desde la base de datos
@IgnoreExtraProperties
data class Snapshot(@get:Exclude var id: String = "",
                    var title: String="",
                    var photoUrl:String ="",
                    var likeList: Map<String,Boolean> = mutableMapOf())
