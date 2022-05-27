package com.example.musicgo


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView


class MusicListAdapter(private val songsList : ArrayList<Song?>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewTypeItem = 0
    private val viewTypeLoading = 1
    class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val songTitle : TextView = view.findViewById(R.id.song_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeItem) {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.movie_list_item,
                        parent,
                        false
                    )
            ItemViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder) {
            populateItemRows(holder as ItemViewHolder?, position)
        }

    }

    private fun populateItemRows(itemViewHolder: ItemViewHolder?, position: Int) {
        val song = this.songsList[position]
        itemViewHolder?.songTitle?.text = song?.songName
        itemViewHolder?.itemView?.setOnClickListener {
            if(song != null) {
                val action =
                    MusicListFragmentDirections.actionMusicListFragmentToMusicPlayerFragment(position)
                Navigation.findNavController(itemViewHolder.itemView).navigate(action)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (songsList[position] == null) viewTypeLoading else viewTypeItem
    }

    override fun getItemCount(): Int {
        return songsList.size
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val progressBar :ProgressBar = view.findViewById(R.id.progressBar)

}
