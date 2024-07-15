package com.tripod.fire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.tripod.fire.R
import com.tripod.fire.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
     private lateinit var binding: FragmentLoginBinding
     private var auth=FirebaseAuth.getInstance()
    private var db=FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentLoginBinding.inflate(layoutInflater)

        checkUserAuth()
        goToTabContainer()
        goToRegister()

        return binding.root
    }

    private fun checkUserAuth() {
        if(auth.currentUser!=null){
                findNavController().navigate(R.id.action_loginFragment_to_tabContainFragment)
            }
    }

    private fun goToRegister() {
        binding.txtRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signinFragment)
        }
    }

    private fun goToTabContainer() {
        binding.btnLogin.setOnClickListener {
            if(binding.edtEmail.text.toString().isEmpty()){
                binding.edtEmail.error="wrong format of the email"
            }else if(binding.edtPassword.text.toString().isEmpty()){
                binding.edtPassword.error="wrong password"
            }else{
                logInUser(binding.edtEmail.text.toString(),binding.edtPassword.text.toString())
            }
        }
    }

    private fun logInUser(email: String, password: String) {
         auth.signInWithEmailAndPassword(email,password)
             .addOnCompleteListener {task->
                 if(task.isSuccessful){
                     val userRef=db.getReference("Users").child(auth.currentUser!!.uid)
                     userRef.child("online").setValue(true)
                     userRef.child("lastSeen").onDisconnect().setValue(ServerValue.TIMESTAMP)
                     userRef.child("online").onDisconnect().setValue(false)
                     findNavController().navigate(R.id.action_loginFragment_to_tabContainFragment)
                 }else{
                     Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show()
                 }
             }
    }

    companion object {

    }
}