package com.example.storyapp.data

import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.Response.ListStoryItem
import com.example.storyapp.datastore.TokenPreference
import com.example.storyapp.datastore.dataStore
import com.example.storyapp.story.StoryViewModel

class StoryPagingSource(private val apiService: ApiService): PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getstories(position, params.loadSize).listStory
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }


}