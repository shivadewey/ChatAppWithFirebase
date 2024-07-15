package com.tripod.fire.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.tripod.fire.R
import com.tripod.fire.adapter.ViewPagerAdapter
import com.tripod.fire.databinding.FragmentTabContainBinding
import com.tripod.fire.models.UserData


class TabContainFragment : Fragment() {
    private lateinit var binding: FragmentTabContainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var db=FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentTabContainBinding.inflate(layoutInflater)
        var list=ArrayList<Fragment>()
        var title=ArrayList<String>()

        list.add(MessageFragment())
        list.add(SearchFragment())

        title.add("Message")
        title.add("Search")


        viewPagerAdapter= ViewPagerAdapter(this,list,title)
        binding.viewPager.adapter=viewPagerAdapter

        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            tab.text=title[position]
        }.attach()

        binding.dots.setOnClickListener {
            showPopUpMenu()
        }

        binding.userImage.setOnClickListener {
            findNavController().navigate(R.id.action_tabContainFragment_to_settingsFragment)
        }

        setUserName()

        return binding.root
    }

    private fun setUserName() {
          var database=db.getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
          database.addListenerForSingleValueEvent(object :ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  if(snapshot.exists()){
                      val user=snapshot.getValue(UserData::class.java)
                      binding.userName.text=user!!.name
                  }
              }

              override fun onCancelled(error: DatabaseError) {
                  binding.userName.text=""
                  Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
              }

          })
    }

    private fun showPopUpMenu() {
        val popUpMenu=PopupMenu(context,binding.dots)
        popUpMenu.menuInflater.inflate(R.menu.popup_menu,popUpMenu.menu)
        
        popUpMenu.setOnMenuItemClickListener { 
            when(it.itemId){
                R.id.account ->{
                    findNavController().navigate(R.id.action_tabContainFragment_to_settingsFragment)
                    true
                }
                R.id.privacy ->{
                    Toast.makeText(context,"Privacy",Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.help ->{
                    Toast.makeText(context,"Help",Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.logout ->{
                    alertLogoutMenu()
                    true
                }
                
                else -> false
            }
        }
        popUpMenu.show()
    }

    private fun alertLogoutMenu() {
        val builder=AlertDialog.Builder(requireContext())
        val alterDialog=builder.create()
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes"){_,_->
                val userRef=db.getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                userRef.child("online").setValue(false)
                userRef.child("lastSeen").setValue(ServerValue.TIMESTAMP)
                findNavController().navigate(R.id.action_tabContainFragment_to_loginFragment)
                Firebase.auth.signOut()

            }
            .setNegativeButton("No"){_,_->
                alterDialog.dismiss()
            }
            .show()
            .setCancelable(false)

    }

    companion object {

    }
}