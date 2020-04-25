package fr.esgi.deezerplayer.view.albumlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.esgi.deezerplayer.data.repositories.AlbumsRepository

@Suppress("UNCHECKED_CAST") // seulement pour la compil et avoir check vert du fichier

// Factory qui creer le viewModel
class AlbumListViewModelFactory(
    private val repository: AlbumsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumListViewModel(repository) as T
    }

}