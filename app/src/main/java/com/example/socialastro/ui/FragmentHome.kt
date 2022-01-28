package com.example.socialastro.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.example.socialastro.R
import com.example.socialastro.databinding.FragmentHomeBinding
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.model.TypeButton
import com.example.socialastro.recycler.PostAdapter
import com.example.socialastro.viewmodel.FavoritesViewModel
import com.example.socialastro.viewmodel.HomeViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FragmentHome : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var skeleton: Skeleton
    private lateinit var adapter: PostAdapter
    private val viewModel by sharedViewModel<HomeViewModel>()
    private val viewModelFaorites by sharedViewModel<FavoritesViewModel>()
    private var listFavorites = listOf<ModelArticle>()
    private var listPosts: MutableList<ModelArticle> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding?.root


        setupAdapter()
        setupSwipe()
        setupObservers()
        viewModel.loadList(false)



        return view
    }

    private fun setupSwipe() {
        binding?.swipe?.setOnRefreshListener {
            viewModel.loadList(true)
        }
    }

    private fun setupObservers() {
        observeLoading()
        observeFavorites()
        observeListArticles()
        observeError()
        observeTopListId()
    }

    private fun observeTopListId() {
        viewModel.topListId.observe(viewLifecycleOwner) { topToList ->
            if (topToList.top && topToList.id == R.id.idHome) {
                binding?.recyclerView?.scrollToPosition(0)
                topToList.top = false
                viewModel.backToTopList(topToList)
            }
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            stopRefreshingSwipe()
            if (loading) {
                Log.e("FDJSJS", "Cheguei $loading")
                skeleton.showSkeleton()
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.layoutRecentsEmpty?.visibility = View.GONE

            } else {
                Log.e("FDJSJS", "Cheguei $loading")
                skeleton.showOriginal()
            }
        }
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) {
            stopRefreshingSwipe()
            if (it.error) {
                view?.let { view -> Snackbar.make(view, it.info, Snackbar.LENGTH_SHORT).show() }
                binding?.layoutRecentsEmpty?.visibility = View.VISIBLE
                binding?.recyclerView?.visibility = View.GONE
                viewModel.viewModelScope.launch {
                    viewModel.flowTimer.collect {
                        binding?.buttonRecarregar?.isEnabled = it == "Recarregar"
                        binding?.buttonRecarregar?.text = it
                    }
                }
                binding?.buttonRecarregar?.setOnClickListener {
                    viewModel.loadList(true)
                }
            } else {
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.layoutRecentsEmpty?.visibility = View.GONE
            }
        }
    }

    private fun stopRefreshingSwipe() {
        if (binding?.swipe?.isRefreshing == true) {
            binding?.swipe?.isRefreshing = false
        }
    }


    private fun observeListArticles() {
        viewModel.listArticle.observe(viewLifecycleOwner) { list ->
            fillFavorites(list)
            listPosts.run {
                clear()
                addAll(list)
            }
            setupAdapter()
        }
    }

    private fun fillFavorites(list: List<ModelArticle>) {
        list.map { post ->
            for (item in listFavorites) {
                if (post.id == item.id) {
                    post.featured = item.featured
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelFaorites.listFavorites.observe(viewLifecycleOwner) { favorites ->
            if (favorites != null) {
                listFavorites = favorites
            }
        }
    }

    private fun setupAdapter() {
        adapter = PostAdapter(requireContext(), listPosts)
        adapter.setOnItemClickListener { item, type ->
            when (type) {
                TypeButton.FAVORITES -> {
                    viewModelFaorites.alterToFavorites(item, !item.featured)
                    adapter.notifyDataSetChanged()
                }
                TypeButton.ABRIR_LINK -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.url)
                        startActivity(this)
                    }
                }
            }
        }
        binding!!.recyclerView.adapter = adapter
        setupSkeleton()
        //skeleton.showSkeleton()
    }

    private fun setupSkeleton() {
        skeleton = binding?.recyclerView!!.applySkeleton(R.layout.item_articles, 3)
        skeleton.shimmerDurationInMillis = 1500
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}