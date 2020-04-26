package fr.esgi.deezerplayer.view.albumlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.databinding.ItemAlbumRecyclerviewBinding
import fr.esgi.deezerplayer.util.loadImage


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

    class AlbumViewHolder(
        // comme itemView (vue d'un item d'un recyclerview) mais dataBinding <layout> in item_album_recyclerview d'où le nom du type ("ItemAlbumRecyclerviewBinding")
        val recyclerViewAlbumBinding: ItemAlbumRecyclerviewBinding
    ) : RecyclerView.ViewHolder(recyclerViewAlbumBinding.root) {
        // Ne recup pas les view ici avec findviewbyid car sont contenu deja dans recyclerViewAlbumBinding
    }
}