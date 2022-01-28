package com.example.socialastro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialastro.model.*
import com.example.socialastro.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class HomeViewModel(val repository: HomeRepository) : ViewModel() {
    private val _listArticles = MutableLiveData<List<ModelArticle>>()
    val listArticle: LiveData<List<ModelArticle>> get() = _listArticles

    private val _topListId = MutableLiveData<TopToList>()
    val topListId: LiveData<TopToList> get() = _topListId


    fun allListArticles(filter: ModelFilter) : LiveData<List<ModelArticle>?> {
        return repository.getPostsInternal(filter)
    }

    fun backToTopList(topToList: TopToList) {
        _topListId.postValue(topToList)
    }

    val _filter = MutableLiveData<ModelFilter>()
    val filter: LiveData<ModelFilter> = _filter

    private val _error = MutableLiveData<ErrorInfo>()
    val error: LiveData<ErrorInfo> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    val flowTimer : Flow<String> = flow {
        for (i in 5 downTo 1){
            emit(""+i)
            delay(1000)
        }
        emit("Recarregar")
    }

    fun loadList(force: Boolean) {
        if (!force) {
            if (_listArticles.value != null) return
            loadingPosts()
        } else {
            loadingPosts()
        }

    }

    private fun loadingPosts() {
        viewModelScope.launch {
            repository.loadPostss()
                .onStart {
                    showLoading()
                    delay(700)
                }
                .catch {
                    stopLoading()
                }
                .collect {
                    stopLoading()
                    when (it) {
                        is UnknownHostException -> {
                            _error.postValue(
                                ErrorInfo(
                                    true,
                                    "Não foi possível carregar as informações"
                                )
                            )
                            return@collect
                        }
                    }
                    _error.postValue(ErrorInfo(false))
                    _listArticles.postValue(it as List<ModelArticle>?)
                }
        }
    }

    fun showLoading() {
        _loading.value = true
    }

    fun stopLoading() {
        _loading.value = false
    }

    fun saveFilter(filter: ModelFilter) {
        _filter.postValue(filter)
        repository.savePreferences(filter)
    }

    fun getFilter() {
        _filter.postValue(repository.getFilter())
    }
}