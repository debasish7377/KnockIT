package com.example.knockitUser.Activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.R


class SearchActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var textView: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.text_view);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                val tags: List<String> = query.toLowerCase().split(" ")
                for ( tag in tags){
                    tag.trim();
                    ProductDatabase.loadProductsBySearch(this@SearchActivity, recyclerView, tag)
                }

                return false
            }

        })


    }
}