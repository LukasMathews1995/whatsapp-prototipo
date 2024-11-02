package com.lucasmathews.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import com.lucasmathews.whatsapp.activities.MensagensActivity


import com.lucasmathews.whatsapp.adapters.ContatosAdapter

import com.lucasmathews.whatsapp.databinding.FragmentContatosBinding
import com.lucasmathews.whatsapp.model.Usuario
import com.lucasmathews.whatsapp.util.Constantes
import com.lucasmathews.whatsapp.util.USUARIOS


class ContatosFragment : Fragment() {
    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapShot: ListenerRegistration
    private lateinit var contatosAdapter : ContatosAdapter
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            binding= FragmentContatosBinding.inflate(inflater,container,false)

       contatosAdapter = ContatosAdapter{usuario ->
            val intent = Intent(context, MensagensActivity::class.java)

            intent.putExtra(Constantes.DADOS_DESTINATARIO, usuario)

           startActivity(intent)


        }
      binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))

        return binding.root

    }

    override fun onStart() {
        super.onStart()
        adicionarListenerUsuarios()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapShot.remove()

    }

    private fun adicionarListenerUsuarios() {
        val idUsuario = auth.currentUser?.uid
        if(idUsuario!=null){
           eventoSnapShot =  firestore.collection(USUARIOS).addSnapshotListener{ querySnapShot, erro->
               val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapShot?.documents
                documentos?.forEach {documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    val idUsuarioLogado = auth.currentUser?.uid
                    if(idUsuarioLogado!=null && usuario!=null){


                        if( usuario.id!= idUsuarioLogado) {
                            listaContatos.add(usuario)
                        }

                    }
                }
               if(listaContatos.isNotEmpty()) {
                   contatosAdapter.adicionarLista(listaContatos)

               }

            }

        }
    }
}
