package fr.esgi.deezerplayer.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory pour envoyer des parametres dans un ViewModel
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}