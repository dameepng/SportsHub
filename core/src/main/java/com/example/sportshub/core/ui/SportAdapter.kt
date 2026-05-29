package com.example.sportshub.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportshub.core.databinding.ItemListSportBinding
import com.example.sportshub.core.domain.model.Sport

class SportAdapter : ListAdapter<Sport, SportAdapter.ListViewHolder>(SportDiffCallback) {

    var onItemClick: ((Sport) -> Unit)? = null

    fun setData(newListData: List<Sport>?) {
        submitList(newListData.orEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListViewHolder(
            ItemListSportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ListViewHolder(private val binding: ItemListSportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Sport) {
            with(binding) {
                Glide.with(articleImage)
                    .load(data.strBadge ?: "")
                    .into(articleImage)
                category.text = data.strSport ?: ""
                title.text = data.strTeam ?: ""
                author.text = data.strCountry ?: ""
                date.text = data.intFormedYear ?: ""
            }
        }

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }

    private object SportDiffCallback : DiffUtil.ItemCallback<Sport>() {
        override fun areItemsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return oldItem.idTeam == newItem.idTeam
        }

        override fun areContentsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return oldItem == newItem
        }
    }
}
