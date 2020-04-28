package fr.esgi.deezerplayer.view.albumdetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.data.model.musicplayer.Player
import fr.esgi.deezerplayer.data.repositories.TrackRepository
import fr.esgi.deezerplayer.util.Coroutines
import kotlinx.coroutines.Job

class AlbumDetailViewModel(
    private val repository: TrackRepository
) : ViewModel() {

    // job = reference sur la coroutine
    private lateinit var job: Job

    // var private en Mutable qui permet de modifier _albums en interne
    private val _tracks = MutableLiveData<List<Track>>()
    // rend accessible un getter de _album SEULEMENT en LiveData pour bloquer la modification a l'extérieur de la classe
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _playerAdapter = Player
    val playerAdapter get() = _playerAdapter
    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> get() = _currentTrack

    // Peut enlever suspend fun et remplace par coroutine job reference et gérer onCleared
    fun getTracks(albumID: Int) {
        job = Coroutines.ioThenMain(
            { repository.getTracks(albumID) },
            {
                _tracks.value = it
                _currentTrack.value = _tracks.value!![0]
            } // callback
        )
    }

    // annule coroutine si la view est détruite
    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

    fun setCurrentTrack(track: Track) {
        _currentTrack.value = track
    }

    fun nextTrack() {
        if (_tracks.value != null && currentTrack.value != null) {
            if (currentTrack.value!!.trackPosition < _tracks.value!!.size) {
                // Next track
                _currentTrack.value = _tracks.value!![currentTrack.value!!.trackPosition]
            }
            else {
                // load first track
                _currentTrack.value = _tracks.value!![0]
            }
            playerAdapter.loadTrack(_currentTrack.value!!.song)
        }
    }

    fun previousTrack() {
        if (_tracks.value != null && currentTrack.value != null) {
            if (currentTrack.value!!.trackPosition != 1) {
                // si c pas la 1ere track on peut previous
                // -2 car trackPosition commence a 1 et pas 0
                _currentTrack.value = _tracks.value!![currentTrack.value!!.trackPosition - 2]
                playerAdapter.loadTrack(_currentTrack.value!!.song)
            }
        }
    }
}
