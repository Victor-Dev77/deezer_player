package fr.esgi.deezerplayer.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
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

}