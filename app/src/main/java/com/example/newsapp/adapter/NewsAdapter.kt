package com.example.newsapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.UI.fragments.HeadlineFragment
import com.example.newsapp.databinding.ItemNewsBinding
import com.example.newsapp.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){


    class ArticleViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root){

    }

    private val differCallBack= object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return  oldItem.url== newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }

     val differ= AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
       return ArticleViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
           return differ.currentList.size
    }

    private var onItemClickLister: ((Article) -> Unit)?=null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
         val article= differ.currentList[position]

        holder.binding.article= article



        holder.binding.root.setOnClickListener {
           onItemClickLister?.let {
               it(article)
           }

        }


    }

    fun setOnItemClickListener(listener : (Article)-> Unit){
        onItemClickLister=listener
    }


}