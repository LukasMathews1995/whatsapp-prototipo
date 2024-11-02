package com.lucasmathews.whatsapp.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class Conversa(
    val idUsuarioRemetente:String="",
    val idUsuarioDestinatario:String="",
    val foto:String="",
    val nome:String="",
    val ultimaMensagem:String="",
    @ServerTimestamp
    val data: Date?=null
):Parcelable
