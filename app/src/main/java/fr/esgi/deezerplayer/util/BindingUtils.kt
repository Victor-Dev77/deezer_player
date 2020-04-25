package fr.esgi.deezerplayer.util

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import fr.esgi.deezerplayer.R

// A la base mettre ici les fonction databinding
// c-a-d func appeler automatiquement avec @BindingAdapter et les param sont dans le XML


// "cover" est defini dans ImageView dans xml et url est l'argument ecrit avec @{album.cover} = string
// @BindingAdapter sait recup la vue ou est def "cover" et le param et son type pour créer la fonction
// Ne sait pas comment ajouter une 2eme vue en parametre (Shimmer) avec Binding Adapter
// Donc load image se trouve dans AlbumAdapter vu qu'on peut recuperer tous les layout xml de la vue = findviewbyid
/*
@BindingAdapter("cover")
fun loadImage(view: ImageView, url: String, shimmer: Int) {
    Picasso.get()
        .load(url)
        .resize(100, 100)
        .centerInside()
        .noFade()
        .placeholder(R.color.grey)
        .error(R.drawable.ic_launcher_background)
        .into(view)
}
*/

fun loadImage(imageView: ImageView, url: String, shimmer: ShimmerFrameLayout) {
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