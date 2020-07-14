package fr.esgi.deezerplayer.data.api

import fr.esgi.deezerplayer.BuildConfig
import fr.esgi.deezerplayer.data.dto.AlbumDTO
import fr.esgi.deezerplayer.data.dto.TrackResponseDTO
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TrackAPI {

    @GET("{album_id}/tracks")
    suspend fun getTracks(@Path("album_id") albumID: Int): Response<TrackResponseDTO>

    @GET("{album_id}")
    suspend fun getAlbum(@Path("album_id") albumID: Int): Response<AlbumDTO>

    // Redefinition fonction invoke pour créer instance Retrofit.
    companion object {
        operator fun invoke(): TrackAPI {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.TRACK_API_URL)
                //.client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TrackAPI::class.java)
        }
    }


}