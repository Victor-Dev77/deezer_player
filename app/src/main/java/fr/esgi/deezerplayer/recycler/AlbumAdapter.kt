package fr.esgi.deezerplayer.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
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
            shimmer.startShimmerAnimation()

            Picasso.get()
                .load(album.coverMedium)
                .resize(100, 100)
                .centerInside()
                .noFade()
                .placeholder(R.color.grey)
                .error(R.drawable.ic_launcher_background)
                .into(albumCoverImv, object : Callback {
                    override fun onSuccess() {
                        albumCoverImv.alpha = 0f
                        shimmer.stopShimmerAnimation()
                        albumCoverImv.animate().setDuration(1000).alpha(1f).start()

                    }

                    override fun onError(e: Exception?) {
                        shimmer.stopShimmerAnimation()
                    }
                })

            //itemView.setOnClickListener { listener?.invoke(photo) }
        }

    }



    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumTitle: TextView = itemView.findViewById(R.id.album_title)
        var albumCoverImv: ImageView = itemView.findViewById(R.id.album_cover)
        var shimmer: ShimmerFrameLayout = itemView.findViewById(R.id.parentShimmerLayout)
    }
}