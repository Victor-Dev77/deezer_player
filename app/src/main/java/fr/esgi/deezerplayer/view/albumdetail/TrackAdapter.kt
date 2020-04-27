package fr.esgi.deezerplayer.view.albumdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.databinding.ItemTrackRecyclerviewBinding
import fr.esgi.deezerplayer.view.RVClickListener

class TrackAdapter (
    private val tracks: List<Track>,
    private val listener: RVClickListener
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun getItemCount() = tracks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val viewBinding = DataBindingUtil.inflate<ItemTrackRecyclerviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_track_recyclerview,
            parent,
            false
        )
        return TrackViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        with(holder) {
            recyclerViewTrackBinding.track = tracks[position]

            recyclerViewTrackBinding.root.setOnClickListener {
                listener.onRecyclerViewItemClick(recyclerViewTrackBinding.root, tracks[position])
            }
        }
    }

    class TrackViewHolder (
        // comme itemView (vue d'un item d'un recyclerview) mais dataBinding <layout> in item_album_recyclerview d'où le nom du type ("ItemAlbumRecyclerviewBinding")
        val recyclerViewTrackBinding: ItemTrackRecyclerviewBinding
    ) : RecyclerView.ViewHolder(recyclerViewTrackBinding.root) {
        // Ne recup pas les view ici avec findviewbyid car sont contenu deja dans recyclerViewAlbumBinding
    }
}