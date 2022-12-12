package com.fbt.mafia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerVotePlayersAdapter(private val names: ArrayList<String>) : RecyclerView.Adapter<RecyclerVotePlayersAdapter.Player>() {
    class Player(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var delete: Button = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Player {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_vote_player, parent, false)
        return Player(itemView)
    }

    override fun onBindViewHolder(holder: Player, position: Int) {
        holder.name.text = names[position]

        holder.delete.setOnClickListener {
            val playerNumber = names.indexOf(holder.name.text.toString())
            DayVote.names.add(names.removeAt(playerNumber))
            DayVote.names.sortBy { it.split('.')[0].toInt() }
            DayVote.toVoteVisibilityChanger()
            notifyItemRemoved(playerNumber)
        }
    }

    override fun getItemCount() = names.size
}