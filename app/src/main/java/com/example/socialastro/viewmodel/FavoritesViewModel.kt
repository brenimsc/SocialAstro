package com.example.socialastro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.repository.FavoritesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(val repository: FavoritesRepository) : ViewModel() {
    val listFavorites: LiveData<List<ModelArticle>?> = repository.getAllFavorites()

    fun alterToFavorites(item: ModelArticle, featured: Boolean) {
        viewModelScope.launch {
            item.featured = featured
            repository.alterToFavorites(item)
        }
    }

    fun deleteFavorites() {
        viewModelScope.launch {
            repository.deleteFavorites()
        }
    }

}