package com.bd.firebasedb

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var btnSave: Button
    private lateinit var spn: Spinner
    private lateinit var recyclerView: RecyclerView
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    lateinit var artistList: MutableList<Artist>
    lateinit var progressDialog: ProgressDialog

    lateinit var dataBaseArtisr: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById<EditText>(R.id.txtname)
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);

        var progressDialog = ProgressDialog(this);
        progressDialog.setMessage("Loading Artist...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        btnSave = findViewById<Button>(R.id.btnSave)
        spn = findViewById<Spinner>(R.id.spn)


        btnSave.setOnClickListener(View.OnClickListener {
            addArtist()

        })
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        artistList = mutableListOf()
        // this.getWindow().setSoftInputMode(MainActivity.);
        editText.clearFocus()

        val options: FirebaseRecyclerOptions<Artist> = FirebaseRecyclerOptions.Builder<Artist>()
            .setQuery(FirebaseDatabase.getInstance().getReference("artists"), Artist::class.java)
            .build()

        dataBaseArtisr = FirebaseDatabase.getInstance().getReference("artists")

        dataBaseArtisr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //       TODO("Not yet implemented")
                if (snapshot.exists()) {
                    artistList.clear()
                    for (i in snapshot.children) {
                        val artist = i.getValue(Artist::class.java)
                        artistList.add(artist!!)
                    }

                    val adapter = MyAdapter(applicationContext, artistList)
                    recyclerView.adapter = adapter
                    progressDialog.dismiss()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //         TODO("Not yet implemented")
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }

        })

    }


    fun addArtist() {
        var name: String = editText.text.toString()
        var genre: String = spn.selectedItem.toString()
        if (name.isEmpty()) {
            editText.error = "Please enter a value"
            return
        }
        var id: String? = dataBaseArtisr.push().key
        // var artist = Artist(id,name,genre)
        if (id != null) {
            Log.d("value", name + genre)
            dataBaseArtisr.child(id).setValue(Artist(id, name, genre))
            Toast.makeText(this, "Artist Added", Toast.LENGTH_LONG).show()
            editText.setText("")
            editText.clearFocus()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        var item: MenuItem = menu.findItem(R.id.search)
        var serverValue: SearchView = item.actionView as SearchView

        serverValue.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //  TODO("Not yet implemented")
                precessSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //  TODO("Not yet implemented")
                precessSearch(newText)
                return false
            }

        })

        return true
    }

    private fun precessSearch(query: String?) {

        Log.d("Message", query.toString())

        dataBaseArtisr.orderByChild("artistgenre").startAt(query.toString())
            .endAt(query.toString() + "\uf8ff").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //       TODO("Not yet implemented")

                    Log.d("Data", snapshot.toString())
                    if (snapshot.exists()) {
                        artistList.clear()
                        for (i in snapshot.children) {
                            val artist = i.getValue(Artist::class.java)
                            artistList.add(artist!!)
                        }

                        val adapter = MyAdapter(applicationContext, artistList)
                        recyclerView.adapter = adapter
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    //         TODO("Not yet implemented")
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }

            })


    }


}