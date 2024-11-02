package com.lucasmathews.whatsapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lucasmathews.whatsapp.databinding.ItemContatosBinding
import com.lucasmathews.whatsapp.model.Usuario
import com.squareup.picasso.Picasso


class ContatosAdapter(private val onClick:(Usuario)->Unit):Adapter<ContatosAdapter.ContatosViewHolder> (){
    private var listaContatos = emptyList<Usuario>()
    fun adicionarLista(list:List<Usuario>){
        listaContatos= list
        notifyDataSetChanged()
    }

    inner class  ContatosViewHolder(
        private val binding:ItemContatosBinding
    ):ViewHolder(binding.root){
        fun bind(usuario: Usuario){
            binding.txtContatoNome.text=   usuario.nome
            Picasso.get().load(usuario.foto).into(binding.imageContatoFoto)


            binding.clItemContato.setOnClickListener {
                onClick(usuario)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
       val itemView = ItemContatosBinding.inflate(inflater,parent,false)


        return  ContatosViewHolder(itemView)
    }

    override fun getItemCount(): Int {
      return listaContatos.size
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
      val usuario =   listaContatos[position]
        holder.bind(usuario)

    }
}