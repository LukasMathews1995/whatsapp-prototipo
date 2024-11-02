package com.lucasmathews.whatsapp.activities

import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lucasmathews.whatsapp.databinding.ActivityPerfilBinding
import com.lucasmathews.whatsapp.util.Constantes
import com.lucasmathews.whatsapp.util.exibirMensagem
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private val storage by lazy{
        FirebaseStorage.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var temPermissoesCamera = false
    private var temPermissoesGaleria = false


    private val gerenciadorGaleria =  registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        if(uri!=null){
            binding.imagePerfil.setImageURI(uri)
            uploadImageStorage(uri)

        }else {
        exibirMensagem("nenhuma imagem selecionada")
        }


    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToolbar()
            solicitarPermissoes()
        inicializarEventosClique()

    }
    private fun uploadImageStorage(uri: Uri) {
        val idUsuario = auth.currentUser?.uid
        if(idUsuario!=null){
            storage.getReference("fotos").child("usuarios").child(idUsuario).child("perfil.jpg").putFile(uri).addOnSuccessListener {task->
                exibirMensagem("sucesso ao fazer o upload da imagem")
               task.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->

                    val dados = mapOf("foto" to uri.toString())


                    atualizarDados(idUsuario,dados)


                }
                    ?.addOnFailureListener {  }

            }.addOnFailureListener {
                exibirMensagem("erro ao fazer o upload da imagem")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuarios()
    }
    private fun atualizarDados(idUsuario: String, dados: Map<String, String>) {
        firestore.collection(Constantes.USUARIOS).document(idUsuario).update(dados).addOnSuccessListener {
            exibirMensagem("Sucesso ao atualizar perfil")
        }.addOnFailureListener {
            exibirMensagem("erro ao atualizar perfil")
        }
    }


    private fun recuperarDadosIniciaisUsuarios() {
        val idUsuario = auth.currentUser?.uid
        if(idUsuario!=null){
        firestore.collection("usuarios").document(idUsuario).get().addOnSuccessListener {documentSnapShot->

        val dadosUsuario = documentSnapShot?.data

            if(dadosUsuario!=null){
                val nome = dadosUsuario["nome"]as String
                val foto = dadosUsuario["foto"]as String

                binding.EditTextPerfilNome.setText(nome)
                if(foto.isNotEmpty()){
                    Picasso.get().load(foto).into(binding.imagePerfil)
                }

            }
        }

        }
    }

    private fun inicializarEventosClique() {
        binding.fabImagePerfil.setOnClickListener {
            if(temPermissoesGaleria){
        gerenciadorGaleria.launch("image/*")

            }else{
                exibirMensagem("nao tem permissão")
                solicitarPermissoes()
            }
        }
        binding.btnAtualizarPerfil.setOnClickListener {
            val nomeUsuario = binding.EditTextPerfilNome.text.toString()

            if(nomeUsuario.isNotEmpty()){
                val idUsuario = auth.currentUser?.uid
                if(idUsuario!=null){
                val dados = mapOf("nome" to nomeUsuario)

               atualizarDados(idUsuario,dados)}
            }else {
                exibirMensagem("erro ao fazer a atualizacao")
            }
        }

    }


    private fun solicitarPermissoes() {
        temPermissoesCamera = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissoesGaleria = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
        //LISTA DE Permissoes negadas
        val permissoesNegadas = mutableListOf<String>()

        if (!temPermissoesCamera) {
            permissoesNegadas.add(android.Manifest.permission.CAMERA)

        }
        if (!temPermissoesGaleria) {
            permissoesNegadas.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
        //solicitar multiplas permissoes
        if (permissoesNegadas.isNotEmpty()) {
            val gerenciadorDePermissoes =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission: Map<String, Boolean> ->

                    Log.i("novas permissoes", " permissoes: $permission ")
                    temPermissoesCamera =
                        permission[android.Manifest.permission.CAMERA] ?: temPermissoesCamera
                    temPermissoesGaleria = permission[android.Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissoesGaleria
                }
            gerenciadorDePermissoes.launch(permissoesNegadas.toTypedArray())
        }
        }




    private fun inicializarToolbar() {
        val toolbar = binding.toolbarPerfil.toolbarPrincipal
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title= "Perfil"
            setDisplayHomeAsUpEnabled(true)

        }
    }
}