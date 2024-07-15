package com.tripod.fire.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tripod.fire.R
import com.tripod.fire.adapter.UsersAdapter
import com.tripod.fire.databinding.FragmentSearchBinding
import com.tripod.fire.models.UserData
import java.util.Locale


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var usersAdapter: UsersAdapter
    private var users:List<UserData>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSearchBinding.inflate(layoutInflater)

        binding.searchUserRecyclerView
            .setHasFixedSize(true)

        binding.searchUserRecyclerView.layoutManager=LinearLayoutManager(context)


        users=ArrayList()

        retrieveAllUsers()

        binding.edtSearchUser.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               if(p0.toString().isNullOrBlank()){
                   retrieveAllUsers()
               }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUser(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNullOrBlank()){
                    retrieveAllUsers()
                }
            }

        })


        return binding.root
    }


    private fun retrieveAllUsers(){
        var currentUserId=FirebaseAuth.getInstance().currentUser?.uid ?:""
        var refUserId=FirebaseDatabase.getInstance().reference.child("Users")

        refUserId.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (users as ArrayList<UserData>).clear()

                if(snapshot.exists()){
                    if(binding.edtSearchUser.text.toString().isEmpty()){
                        for(snap in snapshot.children){
                            val user=snap.getValue(UserData::class.java)
                            user?.let {
                                if(!(it.uid).equals(currentUserId)){
                                    (users as ArrayList<UserData>).add(it)
                                }
                            }

                        }
                        usersAdapter= UsersAdapter(context!!,users!!,false)
                        binding.searchUserRecyclerView.adapter=usersAdapter
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun searchUser(str:String){
        var currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        var searchQuery=FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("name")
            .startAt(str.uppercase())
            .endAt(str.lowercase()+"\uf8ff")

        searchQuery.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (users as ArrayList<UserData>).clear()
                for(snap in snapshot.children){
                    val user:UserData?=snap.getValue(UserData::class.java)

                    if(!(user!!.uid).equals(currentUserId)){
                        (users as ArrayList<UserData>).add(user)
                    }
                }
                if(str.isNotEmpty()){
                    usersAdapter= UsersAdapter(context!!,users!!,false)
                    binding.searchUserRecyclerView.adapter=usersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}