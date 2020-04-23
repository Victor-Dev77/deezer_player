package fr.esgi.deezerplayer.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.model.Album

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    var data: List<Album> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: ((Album) -> Unit)? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }


    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = data[position]

        with(holder) {
            albumTitle.text = album.title
            /*Glide.with(itemView)
                .load(photo.url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnailImv)*/

            //itemView.setOnClickListener { listener?.invoke(photo) }
        }

    }



    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumTitle: TextView = itemView.findViewById(R.id.album_title)
    }
}