package com.example.application3.data

import com.example.application3.data.remote.JsonPlaceholderApi
import com.example.application3.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: JsonPlaceholderApi
) : IPostRepository {

    override suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        api.getPosts()
    }

    override suspend fun searchPostsByUser(userId: Int): List<Post> = withContext(Dispatchers.IO) {
        api.getPostsByUser(userId)
    }

    override suspend fun getPostById(id: Int): Post = withContext(Dispatchers.IO) {
        api.getPostById(id)
    }
}