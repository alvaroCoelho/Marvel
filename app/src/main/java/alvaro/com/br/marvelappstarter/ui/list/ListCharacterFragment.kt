package alvaro.com.br.marvelappstarter.ui.list

import alvaro.com.br.marvelappstarter.R
import androidx.fragment.app.viewModels
import alvaro.com.br.marvelappstarter.databinding.FragmentDetailsCharacterBinding
import alvaro.com.br.marvelappstarter.databinding.FragmentListCharacterBinding
import alvaro.com.br.marvelappstarter.ui.adapters.CharacterAdapter
import alvaro.com.br.marvelappstarter.ui.base.BaseFragment
import alvaro.com.br.marvelappstarter.ui.state.ResourceState
import alvaro.com.br.marvelappstarter.util.hide
import alvaro.com.br.marvelappstarter.util.show
import alvaro.com.br.marvelappstarter.util.toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ListCharacterFragment : BaseFragment<FragmentListCharacterBinding, ListCharacterViewModel>() {

    override val viewModel: ListCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        clickAdapter()
        collectObserver()
    }

    private fun collectObserver() = lifecycleScope.launch{
        viewModel.list.collect{ resource ->
            when(resource){
                is ResourceState.Sucess -> {
                    resource.data?.let { values ->
                        binding.progressCircular.hide()
                        characterAdapter.characters = values.data.results.toList()
                    }
                }
                is ResourceState.Error -> {
                    binding.progressCircular.hide()
                    resource.message?.let { message ->
                        toast(getString(R.string.an_error_occurred))
                        Timber.tag("ListCharacterFragment").e("Error -> $message")
                    }
                }
                is ResourceState.Loading -> {
                    binding.progressCircular.show()
                }
                else -> {}

            }
        }

    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener { characterModel ->
            val action =
                ListCharacterFragmentDirections.actionListCharacterFragmentToDetailsCharacterFragment(
                    characterModel)
            findNavController().navigate(action)

        }
    }

    private fun setupRecycleView() = with(binding) {
        rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCharacterBinding =
        FragmentListCharacterBinding.inflate(inflater, container, false)
}