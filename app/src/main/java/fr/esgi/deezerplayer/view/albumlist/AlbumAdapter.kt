package fr.esgi.deezerplayer.view.albumlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.databinding.ItemAlbumRecyclerviewBinding


class AlbumAdapter(
    private val albums: List<Album>
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun getItemCount() = albums.size

    // Create ViewHolder par defaut => inflate la vue d'un item et creer le viewHolder avec la vue
    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album_recyclerview, parent, false)
        return AlbumViewHolder(view)
    }*/

    // Avec DataBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AlbumViewHolder {
        val viewBinding = DataBindingUtil.inflate<ItemAlbumRecyclerviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_album_recyclerview,
            parent,
            false
        )
        return AlbumViewHolder(viewBinding)
    }


    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        // .album se trouve dans <variable> dans xml
        // met a jour les valeurs dans xml tout seul
        holder.recyclerViewAlbumBinding.album = albums[position]

        // recup l'album pour afficher l'image car ne sait pas mettre dans BindingUtils
        // car ne sait pas comment avoir la ref de 2 vue (ImageView + Shimmer)
        val album = albums[position]
        with(holder) {
            loadImage(albumCoverImv, album.cover, shimmer)
            //itemView.setOnClickListener { listener?.invoke(photo) }
        }

    }

    private fun loadImage(imageView: ImageView, url: String, shimmer: ShimmerFrameLayout) {
        shimmer.startShimmerAnimation()
        Picasso.get()
            .load(url)
            .resize(100, 100)
            .centerInside()
            .noFade()
            .placeholder(R.color.grey)
            .error(R.drawable.ic_launcher_background)  //TODO: changer image error
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // animation fade-in
                    imageView.alpha = 0f
                    shimmer.stopShimmerAnimation()
                    imageView.animate().setDuration(1000).alpha(1f).start()
                }

                override fun onError(e: Exception?) {
                    shimmer.stopShimmerAnimation()
                }
            })
    }

    class AlbumViewHolder(
        // comme itemView (vue d'un item d'un recyclerview) mais dataBinding <layout> in item_album_recyclerview d'où le nom du type ("ItemAlbumRecyclerviewBinding")
        val recyclerViewAlbumBinding: ItemAlbumRecyclerviewBinding
    ) : RecyclerView.ViewHolder(recyclerViewAlbumBinding.root) {
        // normalement pas obliger de recup des vues ici car tout peut etre fait avec databinding tout seul
        // utilisé pour loadImage
        var albumCoverImv: ImageView = itemView.findViewById(R.id.album_cover)
        var shimmer: ShimmerFrameLayout = itemView.findViewById(R.id.parentShimmerLayout)
    }
}