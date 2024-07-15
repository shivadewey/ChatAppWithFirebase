package com.tripod.fire.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.tripod.fire.R
import com.tripod.fire.databinding.ReceiverItemLayoutBinding
import com.tripod.fire.databinding.SenderItemLayoutBinding
import com.tripod.fire.models.MessageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private var list:ArrayList<MessageModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var ITEM_SENT=0
    var ITEM_REVEIVED=1

    inner class SendViewHolder(view:View):RecyclerView.ViewHolder(view){
        var binding:SenderItemLayoutBinding=SenderItemLayoutBinding.bind(view)
    }

    inner class ReceivedViewHolder(view:View):RecyclerView.ViewHolder(view){
        var binding:ReceiverItemLayoutBinding=ReceiverItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType==ITEM_SENT){
             SendViewHolder(
                 LayoutInflater.from(parent.context).inflate(R.layout.sender_item_layout,parent,false)
             )
        }else{
            ReceivedViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.receiver_item_layout,parent,false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(FirebaseAuth.getInstance().currentUser!!.uid==list[position].senderID) ITEM_SENT else ITEM_REVEIVED
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var message=list[position]
        if(holder.itemViewType==ITEM_SENT){
            var viewHolder=holder as SendViewHolder
            viewHolder.binding.sentMessage.text=message.message
            viewHolder.binding.sendMessageTime.text=message.time?.let { TimeFormat(it) }
        }else{
            var viewHolder=holder as ReceivedViewHolder
            viewHolder.binding.receiveMessage.text=message.message
            viewHolder.binding.receivedMessageTime.text= message.time?.let { TimeFormat(it) }
        }
    }


    private  fun TimeFormat(time:Long):String{
        val dateFormat=SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(time))
    }

}