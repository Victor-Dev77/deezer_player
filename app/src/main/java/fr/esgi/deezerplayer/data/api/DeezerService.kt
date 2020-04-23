package fr.esgi.deezerplayer.data.api

import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {
    @GET("albums")
    fun getAlbums(): Call<AlbumResponseDTO>

   /* @GET("?method=flickr.photos.getInfo")
    fun getPhotoInfo(@Query("photo_id") photoId: String): Call<PhotoDetailDTO>*/
}