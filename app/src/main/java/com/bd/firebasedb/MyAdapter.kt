package com.bd.firebasedb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.coroutineContext

class MyAdapter(context: Context, val dataSet: MutableList<Artist>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    var alertDialog: AlertDialog? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var txtName: TextView
        lateinit var txtgenrec: TextView

        init {
            txtName = itemView.findViewById<TextView>(R.id.txtName)
            txtgenrec = itemView.findViewById<TextView>(R.id.txtgenrec)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //  TODO("Not yet implemented")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //  TODO("Not yet implemented")
        holder.txtName.text = dataSet[position].artistName
        holder.txtgenrec.text = dataSet[position].artistgenre


        holder.itemView.setOnLongClickListener(OnLongClickListener {

            showUpdateDialog(
                holder.txtName.context,
                dataSet[position].artistName,
                dataSet[position].artistgenre,
                dataSet[position].artistID
            )

            true
        })

    }

    private fun showUpdateDialog(
        context: Context,
        artistName: String,
        artistgenre: String,
        artistID: String,


        ) {
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.update_dialog, null)
        var mBuilder: AlertDialog.Builder? = context?.let {
            AlertDialog.Builder(it).setView(dialogView)
                .setTitle("Update Data")
        }

        if (mBuilder != null) {
            alertDialog = mBuilder.create()
        }
        alertDialog?.show()


        val txtname: EditText = dialogView.findViewById<EditText>(R.id.txtname)
        var spn: Spinner = dialogView.findViewById<Spinner>(R.id.spn)
        var btnSave: Button = dialogView.findViewById<Button>(R.id.btnUpdate)
        var btnDelete: Button = dialogView.findViewById<Button>(R.id.btnDelete)
        txtname.setText(artistName)


        for (i in 0 until spn.getCount()) {
            if (spn.getItemAtPosition(i).equals(artistgenre)) {
                spn.setSelection(i)
                break
            }
        }

        btnSave.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                updateArtist(
                    context,
                    artistID,
                    txtname.text.toString(),
                    spn.selectedItem.toString()
                )
                alertDialog?.dismiss()

            }

        })

        btnDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                btnDeleteArtist(context, artistID)
                alertDialog?.dismiss()
            }

        })

    }

    private fun btnDeleteArtist(context: Context, artistID: String) {
        var dataBaseArtistValue: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("artists").child(artistID)
        dataBaseArtistValue.removeValue()

    }

    private fun updateArtist(
        context: Context,
        artistID: String,
        txtName: String,
        txtGenre: String
    ): Boolean {
        var dataBaseArtist: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("artists").child(artistID)

        var artist = Artist(artistID, txtName, txtGenre)
        dataBaseArtist.setValue(artist)

        Toast.makeText(context, "Artist Updated", Toast.LENGTH_SHORT).show()
        return true
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }
}