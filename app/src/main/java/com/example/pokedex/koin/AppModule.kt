package com.example.pokedex.koin

import com.example.pokedex.ktor.PokemonApi
import com.example.pokedex.ktor.KtorClient
import com.example.pokedex.ui.home.HomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        KtorClient.httpClient
    }
    single {
        PokemonApi()
    }
    single<PokemonRepository> {
        PokemonRepositoryImpl(get())
    }
    viewModel {
        HomeScreenViewModel(get())
    }
}