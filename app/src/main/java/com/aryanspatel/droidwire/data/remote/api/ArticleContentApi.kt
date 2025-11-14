package com.aryanspatel.droidwire.data.remote.api

import com.aryanspatel.droidwire.data.remote.dto.ArticleDetailDto
import retrofit2.http.GET
import retrofit2.http.Url

interface ArticleContentApi {
    @GET
    suspend fun getDetail(@Url contentUrl: String): ArticleDetailDto
}
