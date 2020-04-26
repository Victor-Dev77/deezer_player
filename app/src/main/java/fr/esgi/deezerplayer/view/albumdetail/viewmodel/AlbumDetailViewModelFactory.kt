package fr.esgi.deezerplayer.view.albumdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.esgi.deezerplayer.data.repositories.TrackRepository

@Suppress("UNCHECKED_CAST") // seulement pour la compil et avoir check vert du fichier
// Factory qui creer le viewModel
class AlbumDetailViewModelFactory (
    private val repository: TrackRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumDetailViewModel(
            repository
        ) as T
    }

}