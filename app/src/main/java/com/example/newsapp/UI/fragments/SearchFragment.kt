package com.example.newsapp.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.UI.NewsActivity
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.Util.Constants
import com.example.newsapp.Util.Constants.Companion.Search_time_delay
import com.example.newsapp.Util.Resource
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentFavouriteBinding
import com.example.newsapp.databinding.FragmentSearchBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

lateinit var binding: FragmentSearchBinding
lateinit var newsViewModel: NewsViewModel
lateinit var retryButton: Button
lateinit var errorText: TextView
lateinit var itemSearchError :CardView
lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding= DataBindingUtil.inflate(inflater, R.layout.fragment_search, container,false)

        itemSearchError= binding.itemSearchError as CardView

        val view:View= inflater.inflate(R.layout.item_error, null)

        retryButton= view.findViewById(R.id.retryButton)
        errorText= view.findViewById(R.id.errorText)

        newsViewModel= (activity as NewsActivity).newsViewModel
        setUpSearchRecyclerview()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_searchFragment_to_articleFragment)
        }

        var job: Job?= null
        binding.searchEdit.addTextChangedListener(){editable->
            job?.cancel()
            job = MainScope().launch {
                delay(Search_time_delay)
                editable?.let{
                    if(editable.toString().isNotEmpty()){
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }
        }


        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success<*>->{
                    hideProgressBar()
                    hideErrorMessage()

                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages= it.totalResults / Constants.query_page_size +2
                        isLastPage=newsViewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }

                    }
                }
                is Resource.Error<*>->{
                    hideProgressBar()
                    response.message?.let {message->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_SHORT).show()
                        showErrorMessage(message)

                    }
                }
                is Resource.Loading<*> ->{
                    showProgressBar()
                }


            }
        })

        retryButton.setOnClickListener{
            if(binding.searchEdit.text.toString().isNotEmpty()){
                newsViewModel.searchNews(binding.searchEdit.text.toString())
            }
            else{
                hideErrorMessage()
            }
        }


        return binding.root
    }


    var isError=false
    var isLoading=false
    var isLastPage=false
    var isScrolling=false

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility= View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility= View.INVISIBLE
        isLoading= true
    }

    private fun hideErrorMessage(){
        itemSearchError.visibility=View.INVISIBLE
        isError= false
    }

    private fun showErrorMessage(message:String){
        itemSearchError.visibility=View.VISIBLE
        errorText.text=message
        isError=true
    }


    //pagination
    val scrollListener= object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginnning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible =
                totalItemCount >= com.example.newsapp.Util.Constants.query_page_size
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginnning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = false
            }



        }
    }

    private fun setUpSearchRecyclerview() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

}