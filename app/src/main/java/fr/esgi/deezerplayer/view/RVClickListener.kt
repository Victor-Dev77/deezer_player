package fr.esgi.deezerplayer.view

import android.view.View

interface RVClickListener {
    fun <T> onRecyclerViewItemClick(view: View, data: T)
}