package com.tripod.fire.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.tripod.fire.R
import com.tripod.fire.adapter.ViewPagerAdapter
import com.tripod.fire.databinding.ActivityMainBinding
import com.tripod.fire.fragments.MessageFragment
import com.tripod.fire.fragments.SearchFragment
import com.tripod.fire.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Tab Layout Setup
//        var list=ArrayList<Fragment>()  // List for store fragments
//        var title=ArrayList<String>()  // List for store title of the fragments
//
//        //add fragments and title in list
//
//        list.add(MessageFragment())
//        list.add(SearchFragment())
//        list.add(SettingsFragment())
//
//        title.add("Message")
//        title.add("Search")
//        title.add("Settings")
//
//        // Call View Pager Adapter
//        viewPagerAdapter= ViewPagerAdapter(this@MainActivity,list,title)
//        binding.viewPager.adapter=viewPagerAdapter
//
//        // Setup Tab Layout functions
//        TabLayoutMediator(binding.tabs,binding.viewPager){tab,position->
//             tab.text=title[position]
//        }.attach()
//
//        // ClickListener to PopUpMenu
//        binding.dots.setOnClickListener {
//            showMenu()
//        }

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.popup_menu,menu)
//        return true
//    }

//    private fun showMenu() {
//        val popUpMenu=PopupMenu(this,binding.dots)
//        popUpMenu.menuInflater.inflate(R.menu.popup_menu,popUpMenu.menu)
//
//        popUpMenu.setOnMenuItemClickListener {
//            when(it.itemId){
//                R.id.account ->{
//                    Toast.makeText(this,"Account",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.privacy ->{
//                    Toast.makeText(this,"Privacy",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.help ->{
//                    Toast.makeText(this,"Help",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                 R.id.logout ->{
//                     Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show()
//                     true
//                 }
//
//                else -> false
//            }
//        }
//        popUpMenu.show()
//    }
}