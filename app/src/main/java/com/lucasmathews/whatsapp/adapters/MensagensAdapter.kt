package com.lucasmathews.whatsapp.adapters



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.lucasmathews.whatsapp.databinding.ItemMensagensDestinatarioBinding

import com.lucasmathews.whatsapp.databinding.ItemMensagensRemetenteBinding
import com.lucasmathews.whatsapp.model.Mensagem
import com.lucasmathews.whatsapp.util.Constantes

class MensagensAdapter():Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(list:List<Mensagem>){
        listaMensagens= list
        notifyDataSetChanged()
    }

    class MensagensRemetenteViewHolder(private val binding: ItemMensagensRemetenteBinding):ViewHolder(binding.root){
        fun bind(mensagem: Mensagem){
        binding.textMensagemRemetente.text = mensagem.mensagem
        }
    }
    class MensagensDestinatarioViewHolder(private val binding:ItemMensagensDestinatarioBinding):ViewHolder(binding.root){
        fun bind(mensagem: Mensagem){
            binding.textMensagensDestinatario.text = mensagem.mensagem
        }
    }

    override fun getItemViewType(position: Int): Int {
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val mensagem = listaMensagens[position]
        return if( idUsuarioLogado==mensagem.idUsuario){
            Constantes.TIPO_REMETENTE
        }else {
            Constantes.TIPO_DESTINATARIO
        }


        return super.getItemViewType(position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType==Constantes.TIPO_REMETENTE){
            val inflate = LayoutInflater.from(parent.context)
            val itemView = ItemMensagensRemetenteBinding.inflate(inflate,parent,false)
            MensagensRemetenteViewHolder(itemView)
        } else{
            val inflate = LayoutInflater.from(parent.context)
            val itemView = ItemMensagensDestinatarioBinding.inflate(inflate,parent,false)
            MensagensDestinatarioViewHolder(itemView)
        }

    }





    override fun getItemCount(): Int {
      return listaMensagens.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val mensagem = listaMensagens[position]
        when (holder){
            is MensagensDestinatarioViewHolder  -> holder.bind(mensagem)
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
    }
}
}
