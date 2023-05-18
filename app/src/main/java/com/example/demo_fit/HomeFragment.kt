package com.example.demo_fit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.demo_fit.databinding.FragmentHomeBinding
import com.example.demo_fit.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment(), FragmentAux {
    private lateinit var mActiveFragment: Fragment
    private var mFragmentManager: FragmentManager? = null
    lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mSnapshotsRef: DatabaseReference
    private lateinit var mImagesRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebase()
        setupAdapter()
        setupRecyclerView()
    }
    private fun setupFirebase() {
        mSnapshotsRef = FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
        mImagesRef = FirebaseDatabase.getInstance().getReference("Imagenes")
    }
    private fun setupAdapter() {
        val query = mSnapshotsRef
        val options = FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query) {
            val snapshot = it.getValue(Snapshot::class.java)
            snapshot!!.id = it.key!!
            snapshot
        }.build()

        mFirebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options) {
            private lateinit var mContext: Context

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext = parent.context

                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_snapshot, parent, false)
                return SnapshotHolder(view)
            }

            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)
                val userUid = snapshot.ownerUid // assuming 'ownerUid' is the UID of the user who created the snapshot


                with(holder) {
                    setListener(snapshot)

                    with(binding) {
                        //
                        mImagesRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val imageUrl = dataSnapshot.child("image").getValue(String::class.java)
                                    Glide.with(mContext)
                                        .load(imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .centerCrop()
                                        .into(binding.siPhotoProfile)
                                } else {
                                    Glide.with(mContext)
                                        .load(R.drawable.ic_profile) // default image from the drawable folder
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .centerCrop()
                                        .into(binding.siPhotoProfile)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // handle error
                            }
                        })

                        //
                        tvTitle.text = snapshot.title
                        cbLike.text = snapshot.likeList.keys.size.toString()
                        tvUserName.text = snapshot.userName//SnapshotsApplication.currentUser.displayName.toString()
                        cbLike.isChecked = snapshot.likeList
                            .containsKey(SnapshotsApplication.currentUser.uid)

                        Glide.with(mContext)
                            .load(snapshot.photoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(imgPhoto as ImageView)

                        btnDelete.visibility = if (model.ownerUid == SnapshotsApplication.currentUser.uid){
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
                        // Cargar y mostrar los comentarios
                        mSnapshotsRef.child(snapshot.id).child("comments")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val commentsList = dataSnapshot.children.map { commentSnapshot ->
                                        commentSnapshot.getValue() as Map<String, String>
                                    }
                                    val commentsAdapter = CommentsAdapter(commentsList)
                                    commentsRecyclerView.adapter = commentsAdapter
                                    commentsRecyclerView.layoutManager = LinearLayoutManager(mContext)
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Manejar errores
                                }}
                            )

                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE
                notifyDataSetChanged()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                //Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show()
                Snackbar.make(mBinding.root, error.message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        mLayoutManager = LinearLayoutManager(context)

        if (mLayoutManager is LinearLayoutManager) {
            (mLayoutManager as LinearLayoutManager).reverseLayout = true
            (mLayoutManager as LinearLayoutManager).stackFromEnd = true
        }

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFirebaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        mFirebaseAdapter.stopListening()
    }
    private fun deleteSnapshot(snapshot: Snapshot) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                    val storageSnapshotsRef = FirebaseStorage.getInstance().reference
                        .child(SnapshotsApplication.PATH_SNAPSHOTS)
                        .child(SnapshotsApplication.currentUser.uid)
                        .child(snapshot.id)
                    storageSnapshotsRef.delete().addOnCompleteListener { result ->
                        if (result.isSuccessful){
                            mSnapshotsRef.child(snapshot.id).removeValue()
                        } else {
                            Snackbar.make(mBinding.root, getString(R.string.home_delete_photo_error),
                                Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton(R.string.dialog_delete_cancel, null)
                .show()
        }
    }
    private fun setLike(snapshot: Snapshot, checked: Boolean) {
        val myUserRef = mSnapshotsRef.child(snapshot.id)
            .child(SnapshotsApplication.PROPERTY_LIKE_LIST)
            .child(SnapshotsApplication.currentUser.uid)

        if (checked) {
            myUserRef.setValue(checked)
        } else {
            myUserRef.setValue(null)
        }
    }
    override fun refresh() {
        mBinding.recyclerView.smoothScrollToPosition(0)
    }
    inner class SnapshotHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSnapshotBinding.bind(view)
        fun setListener(snapshot: Snapshot) {
            with(binding) {
                btnDelete.setOnClickListener { deleteSnapshot(snapshot) }

                cbLike.setOnCheckedChangeListener { _, checked ->
                    setLike(snapshot, checked)
                }

                btnSendComment.setOnClickListener {
                    val comment = etComment.text.toString().trim()
                    if (comment.isNotEmpty()) {
                        sendComment(snapshot, comment)
                        etComment.text.clear()
                    } else {
                        Snackbar.make(binding.root, "Por favor, escribe un comentario.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    inner class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCommentUserName: TextView = view.findViewById(R.id.tvCommentUserName)
        val tvCommentText: TextView = view.findViewById(R.id.tvCommentText)
    }

    inner class CommentsAdapter(private val comments: List<Map<String, String>>) :
        RecyclerView.Adapter<CommentHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
            return CommentHolder(view)
        }

        override fun onBindViewHolder(holder: CommentHolder, position: Int) {
            val comment = comments[position]
            holder.tvCommentUserName.text = comment["userName"]
            holder.tvCommentText.text = comment["comment"]
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }

    // comentarios

    private fun sendComment(snapshot: Snapshot, comment: String) {
        val commentRef = mSnapshotsRef.child(snapshot.id)
            .child("comments")
            .push()

        val commentData = mapOf(
            "userId" to SnapshotsApplication.currentUser.uid,
            "userName" to SnapshotsApplication.currentUser.displayName,
            "comment" to comment
        )

        commentRef.setValue(commentData)
    }
}

