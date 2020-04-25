package fr.esgi.deezerplayer.view.albumlist

import android.util.Log
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
    private val albums: List<Album>,
    private val listener: AlbumListRVClickListener
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

        // loadImage ici car ne sait pas mettre dans BindingUtils
        // car ne sait pas comment avoir la ref de 2 vue (ImageView + Shimmer) directement dans le XML
        with(holder) {
            loadImage(
                recyclerViewAlbumBinding.albumCover, //recupere view (albumCover = id dans XML)
                recyclerViewAlbumBinding.album!!.cover,
                recyclerViewAlbumBinding.parentShimmerLayout // recupere view (parentShimmerLayout = id dans XML)
            )

            // click listener sur tout l'item
            /*recyclerViewAlbumBinding.root.setOnClickListener {
                Log.d("toto", "pos: " + position)
            }*/
            // click listener sur un composant du XML spécifique
            /*recyclerViewAlbumBinding.albumTitle.setOnClickListener {
                Log.d("toto", "title clicked")
            }*/

            // Si on veut faire callback vers la vue quand item cliqué pour startActivity par exemple
            recyclerViewAlbumBinding.root.setOnClickListener {
                listener.onRecyclerViewItemClick(recyclerViewAlbumBinding.root, albums[position])
            }
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
        // Ne recup pas les view ici avec findviewbyid car sont contenu deja dans recyclerViewAlbumBinding
    }
}