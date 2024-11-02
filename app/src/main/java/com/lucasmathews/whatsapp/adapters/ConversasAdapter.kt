package com.lucasmathews.whatsapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.lucasmathews.whatsapp.databinding.ItemConversasBinding
import com.lucasmathews.whatsapp.model.Conversa
import com.squareup.picasso.Picasso


class ConversasAdapter(private val onClick:(Conversa)->Unit):Adapter<ConversasAdapter.ConversaViewHolder>() {
    private var listaConversas= emptyList<Conversa>()
    fun adicionarLista(lista:List<Conversa>){
        listaConversas = lista
        notifyDataSetChanged()
    }
    inner class ConversaViewHolder(private val binding:ItemConversasBinding):ViewHolder(binding.root){
        fun bind(conversa: Conversa){
            val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid

if(idUsuarioLogado!=conversa.idUsuarioDestinatario)
            binding.textConversasNome.text = conversa.nome
            Picasso.get().load(conversa.foto).into(binding.imgConversasFoto)
            binding.textConversaMensagem.text=conversa.ultimaMensagem
            binding.clItemConversa.setOnClickListener {
                onClick(conversa)


        }
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversaViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val itemView = ItemConversasBinding.inflate(inflate,parent,false)
       return ConversaViewHolder(itemView)
    }

    override fun getItemCount(): Int {
      return listaConversas.size
    }

    override fun onBindViewHolder(holder: ConversaViewHolder, position: Int) {
        val usuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()
        Log.i("dados","$usuarioLogado")
        val conversa= listaConversas[position]
        if(usuarioLogado != conversa.idUsuarioDestinatario){

        holder.bind(conversa)

    }
    }
}
