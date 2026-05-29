package com.example.sportshub.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportshub.R
import com.example.sportshub.core.domain.common.Resource
import com.example.sportshub.core.domain.model.Sport
import com.example.sportshub.core.ui.SportAdapter
import com.example.sportshub.databinding.FragmentHomeBinding
import com.example.sportshub.detail.DetailSportActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private companion object {
        const val DEFAULT_SPORT = "Soccer"
        const val DEFAULT_COUNTRY = "Spain"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sportAdapter = SportAdapter()
    private var sports = emptyList<Sport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportAdapter.onItemClick = { selectedData ->
            startActivity(DetailSportActivity.createIntent(requireContext(), selectedData))
        }

        with(binding.rvTeam) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = sportAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTeam(query)
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTeam(newText)
                return true
            }
        })

        observeSports()
    }

    private fun observeSports() {
        homeViewModel.getSports(DEFAULT_SPORT, DEFAULT_COUNTRY).observe(viewLifecycleOwner) { sportResource: Resource<List<Sport>>? ->
            if (sportResource != null) {
                when (sportResource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.viewError.root.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.viewError.root.visibility = View.GONE
                        sports = sportResource.data.orEmpty()
                        filterTeam(binding.searchView.query?.toString())
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.viewError.root.visibility = View.VISIBLE
                        binding.viewError.tvError.text =
                            sportResource.message ?: getString(R.string.something_wrong)
                    }
                }
            }
        }
    }

    private fun filterTeam(teamName: String?) {
        val filteredData = if (teamName.isNullOrBlank()) {
            sports
        } else {
            sports.filter { it.strTeam?.contains(teamName, ignoreCase = true) == true }
        }
        sportAdapter.setData(filteredData)
    }

    override fun onDestroyView() {
        binding.searchView.setOnQueryTextListener(null)
        sportAdapter.onItemClick = null
        binding.rvTeam.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
