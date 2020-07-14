package fr.esgi.deezerplayer.util

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import fr.esgi.deezerplayer.R


@BindingAdapter("cover")
fun loadImage(imageView: ImageView, url: String?) {
    if (url != null) {
        val shimmer = imageView.parent as ShimmerFrameLayout
        shimmer.startShimmerAnimation()
        Picasso.get()
            .load(url)
            .resize(100, 100)
            .centerInside()
            .noFade()
            .placeholder(R.color.grey)
            .error(R.drawable.ic_music_note)  //TODO: changer image error
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
}

@BindingAdapter("durationTrack")
fun convertDurationTrack(textView: TextView, seconds: Int?) {
    var minutes = String.format("%.2f", seconds?.toDouble()?.div(60))
    minutes = minutes.replace(",", ":")
    textView.text = minutes
}

@BindingAdapter("date_album")
fun setDateAlbum(textView: TextView, date: String) {
    val dateAlbum = date.split("-").reversed().joinToString("/")
    textView.text = dateAlbum
}

class BindingUtils {
    fun shareAlbum(view: View, idAlbum: Int) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "deezerplayer://player?album=$idAlbum")
            type = "text / plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(view.context, shareIntent, null)
    }

    fun shareTrack(view: View, idAlbum: Int, idTrack: Int) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "deezerplayer://player?album=$idAlbum&track=$idTrack")
            type = "text / plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(view.context, shareIntent, null)
    }
}
