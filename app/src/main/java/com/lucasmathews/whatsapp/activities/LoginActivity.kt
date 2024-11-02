package com.lucasmathews.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.lucasmathews.whatsapp.databinding.ActivityLoginBinding
import com.lucasmathews.whatsapp.util.exibirMensagem

class LoginActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var email:String
    private lateinit var senha : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()

    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = auth.currentUser
        if(usuarioAtual!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.EditTextEmailLogin.text.toString()
        senha = binding.EditTextSenhaLogin.text.toString()
        if(email.isNotEmpty()){
            binding.textInputEmailLogin.error = null

            if(senha.isNotEmpty()){
                binding.textInputSenhaLogin.error = null
                return true
            }else {
                binding.textInputSenhaLogin.error = "digite sua senha corretamente!"
                return false
            }
        }else {
            binding.textInputEmailLogin.error = "digite seu e-mail corretamente!"
            return false
        }

    }

    private fun inicializarEventosClique() {
        binding.txtCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))


        }
        binding.btnLogar.setOnClickListener {
            if(validarCampos()){
             criarUsuario(email,senha)


            }
        }
    }

    private fun criarUsuario(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email,senha).addOnSuccessListener{
            exibirMensagem("logado com sucesso!!")
        startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener { error->
            try {
                throw error
            }catch (errorEmail:FirebaseAuthEmailException ){
                errorEmail.printStackTrace()
                exibirMensagem("e-mail errado!")
            }catch (errorUsuario:FirebaseAuthInvalidUserException) {
                errorUsuario.printStackTrace()
                exibirMensagem("usuario inválido!")

            }catch (errorCredenciais:FirebaseAuthInvalidCredentialsException){
                errorCredenciais.printStackTrace()
                exibirMensagem("Credenciais inválidas!!")
            }
        }


    }


}