package alvaro.com.br.marvelappstarter.data.remote

import alvaro.com.br.marvelappstarter.data.model.character.CharacterModelResponse
import alvaro.com.br.marvelappstarter.data.model.comic.ComicModelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {

    @GET("characters")
    suspend fun list(
        @Query("nameStartsWith") nameStartsWith: String?= null
    ):Response<CharacterModelResponse>



    @GET("characters/{characterId}/comics")
    suspend fun getComics(
        @Path(
            value = "characterId",
            encoded = true
        )characterId: Int

    ): Response<ComicModelResponse>
}