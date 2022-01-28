package com.example.socialastro.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.socialastro.R
import com.example.socialastro.databinding.FragmentNewPostBinding
import com.example.socialastro.dialog.DialogFilter
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.model.ModelFilter
import com.example.socialastro.model.TypeButton
import com.example.socialastro.recycler.PostAdapter
import com.example.socialastro.viewmodel.FavoritesViewModel
import com.example.socialastro.viewmodel.HomeViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FragmentNewPost : Fragment() {

    private var change: Boolean = false
    private var binding: FragmentNewPostBinding? = null
    private val viewModel by sharedViewModel<HomeViewModel>()
    private val viewModelFavorites by sharedViewModel<FavoritesViewModel>()
    private lateinit var adapter: PostAdapter
    private lateinit var filter: ModelFilter
    private val list: MutableList<ModelArticle> = mutableListOf()
    private lateinit var skeleton: Skeleton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val view = binding?.root


        setupObservers()
        viewModel.getFilter()
        setupAdapter()
        setupSkeleton()
        setupButtons()

        return view

    }

    private fun setupButtons() {
        binding!!.buttonFilter.setOnClickListener {
            DialogFilter(requireContext()).apply {
                setInitialConfiguration(filter)
                setOnClickListener {
                    viewModel.saveFilter(it)
                    binding?.recyclerViewAllPost?.scrollToPosition(0)
                }
            }.show()
        }

        binding!!.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun setupObservers() {
        viewModel.filter.observe(viewLifecycleOwner) {
            filter = it
            change = true
            getListArticles(filter)
        }
        viewModel.topListId.observe(viewLifecycleOwner) { topToList ->
            if (topToList.top && topToList.id == R.id.idNewPosts) {
                binding?.recyclerViewAllPost?.scrollToPosition(0)
                topToList.top = false
                viewModel.backToTopList(topToList)
            }
        }
    }

    private fun getListArticles(filter: ModelFilter) {
        viewModel.allListArticles(filter).observe(viewLifecycleOwner) {
            it?.toMutableList()?.let { lista -> adapter.setList(lista) }
            if (change) {
                it?.let {
                    list.run {
                        clear()
                        addAll(it)
                        binding?.search?.apply {
                            setQuery("", false)
                            clearFocus()
                        }
                    }
                }
            }
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
            change = false
        }
    }

    private fun setupAdapter() {
        setupSkeleton()
        adapter = PostAdapter(requireContext(), list)
        adapter.setOnItemClickListener { item, type ->
            when (type) {
                TypeButton.FAVORITES -> {
                    viewModelFavorites.alterToFavorites(item, !item.featured)
                    viewModel.loadList(true)

                }
                TypeButton.ABRIR_LINK -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.url)
                        startActivity(this)
                    }
                }
            }
        }

        binding!!.recyclerViewAllPost.adapter = adapter
        setupToolbarWithScroll()
    }

    private fun setupToolbarWithScroll() {
        binding!!.recyclerViewAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var state = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state = newState

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0 && (state == 0 || state == 2)) {
                    binding!!.appBar.visibility = View.GONE

                } else {
                    if (dy <= 0)
                        binding!!.appBar.visibility = View.VISIBLE
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setupSkeleton() {
        skeleton = binding!!.recyclerViewAllPost.applySkeleton(R.layout.item_articles, 3)
        skeleton.shimmerDurationInMillis = 1500
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStop() {
        binding!!.search.setQuery("", false)
        super.onStop()
    }

}