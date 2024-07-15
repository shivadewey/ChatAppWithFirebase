package com.tripod.fire.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.tripod.fire.R
import com.tripod.fire.adapter.ChatAdapter
import com.tripod.fire.databinding.FragmentChatBinding
import com.tripod.fire.models.MessageModel
import com.tripod.fire.models.UserData
import com.tripod.fire.notification.NotificationData
import com.tripod.fire.notification.PushNotification
import com.tripod.fire.notification.api.ApiUtilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private var db = FirebaseDatabase.getInstance()
    private lateinit var list: ArrayList<MessageModel>

    private var senderID: String? = ""
    private var receiverID: String? = ""


    private var isTyping: Boolean = false
    private lateinit var typingEventListener: ValueEventListener

    private lateinit var senderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        senderID = FirebaseAuth.getInstance().currentUser!!.uid
        receiverID = arguments?.getString("visit_id").toString()

        list = ArrayList()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater)



        setUser(receiverID!!)

        setSizeOfDrawableIcon()




        binding.btnSend.setOnClickListener {
            if (binding.edtMessage.text.toString().isNullOrBlank()) {

            } else {
                sendMessage()
            }
        }


        binding.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isTyping = p0?.isNotBlank() == true
                updateUserTyping(isTyping)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        setMessage()

        return binding.root
    }

    private fun updateUserTyping(typing: Boolean) {
        db.reference.child("Users")
            .child(receiverID!!)
            .child("typing")
            .setValue(typing)


    }

    private fun setSizeOfDrawableIcon() {
        val editText = binding.edtMessage
        val drawable = resources.getDrawable(R.drawable.ic_attach)
        val sizeInPixels =
            resources.getDimensionPixelSize(R.dimen.custom_icon_size) // replace with your desired size
        drawable.setBounds(0, 0, sizeInPixels, sizeInPixels)
        editText.setCompoundDrawables(drawable, null, null, null)
    }

    private fun setMessage() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.messageRecyclerView.layoutManager = layoutManager

        db.reference.child("Chats")
            .child(senderID!!)
            .child(receiverID!!)
            .child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val data = snap.getValue(MessageModel::class.java)
                            list.add(data!!)
                        }
                    }
                    binding.messageRecyclerView.adapter = ChatAdapter(list)

                    layoutManager.scrollToPositionWithOffset(list.size - 1, 0)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun sendMessage() {
        val message =
            MessageModel(senderID, receiverID, binding.edtMessage.text.toString(), Date().time)
        val randomKey = db.reference.push().key

        //Extract name of sender
        val data = db.getReference("Users")
            .child(senderID!!)
        data.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    user?.let {
                        senderName = it.name.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        db.reference.child("Chats")
            .child(senderID!!)
            .child(receiverID!!)
            .child("message")
            .child(randomKey!!)
            .setValue(message)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendNotification(binding.edtMessage.text.toString())
                    binding.edtMessage.setText("")
                    db.reference.child("Chats")
                        .child(receiverID!!)
                        .child(senderID!!)
                        .child("message")
                        .child(randomKey!!)
                        .setValue(message)
                        .addOnSuccessListener {

                        }
                }
            }
    }


    private fun sendNotification(message: String) {
        val database = db.getReference("Users")
            .child(receiverID!!)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    val notificationData = PushNotification(
                        NotificationData("${senderName}", message),
                        user!!.fcmToken
                    )
                    ApiUtilities.getApiInstance().sendNotification(notificationData)
                        .enqueue(object : Callback<PushNotification> {
                            override fun onResponse(
                                call: Call<PushNotification>,
                                response: Response<PushNotification>
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "Notification send",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Notification Not send",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        })
                    user.let {
                        binding.particularUserName.text = it!!.name

                        if (it.online) {
                            binding.userStatus.text = "online"
                        } else {
                            val lastSeen = FormatLastSeen(it.lastSeen!!)
                            binding.userStatus.text = "Last seen $lastSeen"
                        }
                    }
                    db.reference.child("Users").child(senderID!!)
                        .child("typing").addValueEventListener(typingEventListener)


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


    private fun setUser(receiverID: String) {
        val database = db.getReference("Users")
            .child(receiverID)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    user.let {
                        binding.particularUserName.text = it!!.name

                        if (it.online) {
                            binding.userStatus.text = "online"

                        } else {
                            val lastSeen = FormatLastSeen(it.lastSeen!!)
                            binding.userStatus.text = "Last seen $lastSeen"
                        }
                    }
                    db.reference.child("Users").child(senderID!!)
                        .child("typing").addValueEventListener(typingEventListener)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        typingEventListener = object : ValueEventListener {
            override fun onDataChange(typingSnapshot: DataSnapshot) {
                val senderIsTyping = typingSnapshot.getValue(Boolean::class.java) ?: false
                updateReceiverTypingStatus(senderIsTyping)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
    }

    private fun updateReceiverTypingStatus(senderIsTyping: Boolean) {
        if (senderIsTyping) {
            binding.userStatus.text = "typing..."
        } else {
            val userRef = db.getReference("Users").child(receiverID!!)
            userRef.child("lastSeen").onDisconnect().setValue(ServerValue.TIMESTAMP)
            userRef.child("online").onDisconnect().setValue(false)

            val database = db.getReference("Users")
                .child(receiverID!!)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserData::class.java)
                        user.let {
                            binding.particularUserName.text = it!!.name

                            if (it.online) {
                                binding.userStatus.text = "online"
                            } else {
                                val lastSeen = FormatLastSeen(it.lastSeen!!)
                                binding.userStatus.text = "Last seen $lastSeen"
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }
    }

    private fun FormatLastSeen(lastSeen: Long): String {
        val dateFormat = SimpleDateFormat("d MMM yy hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(lastSeen))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}