package com.fbt.mafia

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerDeadPlayersAdapter(private val dead: ArrayList<String>) : RecyclerView.Adapter<RecyclerDeadPlayersAdapter.Player>() {
    class Player(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var number: TextView = itemView.findViewById(R.id.number)
        var roleLetter: TextView = itemView.findViewById(R.id.roleLetter)
        var name: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Player {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dead_player, parent, false)
        return Player(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Player, position: Int) {
        val deadInfo = dead[position].split('.')

        holder.number.text = deadInfo[0]+"."
        holder.roleLetter.text = deadInfo[1]
        holder.name.text = deadInfo[2]
    }

    override fun getItemCount() = dead.size
}