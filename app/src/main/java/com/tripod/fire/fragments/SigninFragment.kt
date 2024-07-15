package com.tripod.fire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.tripod.fire.R
import com.tripod.fire.databinding.FragmentSigninBinding
import com.tripod.fire.models.UserData

class SigninFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSigninBinding.inflate(layoutInflater)
        goToTabContainer()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private fun goToTabContainer() {
        binding.btnSignIn.setOnClickListener {
            if (binding.edtName.text.toString().isEmpty()) {
                binding.edtName.error = "name required"
            } else if (binding.edtEmail.text.toString().isEmpty()) {
                binding.edtEmail.error = "wrong format of the email"
            } else if (binding.edtPassword.text.toString().isEmpty()) {
                binding.edtPassword.error = "wrong password"
            } else if (!binding.edtConfirmPassword.text.toString()
                    .equals(binding.edtPassword.text.toString())
            ) {
                binding.edtConfirmPassword.error = "password doesn't match"
            } else {
                createUser(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
            }
        }
    }

    private fun createUser(email: String, password: String) {
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                val token = task.result
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val db = database.getReference("Users")
                                .child(auth.currentUser!!.uid)
                            db.setValue(
                                UserData(
                                    uid = FirebaseAuth.getInstance().currentUser!!.uid,
                                    name = binding.edtName.text.toString(),
                                    email = email,
                                    password = password,
                                    fcmToken = token
                                )
                            )
                            findNavController().navigate(R.id.action_signinFragment_to_tabContainFragment)
                        }
                    }
            }
    }

    companion object {

    }
}