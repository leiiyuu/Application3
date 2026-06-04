package com.example.application3.data

import com.example.application3.model.Post

interface IPostRepository {
    suspend fun getPosts(): List<Post>
    suspend fun searchPostsByUser(userId: Int): List<Post>
    suspend fun getPostById(id: Int): Post
}