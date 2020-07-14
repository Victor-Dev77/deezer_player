package fr.esgi.deezerplayer.features

import android.view.View

interface RVClickListener {
    fun <T> onRecyclerViewItemClick(view: View, data: T)
}