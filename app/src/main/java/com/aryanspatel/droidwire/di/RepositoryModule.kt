package com.aryanspatel.droidwire.di

import com.aryanspatel.droidwire.data.repository.ArticleRepositoryImp
import com.aryanspatel.droidwire.domain.repo.ArticleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{
    @Binds
    abstract fun bindArticleRepository(
        imp: ArticleRepositoryImp
    ) : ArticleRepository
}