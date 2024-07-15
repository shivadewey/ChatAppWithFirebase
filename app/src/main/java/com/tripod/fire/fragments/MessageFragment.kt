package com.tripod.fire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tripod.fire.adapter.UsersAdapter
import com.tripod.fire.databinding.FragmentMessageBinding
import com.tripod.fire.models.UserData

class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private var list: List<UserData>? = null

    private lateinit var usersAdapter: UsersAdapter
//    private var users: List<UserData>? = null

    private var users: MutableList<UserData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater)

        list = ArrayList()


        binding.userRecyclerView
            .setHasFixedSize(true)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(context)

        users = ArrayList()

        retrieveAllUsers()



        return binding.root
    }

    private fun retrieveAllUsers() {
        binding.progressBar.isVisible = true
        var currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        var refUserId = FirebaseDatabase.getInstance().reference.child("Chats")


        refUserId.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.isVisible = false
                users?.clear()

                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        if (snap.hasChild(currentUserId)) {
                            val otherUserId = snap.key

                            if (otherUserId != null) {
                                binding.progressBar.isVisible = false
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(otherUserId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(userSnapshot: DataSnapshot) {

                                            if(userSnapshot.exists()){
                                                val user: UserData? =
                                                    userSnapshot.getValue(UserData::class.java)
                                                user?.let {
                                                    users?.add(it)
                                                    usersAdapter =
                                                        UsersAdapter(context!!, users!!, false)
                                                    binding.userRecyclerView.adapter = usersAdapter
                                                    checkIfEmpty()
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            binding.progressBar.isVisible = false
                                        }

                                    })
                            }
                        }
                    }

                }else{
                    checkIfEmpty()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.isVisible = false
            }

        })
    }

    private fun checkIfEmpty() {
        if (users.isNullOrEmpty()) {
            binding.noMessage.visibility = View.VISIBLE
        } else {
            binding.noMessage.visibility = View.GONE
        }
    }

}