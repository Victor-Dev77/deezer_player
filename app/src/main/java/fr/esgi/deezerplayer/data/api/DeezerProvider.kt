package fr.esgi.deezerplayer.data.api

import android.util.Log
import fr.esgi.deezerplayer.BuildConfig
import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import fr.esgi.deezerplayer.data.dto.mapper.AlbumsResponseMapper
import fr.esgi.deezerplayer.model.Album
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object DeezerProvider {
    private var service: DeezerService

    init {
        service = Retrofit.Builder().baseUrl(BuildConfig.DEEZER_API_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeezerService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            //.addNetworkInterceptor(StethoInterceptor())
            .addInterceptor {
                val request = it.request()
                val url = request.url
                val builder = url.newBuilder()
                    /*.addQueryParameter(PARAM_API_KEY, BuildConfig.FLICKR_API_KEY)
                    .addQueryParameter(PARAM_FORMAT, BuildConfig.FLICKR_API_FORMAT)
                    .addQueryParameter(PARAM_NOJSON_CALLBACK, BuildConfig.FLICKR_API_CALLBACK)*/

                val newUrl = builder.build()
                val newRequest = request.newBuilder().url(newUrl).build()

                it.proceed(newRequest)
            }.build()
    }


    fun getAlbums(listener: Listener<List<Album>>) {
        service.getAlbums().enqueue(object : Callback<AlbumResponseDTO> {
            override fun onFailure(call: Call<AlbumResponseDTO>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<AlbumResponseDTO>,
                response: Response<AlbumResponseDTO>
            ) {
                response.body()?.let { albumsResponseDTO ->
                    val photos = AlbumsResponseMapper().map(albumsResponseDTO)
                    listener.onSuccess(photos)
                }
            }
        })
    }

}

interface Listener<T> {
    fun onSuccess(data: T)
    fun onError(t: Throwable)
}