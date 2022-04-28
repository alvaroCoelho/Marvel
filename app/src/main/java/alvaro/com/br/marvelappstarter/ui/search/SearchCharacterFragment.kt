package alvaro.com.br.marvelappstarter.ui.search

import alvaro.com.br.marvelappstarter.R
import alvaro.com.br.marvelappstarter.databinding.FragmentDetailsCharacterBinding
import alvaro.com.br.marvelappstarter.databinding.FragmentSearchCharacterBinding
import alvaro.com.br.marvelappstarter.ui.adapters.CharacterAdapter
import alvaro.com.br.marvelappstarter.ui.base.BaseFragment
import alvaro.com.br.marvelappstarter.ui.state.ResourceState
import alvaro.com.br.marvelappstarter.util.Constants.DEFAULT_QUERY
import alvaro.com.br.marvelappstarter.util.Constants.LAST_SEARCH_QUERY
import alvaro.com.br.marvelappstarter.util.hide
import alvaro.com.br.marvelappstarter.util.show
import alvaro.com.br.marvelappstarter.util.toast
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchCharacterFragment :
    BaseFragment<FragmentSearchCharacterBinding, SearchCharacterViewModel>() {
    override val viewModel: SearchCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        clickAdapter()

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        searchInit(query)

        collectObserver()
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.searchCaracter.collect { result ->
            when (result) {
                is ResourceState.Sucess -> {
                    binding.progressbarSearch.hide()
                    result.data?.let {
                        characterAdapter.characters = it.data.results.toList()
                    }
                }
                is ResourceState.Error -> {
                    binding.progressbarSearch.hide()
                    result.message?.let { message ->
                        Timber.tag("SearchCharacterFragment").e("Error--->$message")
                        toast(getString(R.string.an_error_occurred))
                    }

                }
                is ResourceState.Loading -> {
                    binding.progressbarSearch.show()
                }
                else -> {}
            }
        }

    }

    private fun searchInit(query: String) = with(binding) {
        edSearchCharacter.setText(query)
        edSearchCharacter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateCharacterList()
                true
            } else {
                false
            }
        }

        edSearchCharacter.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCharacterList()
                true
            } else {
                false
            }
        }

    }

    private fun updateCharacterList() = with(binding) {
        edSearchCharacter.editableText.trim().let {
            if (it.isNotEmpty()) {
                searchQuery(it.toString())
            }
        }
    }

    private fun searchQuery(query: String) {
        viewModel.fetch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            LAST_SEARCH_QUERY,
            binding.edSearchCharacter.editableText.trim().toString()
        )
    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener { characterModel ->
            val action =
                SearchCharacterFragmentDirections.actionSearchCharacterFragmentToDetailsCharacterFragment(
                    characterModel
                )
            findNavController().navigate(action)
        }
    }

    private fun setupRecycleView() = with(binding) {
        rvSearchCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchCharacterBinding =
        FragmentSearchCharacterBinding.inflate(inflater, container, false)
}