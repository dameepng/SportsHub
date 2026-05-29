package com.example.sportshub.core.domain.model

data class Sport(
    val idTeam: String?,
    val strTeam: String?,
    val strTeamAlternate: String?,
    val strCountry: String?,
    val strSport: String?,
    val strBadge: String?,
    val intFormedYear: String? = null,
    val strFanart1: String? = null,
    val strDescriptionEN: String? = null,
    val isFavorite: Boolean = false
)
