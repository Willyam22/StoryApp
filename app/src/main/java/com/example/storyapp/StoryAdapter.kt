package com.example.storyapp

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.detailed.DetailActivity
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            binding.txtUsername.text ="${story.name}"
            Glide.with(itemView.context)
                .load("${story.photoUrl}")
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener{
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                val optionsCompat:ActivityOptionsCompat=
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgItemPhoto, "profile"),
                        Pair(binding.txtUsername, "name")

                    )
                intentDetail.putExtra(DetailActivity.EXTRA_ID, "{${story.id}}")
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StoryAdapter.ListViewHolder, position: Int) {
        val story= getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}