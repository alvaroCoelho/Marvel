package alvaro.com.br.marvelappstarter.ui.details

import alvaro.com.br.marvelappstarter.data.model.comic.ComicModelResponse
import alvaro.com.br.marvelappstarter.repository.MarvelRepository
import alvaro.com.br.marvelappstarter.ui.state.ResourceState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {
    private val _details =
        MutableStateFlow<ResourceState<ComicModelResponse>>(ResourceState.Loading())
    val details: StateFlow<ResourceState<ComicModelResponse>> = _details

    fun fetch(characterId: Int) = viewModelScope.launch {
        safeFetch(characterId)
    }

    private suspend fun safeFetch(characterId: Int) {
        _details.value = ResourceState.Loading()
        try {
            val response = repository.getComics(characterId)
            _details.value = handleResponse(response)
        }catch (t: Throwable){
            when (t){
                is IOException -> _details.value = ResourceState.Error(t.message.toString())
                else -> _details.value = ResourceState.Error(t.message.toString())
            }
        }
    }

    private fun handleResponse(response: Response<ComicModelResponse>):
            ResourceState<ComicModelResponse> {
        if (response.isSuccessful){
            response.body()?.let { values ->
                return ResourceState.Sucess(values)
            }
        }
        return ResourceState.Error(response.message())
    }

}
