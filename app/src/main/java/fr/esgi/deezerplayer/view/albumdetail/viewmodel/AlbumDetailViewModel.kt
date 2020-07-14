package fr.esgi.deezerplayer.view.albumdetail.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.data.model.musicplayer.CreateNotification
import fr.esgi.deezerplayer.data.model.musicplayer.Player
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.util.Coroutines
import fr.esgi.deezerplayer.view.AppWidgetHandle
import kotlinx.coroutines.Job

class AlbumDetailViewModel(
    private val context: Context,
    private val repository: TrackRepository
) : ViewModel() {

    // job = reference sur la coroutine
    private lateinit var job: Job

    // var private en Mutable qui permet de modifier _albums en interne
    private val _tracks = MutableLiveData<List<Track>>()
    // rend accessible un getter de _album SEULEMENT en LiveData pour bloquer la modification a l'extérieur de la classe
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _playerAdapter = Player

    // Peut enlever suspend fun et remplace par coroutine job reference et gérer onCleared
    fun getTracks(albumID: Int) {
        job = Coroutines.ioThenMain(
            { repository.getTracks(albumID) },
            {
                _tracks.value = it
            } // callback
        )
    }

    // annule coroutine si la view est détruite
    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

    fun setCurrentTrack(track: Track) {
        _playerAdapter.setCurrentTrack(track)
    }

    fun setTrackList(tracks: List<Track>?) {
        _playerAdapter.setTrackList(tracks)
    }

    fun setAlbum(album: Album?) {
        _playerAdapter.setAlbum(album)
    }

    fun startPlayer(track: Track, album: Album) {
        _playerAdapter.loadTrack(track.song)
        CreateNotification.createNotification(context, album, track, R.drawable.ic_pause, 1, ((tracks.value?.size
            ?: 2) - 1))
     //   AppWidgetHandle.updateAppWidget(context)
    }

}
