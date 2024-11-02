package com.lucasmathews.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lucasmathews.whatsapp.activities.MensagensActivity
import com.lucasmathews.whatsapp.adapters.ConversasAdapter
import com.lucasmathews.whatsapp.databinding.FragmentConversasBinding
import com.lucasmathews.whatsapp.model.Conversa
import com.lucasmathews.whatsapp.model.Usuario
import com.lucasmathews.whatsapp.util.Constantes
import com.lucasmathews.whatsapp.util.exibirMensagem


class ConversasFragment : Fragment() {
    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventoSnaShot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter
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
       binding= FragmentConversasBinding.inflate(inflater,container,false)

        conversasAdapter=ConversasAdapter{conversa->
            val intent = Intent(context,MensagensActivity::class.java)
            val usuario= Usuario(id=conversa.idUsuarioDestinatario,nome=conversa.nome,foto=conversa.foto)
            intent.putExtra(Constantes.DADOS_DESTINATARIO,usuario)
            Log.i("dados","${usuario.nome}")
/*            intent.putExtra("origem",Constantes.ORIGEM_CONVERSA)*/
            startActivity(intent)

        }
        binding.rvConversasFragment.adapter = conversasAdapter
        binding.rvConversasFragment.layoutManager =LinearLayoutManager(context)
        binding.rvConversasFragment.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))






        return binding.root


    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()

    }



    private fun adicionarListenerConversas() {
        val idUsuarioRemetente = auth.currentUser?.uid
        if (idUsuarioRemetente != null) {
         eventoSnaShot=   firestore.collection(Constantes.CONVERSA).document(idUsuarioRemetente)
                .collection(Constantes.ULTIMAS_CONVERSAS).orderBy("data",Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapShot, erro ->
                    if (erro != null) {
                        activity?.exibirMensagem("erro ao recuperar conversas")
                    }
                        val listaConversas = mutableListOf<Conversa>()
                        val documento = querySnapShot?.documents
                        documento?.forEach{documentSnapshot ->
                            val conversa =documentSnapshot.toObject(Conversa::class.java)
                            if(conversa!=null){
                                listaConversas.add(conversa)
                                Log.i("exibir_conversa","${conversa.nome}- ${conversa.ultimaMensagem}")
                            }

                        }
                        if(listaConversas.isNotEmpty()){
                         conversasAdapter.adicionarLista(listaConversas)
                        }

                    }

                }
        }


    override fun onDestroy() {
        super.onDestroy()
        eventoSnaShot.remove()
    }


    }


