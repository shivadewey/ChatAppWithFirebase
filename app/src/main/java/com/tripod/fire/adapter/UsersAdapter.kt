package com.tripod.fire.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tripod.fire.R
import com.tripod.fire.models.UserData
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(
    val context: Context,
    private val userList:List<UserData>,
    isChatCheck:Boolean
):RecyclerView.Adapter<UsersAdapter.UserViewHolder>(){
    inner class UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val userProfile:CircleImageView=itemView.findViewById(R.id.userProfile)
        val userName:TextView=itemView.findViewById(R.id.userProfileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.user_design_layout,parent,false)
        return  UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        if (userList[position].userImage != null && userList[position].userImage!!.isNotEmpty()) {

            Glide.with(holder.itemView)
                .load(userList[position].userImage)
                .into(holder.userProfile)
        } else {
            holder.userProfile.setImageResource(R.drawable.ic_profile)
        }

        holder.userName.text=userList[position].name

        holder.itemView.setOnClickListener {

            val options= arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val builder:AlertDialog.Builder=AlertDialog.Builder(context)
            builder.setTitle("Select One")
            builder.setIcon(R.drawable.ic_profile)
            builder.setItems(options,DialogInterface.OnClickListener { dialogInterface, i ->
                if(i==0){
                    val bundle= bundleOf("visit_id" to userList[position].uid)
                    Navigation.findNavController(holder.itemView)
                        .navigate(R.id.action_tabContainFragment_to_chatFragment,bundle)
                }
                if(i==1){
                    val bundle= bundleOf("visit_id" to userList[position].uid)
                    Navigation.findNavController(holder.itemView)
                        .navigate(R.id.action_tabContainFragment_to_userProfileFragment,bundle)
                }
            })
            val alertDialog:AlertDialog=builder.create()
            alertDialog.show()
        }
    }

}