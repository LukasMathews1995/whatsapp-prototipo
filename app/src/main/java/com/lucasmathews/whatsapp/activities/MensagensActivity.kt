package com.lucasmathews.whatsapp.activities


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lucasmathews.whatsapp.adapters.MensagensAdapter
import com.lucasmathews.whatsapp.databinding.ActivityMensagensBinding
import com.lucasmathews.whatsapp.model.Conversa
import com.lucasmathews.whatsapp.model.Mensagem
import com.lucasmathews.whatsapp.model.Usuario
import com.lucasmathews.whatsapp.util.Constantes
import com.lucasmathews.whatsapp.util.exibirMensagem
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)

    }
    private val auth by lazy{
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var dadosDestinatario:Usuario? = null
    private var dadosUsuarioRemetente:Usuario?=null
    private lateinit var snapShotListener: ListenerRegistration
    private lateinit var mensagensAdapter : MensagensAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        recuperarDadosUsuario()
        inicializarToolbar()
        inicializarEventosClique()
        inicializarRecyclerView()
        inicializarListener()


    }

    private fun inicializarRecyclerView() {
    with(binding){
         mensagensAdapter = MensagensAdapter()
        rvConversas.adapter = mensagensAdapter
        rvConversas.layoutManager = LinearLayoutManager(applicationContext)

    }
    }

    override fun onStart() {
        super.onStart()
        inicializarListener()
    }

    private fun inicializarListener() {
        val idUsuarioRemetente= auth.currentUser?.uid.toString()
        val idUsuarioDestinatario =  dadosDestinatario?.id.toString()

        if(idUsuarioRemetente!=null && idUsuarioDestinatario!=null) {
           snapShotListener = firestore.collection(Constantes.COLECAO_MENSAGENS).document(idUsuarioRemetente).collection(idUsuarioDestinatario)
                .orderBy("data",Query.Direction.ASCENDING).addSnapshotListener{ querySnapShot, erro->
                    if(erro!=null){
                        exibirMensagem("erro ao recuperar mensagem")
                    }
                   val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapShot?.documents
                   documentos?.forEach {documentSnapShot->
                      val mensagem = documentSnapShot.toObject(Mensagem::class.java)
                       if(mensagem!=null){
                            listaMensagens.add(mensagem)

                       }
                   }
                   if(listaMensagens.isNotEmpty()){
                    mensagensAdapter.adicionarLista(listaMensagens)
                   }
                }

        }

    }

    private fun inicializarEventosClique() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editTextConversa.text.toString()

           salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        if(textoMensagem.isNotEmpty()){
        val idUsuarioRemetente= auth.currentUser?.uid.toString()
       val idUsuarioDestinatario =  dadosDestinatario?.id.toString()

        if(idUsuarioRemetente!=null && idUsuarioDestinatario!=null) {
            val mensagem = Mensagem(idUsuarioRemetente, textoMensagem)
            //Salvar para o remetente
            salvarMensagemFireStore(idUsuarioRemetente,idUsuarioDestinatario,mensagem)
                // foto e nome Destinatario
            val conversaRemetente = Conversa(idUsuarioRemetente,idUsuarioDestinatario,dadosDestinatario!!.foto,dadosDestinatario!!.nome,textoMensagem)
            salvarConversaFirestore (conversaRemetente)

            //salvar para o destinatario
            salvarMensagemFireStore(idUsuarioDestinatario,idUsuarioRemetente,mensagem)
            // foto e nome remetente
            val conversaDestinatario = Conversa(idUsuarioDestinatario,idUsuarioRemetente,dadosUsuarioRemetente!!.foto,dadosUsuarioRemetente!!.nome,textoMensagem)
            salvarConversaFirestore (conversaDestinatario)
        }}
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore.collection(Constantes.CONVERSA).document(conversa.idUsuarioRemetente).collection(Constantes.ULTIMAS_CONVERSAS).document(conversa.idUsuarioDestinatario).set(conversa)
            .addOnFailureListener {
                exibirMensagem("erro ao salvar conversa")
            }

    }

    private fun salvarMensagemFireStore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {
        firestore.collection(Constantes.COLECAO_MENSAGENS).document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario).add(mensagem).addOnFailureListener {
                exibirMensagem("Mensagem nao enviada")
            }

    }

    private fun inicializarToolbar() {
     val toolbar = binding.tbMensagens

        setSupportActionBar(toolbar)
        supportActionBar?.apply {

            title=""
            if(dadosDestinatario!=null){
                configurarView()
            setDisplayHomeAsUpEnabled(true)}
        }
            }

    private fun configurarView() {

     Picasso.get().load(dadosDestinatario?.foto).into(binding.imageFotoPerfil)
        binding.textNomeConversa.text = dadosDestinatario?.nome
    }


    private fun recuperarDadosUsuario() {
        val idUsuarioLogado = auth.currentUser?.uid
        if(idUsuarioLogado!=null) {
            firestore.collection(Constantes.USUARIOS).document(idUsuarioLogado).get().addOnSuccessListener {documentSnapShot->

              val usuario = documentSnapShot.toObject(Usuario::class.java)
                if(usuario!=null){
                dadosUsuarioRemetente = usuario

                }
            }

        }

        val extras = intent.extras

        if(extras!=null){
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               dadosDestinatario= extras.getParcelable(Constantes.DADOS_DESTINATARIO,Usuario::class.java)
            Log.i("dados","${dadosDestinatario?.nome}")

            }else {
               dadosDestinatario= extras.getParcelable(Constantes.DADOS_DESTINATARIO)


        }
    }


}
}