package com.example.musicgo

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicListFragment : Fragment() {
    var isLoading = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MusicListAdapter
    private var songsList: ArrayList<Song?> = ArrayList()
    private val recyclerList: ArrayList<Song?> = ArrayList()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var songIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)
        fetchSongsFromInternalStorage(view)
        recyclerView = view.findViewById(R.id.music_list)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        populateData()
        recyclerAdapter = MusicListAdapter(recyclerList)
        recyclerView.adapter = recyclerAdapter
        initializeScrollListener()
        return view
    }

    private fun initializeScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerList.size - 1) {
                        loadMore()
                        isLoading = true
                    }
                }
            }


        })
    }

    private fun populateData() {
        var count = 0
        while (count < 10 && songIndex < songsList.size) {
            recyclerList.add(songsList[songIndex])
            songIndex += 1
            count += 1
        }
    }

    private fun fetchSongsFromInternalStorage(view: View) {
        songsList = MusicSource.getInstance(view)!!.getSongsList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore() {
        recyclerList.add(null)
        recyclerAdapter.notifyItemInserted(recyclerList.size - 1)
        scope.launch {
            delay(2000)
            recyclerList.removeAt(recyclerList.size - 1)
            val scrollPosition = recyclerList.size
            recyclerAdapter.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit && songIndex < songsList.size) {
                recyclerList.add(songsList[songIndex])
                songIndex++
                currentSize++
            }
            recyclerAdapter.notifyDataSetChanged()
            isLoading = false
        }

    }

}

