package com.example.newsapp.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.UI.NewsActivity
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {

    lateinit var newsViewModel: NewsViewModel
    lateinit var binding:FragmentArticleBinding
    val args:ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        // Check if the article argument is not null before using it
        if (article != null) {
            binding.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }

            binding.fab.setOnClickListener { view ->
                newsViewModel.addToFavourite(article)
                Snackbar.make(view, "Added to the favourites", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case where the article argument is missing
            // For example, you can show an error message or navigate back
            Snackbar.make(binding.root, "Article data is missing", Snackbar.LENGTH_SHORT).show()
        }

        return binding.root
    }
}