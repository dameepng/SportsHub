package com.example.sportshub.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SportResponse(

	@field:SerializedName("teams")
	val teams: List<TeamsItem?>? = null
)

data class TeamsItem(

	@field:SerializedName("strSport")
	val strSport: String? = null,

	@field:SerializedName("strBadge")
	val strBadge: String? = null,

	@field:SerializedName("strCountry")
	val strCountry: String? = null,

	@field:SerializedName("strFanart1")
	val strFanart1: String? = null,

	@field:SerializedName("intFormedYear")
	val intFormedYear: String? = null,

	@field:SerializedName("idTeam")
	val idTeam: String? = null,

	@field:SerializedName("strDescriptionEN")
	val strDescriptionEN: String? = null,

	@field:SerializedName("strTeamAlternate")
	val strTeamAlternate: String? = null,

	@field:SerializedName("strTeam")
	val strTeam: String? = null,
)
