package com.example.sportshub.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.example.sportshub.core.domain.usecase.SportUseCase

class FavoriteViewModel(sportUseCase: SportUseCase) : ViewModel() {
    val favoriteSport = sportUseCase.getFavoriteSport().toLiveData()
}
