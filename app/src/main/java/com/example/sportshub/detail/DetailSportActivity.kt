package com.example.sportshub.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sportshub.R
import com.example.sportshub.core.domain.model.Sport
import com.example.sportshub.databinding.ActivityDetailSportBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailSportActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ID_TEAM = "extra_id_team"
        private const val EXTRA_TEAM = "extra_team"
        private const val EXTRA_TEAM_ALTERNATE = "extra_team_alternate"
        private const val EXTRA_COUNTRY = "extra_country"
        private const val EXTRA_SPORT = "extra_sport"
        private const val EXTRA_BADGE = "extra_badge"
        private const val EXTRA_FORMED_YEAR = "extra_formed_year"
        private const val EXTRA_FANART = "extra_fanart"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_IS_FAVORITE = "extra_is_favorite"

        fun createIntent(context: Context, sport: Sport): Intent {
            return Intent(context, DetailSportActivity::class.java).apply {
                putExtra(EXTRA_ID_TEAM, sport.idTeam)
                putExtra(EXTRA_TEAM, sport.strTeam)
                putExtra(EXTRA_TEAM_ALTERNATE, sport.strTeamAlternate)
                putExtra(EXTRA_COUNTRY, sport.strCountry)
                putExtra(EXTRA_SPORT, sport.strSport)
                putExtra(EXTRA_BADGE, sport.strBadge)
                putExtra(EXTRA_FORMED_YEAR, sport.intFormedYear)
                putExtra(EXTRA_FANART, sport.strFanart1)
                putExtra(EXTRA_DESCRIPTION, sport.strDescriptionEN)
                putExtra(EXTRA_IS_FAVORITE, sport.isFavorite)
            }
        }
    }

    private val detailSportViewModel: DetailSportViewModel by viewModel()
    private lateinit var binding: ActivityDetailSportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showDetailSport(intent.toSport())

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun Intent.toSport(): Sport {
        return Sport(
            idTeam = getStringExtra(EXTRA_ID_TEAM),
            strTeam = getStringExtra(EXTRA_TEAM),
            strTeamAlternate = getStringExtra(EXTRA_TEAM_ALTERNATE),
            strCountry = getStringExtra(EXTRA_COUNTRY),
            strSport = getStringExtra(EXTRA_SPORT),
            strBadge = getStringExtra(EXTRA_BADGE),
            intFormedYear = getStringExtra(EXTRA_FORMED_YEAR),
            strFanart1 = getStringExtra(EXTRA_FANART),
            strDescriptionEN = getStringExtra(EXTRA_DESCRIPTION),
            isFavorite = getBooleanExtra(EXTRA_IS_FAVORITE, false)
        )
    }

    private fun showDetailSport(detailSport: Sport?) {
        detailSport?.let {
            supportActionBar?.title = detailSport.strTeam
            binding.categoryBadge.text = detailSport.strSport
            binding.fullName.text = detailSport.strTeam
            binding.subtitleText.text = getString(R.string.founded_in_format, detailSport.intFormedYear)
            binding.content.teamName.text = detailSport.strTeam
            binding.content.articleContent.text = detailSport.strDescriptionEN ?: ""
            Glide.with(this@DetailSportActivity)
                .load(detailSport.strBadge)
                .into(binding.headerImage)

            var statusFavorite = detailSport.isFavorite
            setStatusFavorite(statusFavorite)
            binding.saveButton.setOnClickListener {
                statusFavorite = !statusFavorite
                detailSportViewModel.setFavoriteSport(detailSport, statusFavorite)
                setStatusFavorite(statusFavorite)
            }
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_24))
        } else {
            binding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_border_24))
        }
    }
}
