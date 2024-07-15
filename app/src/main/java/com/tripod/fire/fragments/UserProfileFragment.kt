package com.tripod.fire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tripod.fire.R
import com.tripod.fire.databinding.FragmentUserProfileBinding
import com.tripod.fire.models.UserData

class UserProfileFragment : Fragment() {
    private lateinit var binding:FragmentUserProfileBinding
    private var uid:String?=""
    private var db=FirebaseDatabase.getInstance()
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
        binding= FragmentUserProfileBinding.inflate(layoutInflater)
        uid=arguments?.getString("visit_id").toString()
        setProfile(uid!!)
        return binding.root
    }

    private fun setProfile(uid: String) {
       val database=db.getReference("Users")
           .child(uid)

       database.addListenerForSingleValueEvent(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   val user=snapshot.getValue(UserData::class.java)
                   binding.profileName.text=user!!.name
                   binding.profileEmail.text=user.email
                   binding.particularUserName.text="${user.name}'s Profile"
               }
           }

           override fun onCancelled(error: DatabaseError) {
           }

       })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }


}