package alvaro.com.br.marvelappstarter.ui.details

import alvaro.com.br.marvelappstarter.R
import alvaro.com.br.marvelappstarter.data.model.character.CharacterModel
import alvaro.com.br.marvelappstarter.databinding.FragmentDetailsCharacterBinding
import alvaro.com.br.marvelappstarter.ui.adapters.ComicAdapter
import alvaro.com.br.marvelappstarter.ui.base.BaseFragment
import alvaro.com.br.marvelappstarter.ui.state.ResourceState
import alvaro.com.br.marvelappstarter.util.hide
import alvaro.com.br.marvelappstarter.util.limitDescription
import alvaro.com.br.marvelappstarter.util.show
import alvaro.com.br.marvelappstarter.util.toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailsCharacterFragment :
    BaseFragment<FragmentDetailsCharacterBinding, DetailsCharacterViewModel>() {
    override val viewModel: DetailsCharacterViewModel by viewModels()

    private val args: DetailsCharacterFragmentArgs by navArgs()
    private lateinit var characterModel: CharacterModel
    private val comicAdapter by lazy { ComicAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.character
        viewModel.fetch(characterModel.id)
        onLoadedCharacter(characterModel)
        setupRecyclerView()
        collectObserver()
    }

    private fun setupRecyclerView() = with(binding){
        rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.details.collect { result ->
            when (result) {
                is ResourceState.Sucess -> {
                    binding.progressBarDetail.hide()
                    result.data?.let { values ->
                        if (values.data.result.count() > 0) {
                            comicAdapter.comics = values.data.result.toList()
                        } else {
                            toast(getString(R.string.empty_list_comics))
                        }
                    }

                }
                is ResourceState.Error -> {
                    binding.progressBarDetail.hide()
                    result.message?.let { message ->
                        Timber.tag("detailsCharacter").e("Error -> $message")
                        toast(getString(R.string.an_error_occurred)+" "+message)
                    }
                }
                is ResourceState.Loading -> {
                    binding.progressBarDetail.show()
                }
                else -> {
                }

            }
        }

    }

    private fun onLoadedCharacter(characterModel: CharacterModel) = with(binding) {
        tvNameCharacterDetails.text = characterModel.name
        if (characterModel.description.isEmpty()) {
            tvDescriptionCharacterDetails.text = requireContext()
                .getString(R.string.text_description_empty)
                .limitDescription(100)
        } else {
            tvDescriptionCharacterDetails.text = characterModel.description
        }
        Glide.with(requireContext())
            .load(characterModel.thumbnailModel.path + "." + characterModel.thumbnailModel.extension)
            .into(imgCharacterDetails)

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}
