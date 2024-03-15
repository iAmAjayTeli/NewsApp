package com.example.newsapp.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.Util.Resource
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(val app: Application, val repository: NewsRepository) : AndroidViewModel(app){

    val headlines:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage=1
    var headlineResponse:NewsResponse ?= null

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage=1
    var searchNewsResponse:NewsResponse ?=null
    var newSearchQuery:String ?=null
    var oldSearchQuery: String?= null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String)=viewModelScope.launch(Dispatchers.IO) {
        headlineInternet(countryCode)
    }

    fun searchNews(searchQuery : String)=viewModelScope.launch(Dispatchers.IO)  {
        searchNewsInternet(searchQuery)
    }

    private  fun handleHeadlineResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                headlinesPage++
                if(headlineResponse==null){
                    headlineResponse=it
                }
                else{
                    val oldArticles= headlineResponse!!.articles?: emptyList()
                    val newArticles = it.articles
                   val combinedArticles= mutableListOf<Article>()
                    combinedArticles.addAll(oldArticles)
                    combinedArticles.addAll(newArticles)
                    headlineResponse!!.articles= combinedArticles
                }
                return Resource.Success(headlineResponse ?:it)
            }
        }
        return Resource.Error(response.message())
    }


   private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
       if(response.isSuccessful){
           response.body()?.let {
               if(searchNewsResponse ==null || newSearchQuery !=null){
                   searchNewsPage=1
                   oldSearchQuery=newSearchQuery
                   searchNewsResponse = it
               }
               else{
                   searchNewsPage++
                   val oldArticles= searchNewsResponse!!.articles?: emptyList()
                   val newArticles =it.articles
                   val combinedArticles= mutableListOf<Article>()
                   combinedArticles.addAll(oldArticles)
                   combinedArticles.addAll(newArticles)
                   searchNewsResponse!!.articles= combinedArticles
               }
               return Resource.Success(searchNewsResponse ?: it)
           }
       }
       return  Resource.Error(response.message())
   }

    //add to favourite method
    fun addToFavourite(article: Article)=viewModelScope.launch(Dispatchers.IO)  {
        repository.insert(article)
    }

    //get all news method
    fun getFavouriteNews()=repository.getAllArticles()

    //delete article method
    fun deleteArticle(article: Article)=viewModelScope.launch(Dispatchers.IO)  {
        repository.delete(article)
    }

    //checking for the internet connection method
    fun internetConnection(context:Context):Boolean{
        (context.getSystemService((Context.CONNECTIVITY_SERVICE)) as ConnectivityManager).apply {
            return  getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport((NetworkCapabilities.TRANSPORT_WIFI))-> true
                    hasTransport((NetworkCapabilities.TRANSPORT_CELLULAR))-> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else-> false
                }
            } ?:false


        }
    }

    private suspend fun headlineInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response =repository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlineResponse(response))
              }
            else{
                headlines.postValue(Resource.Error("No internet Connection"))
            }
    }
        catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable tp connect with the internet"))
                else -> headlines.postValue(Resource.Error("No signal"))
            }
           }
    }


    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery=searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response= repository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }
        catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect to the internet"))
                else -> searchNews.postValue(Resource.Error("No signal"))
            }
        }
    }

}