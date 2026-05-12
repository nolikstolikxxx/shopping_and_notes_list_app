package com.example.shoppingAndNotesListApp.core.utils

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingAndNotesListApp.data.repository.MainRepository
import com.example.shoppingAndNotesListApp.ui.activities.MainApp
import com.example.shoppingAndNotesListApp.ui.viewmodel.MainViewModel

/**
 * Provides MainViewModel for Activity
 */
fun ComponentActivity.mainViewModel(): Lazy<MainViewModel> = lazy {
    val dao = (application as MainApp).dataBase.getDao()
    val repository = MainRepository(dao)

    ViewModelProvider(
        this ,
        MainViewModel.MainViewModelFactory(repository)
    )[MainViewModel::class.java]
}

/**
 * Provides MainViewModel for Fragment (shared with Activity)
 */
fun Fragment.mainViewModel(): Lazy<MainViewModel> = lazy {
    val dao = (requireActivity().application as MainApp).dataBase.getDao()
    val repository = MainRepository(dao)

    ViewModelProvider(
        requireActivity() ,
        MainViewModel.MainViewModelFactory(repository)
    )[MainViewModel::class.java]
}