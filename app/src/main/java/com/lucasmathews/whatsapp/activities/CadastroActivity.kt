package com.lucasmathews.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasmathews.whatsapp.databinding.ActivityCadastroBinding
import com.lucasmathews.whatsapp.model.Usuario
import com.lucasmathews.whatsapp.util.exibirMensagem

class CadastroActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarEventodeClique()
    }


    private fun inicializarEventodeClique() {

        binding.btnCadastrar.setOnClickListener {
            if (validarCampo()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful) {
                val idUsuario = it.result.user?.uid
                if(idUsuario != null){
                    val usuario = Usuario (idUsuario,nome,email)
                    salvarUsuario(usuario)
                }
                exibirMensagem("sucesso ao fazer seu cadastro")
                startActivity(Intent(this, MainActivity::class.java))



            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroErroEmail: FirebaseAuthInvalidCredentialsException) {
                erroErroEmail.printStackTrace()
                exibirMensagem("E-mail inválido, digite outro e-mail")
            } catch (erroColisao: FirebaseAuthUserCollisionException) {
                erroColisao.printStackTrace()
                exibirMensagem("E-mail já existente, digite outro e-mail")
            } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca, digite outra senha com letras e caracteres")
            }
        }
    }

    private fun salvarUsuario(usuario: Usuario) {
        database.collection("usuarios").document(usuario.id).set(usuario).addOnSuccessListener {
            exibirMensagem("sucesso ao cadastrar usuario")
        }.addOnFailureListener {
            exibirMensagem("erro ao cadastrar usuario")
        }

    }


    private fun validarCampo(): Boolean {
            nome = binding.EditNome.text.toString()
            senha = binding.EditSenha.text.toString()
            email = binding.EditEmail.text.toString()

            if (nome.isNotEmpty()) {
                binding.textInputNome.error = null

                if (email.isNotEmpty()) {
                    binding.textInputEmail.error = null
                    if (senha.isNotEmpty()) {
                        binding.textInputEmail.error = null
                        return true
                    } else {
                        binding.textInputSenha.error = "digite a senha corretamente!"
                        return false
                    }
                } else {
                    binding.textInputEmail.error = "digite o email corretamente!"
                    return false
                }

            } else {
                binding.textInputNome.error = "digite o nome corretamente!"
                return false
            }

        }

        private fun inicializarToolbar() {
            val toolbar = binding.includeToolbar.toolbarPrincipal
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                title = "Faça seu cadastro"
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }
