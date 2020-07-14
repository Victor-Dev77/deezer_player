package fr.esgi.deezerplayer.data.repository

import fr.esgi.deezerplayer.data.api.AlbumAPI
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.dto.mapper.AlbumsResponseMapper

// Repository (design pattern) permet de faire le pont entre le viewModel et les sources de données
// ici la source de donnée est l'API avec Retrofit mais on peut aussi avoir la BDD local SQLite
class AlbumRepository(
    private val api: AlbumAPI
) : ListenerAPIRequest() {

    suspend fun getAlbums(): List<Album> {
        // apiRequest de ListenerAPIRequest qui va lancer la req http et retourner le resultat T
        // ici AlbumResponseDTO qui est "data" du json => un array d'album
        val albumsResponseDTO = apiRequest { api.getAlbums() } // { ... } func call
        return AlbumsResponseMapper().map(albumsResponseDTO)
    }
}