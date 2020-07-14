package fr.esgi.deezerplayer.features.view.albumlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.repository.AlbumRepository
import fr.esgi.deezerplayer.util.Coroutines
import kotlinx.coroutines.Job

// viewModel de l'architecture MVVM
class AlbumListViewModel(
    // connecté au repository
    private val repository: AlbumRepository
) : ViewModel() {

    // job = reference sur la coroutine
    private lateinit var job: Job

    // var private en Mutable qui permet de modifier _albums en interne
    private val _albums = MutableLiveData<List<Album>>()
    // rend accessible un getter de _album SEULEMENT en LiveData pour bloquer la modification a l'extérieur de la classe
    val albums: LiveData<List<Album>> get() = _albums

    // Peut enlever suspend fun et remplace par coroutine job reference et gérer onCleared
    fun getAlbums() {
        job = Coroutines.ioThenMain(
            { repository.getAlbums() },
            { _albums.value = it } // callback
        )
    }

    // annule coroutine si la view est détruite
    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

}
