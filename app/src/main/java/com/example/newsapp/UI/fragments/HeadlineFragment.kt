package com.example.newsapp.UI.fragments

import android.content.Context
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.UI.NewsActivity
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.Util.Resource
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentHeadlineBinding
import org.w3c.dom.Text

class HeadlineFragment : Fragment() {
lateinit var binding: FragmentHeadlineBinding
lateinit var newsViewModel: NewsViewModel
lateinit var retryButton :Button
lateinit var errorText : TextView
lateinit var itemHeadlineError :CardView
lateinit var newsAdapter : NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_headline, container, false)

        itemHeadlineError= binding.itemHeadlinesError as CardView


       val view:View= inflater.inflate(R.layout.item_error, container, false)
        itemHeadlineError.addView(view) // Add the inflated error layout to the card view

        errorText= view.findViewById(R.id.errorText)

        newsViewModel= (activity as NewsActivity).newsViewModel
        setupHeadlinesRecyclerview()

        newsAdapter.setOnItemClickListener { article ->
            if (article.url.isNotEmpty()) { // Check if the URL is not null or empty
                val bundle = Bundle().apply {
                    putSerializable("article", article)

                }

                findNavController().navigate(R.id.action_headlineFragment_to_articleFragment, bundle)
            } else {
                // Handle the scenario where the URL is null or empty
                Toast.makeText(activity, "Article URL is unavailable", Toast.LENGTH_SHORT).show()
            }
        }




        newsViewModel.headlines.observe(viewLifecycleOwner, Observer {response->
           when(response){
               is Resource.Success<*>->{
                   hideProgressBar()
                   hideErrorMessage()

                   response.data?.let {
                       newsAdapter.differ.submitList(it.articles.toList())
                       val totalPages= it.totalResults / com.example.newsapp.Util.Constants.query_page_size +2
                       isLastPage=newsViewModel.headlinesPage == totalPages
                       if(isLastPage){
                           binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
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

        // Set click listener for retry button
           retryButton=view.findViewById(R.id.retryButton)

           retryButton.setOnClickListener{
            if (newsViewModel.internetConnection(it.context)) { // Check for internet connectivity
                // Retry fetching headlines
                newsViewModel.getHeadlines("us")
                hideErrorMessage() // Hide error message layout
            } else {
                // Show error message indicating no internet connection
                Toast.makeText(activity, "No internet connection available", Toast.LENGTH_SHORT).show()
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
        itemHeadlineError.visibility=View.INVISIBLE
        isError= false
    }

    private fun showErrorMessage(message:String){
        itemHeadlineError.visibility=View.VISIBLE
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
                newsViewModel.getHeadlines("us")
                isScrolling = false
            }
        }
    }
        private fun setupHeadlinesRecyclerview(){
            newsAdapter= NewsAdapter()
            binding.recyclerHeadlines.apply {
                adapter= newsAdapter
                layoutManager=LinearLayoutManager(activity)
                addOnScrollListener(this@HeadlineFragment.scrollListener)
            }
        }


}