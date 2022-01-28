package com.example.socialastro.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.socialastro.R
import com.example.socialastro.databinding.FragmentSettingsBinding
import com.example.socialastro.dialog.DialogYesNo
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.model.TypeButton
import com.example.socialastro.recycler.PostAdapter
import com.example.socialastro.viewmodel.FavoritesViewModel
import com.example.socialastro.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentSettings : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private val viewModel: FavoritesViewModel by sharedViewModel()
    private val viewModelHome: HomeViewModel by sharedViewModel()
    private val list: MutableList<ModelArticle> = mutableListOf()
    private var qtd: Int = 0
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding?.root

        setupObservers()
        setupAdapter()
        setupButtonFavorites()

        return view
    }

    private fun setupButtonFavorites() {
        binding!!.favoritesButton.setOnClickListener {
            if (qtd != 0) {
                DialogYesNo(requireContext()).apply {
                    setOnClickListener {
                        if (it == "yes") {
                            viewModel.deleteFavorites()
                            viewModelHome.loadList(true)
                            dismiss()
                        } else {
                            dismiss()
                        }
                    }
                    show()
                }
            }
        }
    }

    private fun setupObservers() {
        observeListFavorites()
        observeTopListId()

    }

    private fun observeTopListId() {
        viewModelHome.topListId.observe(viewLifecycleOwner) { topToList ->
            if (topToList.top && topToList.id == R.id.idSettings) {
                binding?.recyclerViewFavorites?.scrollToPosition(0)
                topToList.top = false
                viewModelHome.backToTopList(topToList)
            }
        }
    }

    private fun observeListFavorites() {
        viewModel.listFavorites.observe(viewLifecycleOwner) {
            fillQtdFavorites(it)
            if (it?.isNotEmpty() == true) {
                list.run {
                    clear()
                    addAll(it)
                }
                binding?.recyclerViewFavorites?.visibility = View.VISIBLE
                binding?.layoutFavoritesEmpty?.visibility = View.GONE
                activity?.runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            } else {
                binding?.recyclerViewFavorites?.visibility = View.GONE
                binding?.layoutFavoritesEmpty?.visibility = View.VISIBLE
            }
        }
    }

    private fun fillQtdFavorites(it: List<ModelArticle>?) {
        binding?.qtdFavorites?.text = it?.size?.let { size ->
            qtd = size
            setupImageFavorites(size)
        }
    }

    private fun setupImageFavorites(size: Int): String {
        if (size >= 1) {
            binding?.favoritesButton?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            return size.toString()
        }
        binding?.favoritesButton?.setColorFilter(ContextCompat.getColor(requireContext(),
            R.color.black
        ))
        return ""
    }


    private fun setupAdapter() {
        adapter = PostAdapter(requireContext(), list)
        adapter.setOnItemClickListener { item, type ->
            when (type) {
                TypeButton.FAVORITES -> {
                    viewModel.alterToFavorites(item, !item.featured)
                    viewModelHome.loadList(true)
                }
                TypeButton.ABRIR_LINK -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.url)
                        startActivity(this)
                    }
                }
            }
        }
        binding?.recyclerViewFavorites?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}