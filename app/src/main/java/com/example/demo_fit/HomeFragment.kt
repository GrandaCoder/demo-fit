package com.example.demo_fit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_fit.databinding.FragmentHomeBinding
import com.example.demo_fit.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private  lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>
    //solo dejamos el oncreateView de momento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseDatabase.getInstance().reference.child("snapshots")
        val options = FirebaseRecyclerOptions.Builder<Snapshot>()
            .setQuery(query,Snapshot::class.java).build()
        mFirebaseAdapter = object :FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                TODO("Not yet implemented")
            }

            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                TODO("Not yet implemented")
            }

        }
    }

    inner class SnapshotHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot){

        }
    }
}