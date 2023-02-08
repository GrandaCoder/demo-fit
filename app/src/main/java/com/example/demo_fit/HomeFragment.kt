package com.example.demo_fit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.demo_fit.databinding.FragmentHomeBinding
import com.example.demo_fit.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

//Este código es un fragmento de la aplicación que representa la pantalla de inicio.
// En este fragmento, se muestran imágenes y títulos de los "Snapshots" obtenidos de la base de datos
// de Firebase.
class HomeFragment : Fragment() {
    //hacemos un binding para manipular las propiedades del home
    private lateinit var mBinding: FragmentHomeBinding

    /*
    *Esta línea de código es una variable de miembro que se declara para almacenar una instancia
    * de "FirebaseRecyclerAdapter". Este adaptador es utilizado para conectarse a la base de datos de Firebase
    * y recibir los datos de "Snapshots". El tipo de la variable es
    * "FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>" lo que significa que el adaptador está vinculado
    * a los datos de "Snapshot" y a la clase "SnapshotHolder".
    La palabra clave "lateinit" indica que esta variable se inicializará más tarde en el ciclo de vida del fragmento, específicamente en el método "onViewCreated". Esta palabra clave se usa en lugar de "var" o "val" porque la inicialización tardía es necesaria en este caso.
    * */
    private  lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    //solo dejamos el oncreateView de momento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*
        * Esta línea de código está inflando y asignando la vista de un fragmento específico
        * a la variable "mBinding". Inflar significa crear una vista a partir de un archivo XML
        * y "FragmentHomeBinding" es una clase generada automáticamente por el sistema de enlaces de
        *  datos de Android que se utiliza para vincular los datos a la vista.
        * */
        mBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //aqui se conecta a la base de datos desde el nodo snapshots (realtime database)
        val query = FirebaseDatabase.getInstance().reference.child("snapshots")

        //Aqui se le especificas cuales son los datos que se esperan de la base de datos,
        // desde la data clase snapshot
        val options =
        FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query, SnapshotParser {
            val snapshot = it.getValue(Snapshot::class.java)
            snapshot!!.id = it.key!!
            snapshot
        }).build()
        //FirebaseRecyclerOptions.Builder<Snapshot>()
        //.setQuery(query,Snapshot::class.java).build()


        /*
        * Este código establece un adapter para un RecyclerView usando FirebaseRecyclerAdapter.
        *  Se establece una consulta a una ruta específica en la base de
        *  datos de Firebase llamada "snapshots".*/
        mFirebaseAdapter = object :FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>(options){

            private  lateinit var mContext: Context

            //onCreateViewHolder crea un ViewHolder personalizado y enlaza su vista a una variable llamada binding.
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext = parent.context
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_snapshot,parent,false)
                return SnapshotHolder(view)
            }

            /*
            onBindViewHolder llena la vista con información del modelo Snapshot.
             Aquí se establece el texto del título y se carga una imagen con Glide.
            * */
            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)

                //Un holder es una clase en Android que se utiliza para contener
                // y manejar los datos de un elemento de una lista,
                with(holder){
                    setListener(snapshot)
                    binding.tvTitle.text = snapshot.title
                    Glide.with(mContext)
                        .load(snapshot.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.imgPhoto)
                }
            }

            //onDataChanged se ejecuta cuando los datos de la base de datos han cambiado.
            // Aquí se hace invisible una barra de progreso.
            @SuppressLint("NotifyDataSetChanged")  // error interno firebase UI
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE


                notifyDataSetChanged()
            }

            //onError muestra un mensaje Toast en caso de un error en la consulta a la base de datos.
            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext,error.message, Toast.LENGTH_SHORT).show()
            }
        }

        mLayoutManager = LinearLayoutManager(context)
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFirebaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        //empieza a escuchar si hay cambios en la base de datos. (tener encuenta: ciclo de vida de una app)
        mFirebaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        //deja de escuchar si hay cambios en la base de datos
        mFirebaseAdapter.stopListening()
    }

    private fun deleteSnapshot(snapshot: Snapshot){
        val databaseReference = FirebaseDatabase.getInstance().reference.child("snapshots")
        databaseReference.child(snapshot.id).removeValue()
    }

    private fun setLikeSnapshot(snapshot: Snapshot,isChecked: Boolean){

    }

    //Este código define una clase interna llamada SnapshotHolder que hereda de la clase
    // RecyclerView.ViewHolder. La clase SnapshotHolder tiene una propiedad llamada "binding"
    // que se inicializa a partir de la clase "ItemSnapshotBinding" y el método "bind" en la vista.
    inner class SnapshotHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot){
            binding.btnDelete.setOnClickListener { deleteSnapshot(snapshot) }
        }
    }
}