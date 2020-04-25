package fr.esgi.deezerplayer.data.api

import fr.esgi.deezerplayer.BuildConfig
import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Service Retrofit contenant tt les req http api (avec @GET, ...)
interface DeezerAPI {

    @GET("albums")
    suspend fun getAlbums(): Response<AlbumResponseDTO>

    // Modele de requette http: @GET => morceau d'url a getter
                             // @Query => param a envoyer dans la req
    /* @GET("?method=flickr.photos.getInfo")
    fun getPhotoInfo(@Query("photo_id") photoId: String): Call<PhotoDetailDTO>*/


    // Redefinition fonction invoke pour créer instance Retrofit.
    companion object {
        operator fun invoke(): DeezerAPI {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.DEEZER_API_URL)
                //.client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DeezerAPI::class.java)
        }
    }


    // Créer le client HTTP avec Stetho (lib) pour voir
    // et intercepter le reseau (req http) si ya une merde

    /*private fun createOkHttpClient(): OkHttpClient {
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
    }*/
}