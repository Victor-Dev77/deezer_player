package fr.esgi.deezerplayer.data.repository

import retrofit2.Response
import java.io.IOException

// class abstraite qui va faire req async http et traiter la reponse
abstract class ListenerAPIRequest {

    // suspend = fonction asynchrone
    // func générique qui prend une func en param qui retourne une Response<T> et renvoi T ou une exception
    // exemple: albumRepository appel apiRequest avec { getAlbums() }
    // getAlbums() de DeezerAPI qui renvoi Response<AlbumResponseDTO>
    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke() // lance req retrofit
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw ApiException(response.code().toString())
        }
    }
}

class ApiException(message: String) : IOException(message)