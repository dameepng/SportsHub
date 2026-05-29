package com.example.sportshub.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sportshub.core.domain.common.Resource
import com.example.sportshub.core.domain.model.Sport
import com.example.sportshub.core.domain.usecase.SportUseCase
import androidx.lifecycle.toLiveData

class HomeViewModel(private val sportUseCase: SportUseCase) : ViewModel() {

    fun getSports(sport: String, country: String): LiveData<Resource<List<Sport>>> {
        return sportUseCase.getAllSport(sport, country).toLiveData()
    }
}
