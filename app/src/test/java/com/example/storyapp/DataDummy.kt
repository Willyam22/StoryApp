package com.example.storyapp

import com.example.storyapp.data.Response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
            )
            items.add(story )
        }
        return items
    }
}