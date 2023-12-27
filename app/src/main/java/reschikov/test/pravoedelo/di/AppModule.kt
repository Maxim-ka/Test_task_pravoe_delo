package reschikov.test.pravoedelo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import reschikov.test.pravoedelo.data.IRequester
import reschikov.test.pravoedelo.data.Repository
import reschikov.test.pravoedelo.data.network.NetWorkProvider
import reschikov.test.pravoedelo.ui.screens.code.IRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindIRequester(netWorkProvider: NetWorkProvider) : IRequester

    @Binds
    @Singleton
    abstract fun bindRepository(repository: Repository) : IRepository
}