package com.fbt.mafia

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class RecyclerPlayersAdapter(private val names: ArrayList<String>, private val seats: ArrayList<String>, private val roles: ArrayList<String>, private val timers: ArrayList<String>) : RecyclerView.Adapter<RecyclerPlayersAdapter.Player>() {
    class Player(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var number: TextView = itemView.findViewById(R.id.number)
        var roleLetter: TextView = itemView.findViewById(R.id.roleLetter)
        var name: TextView = itemView.findViewById(R.id.name)
        var timer: TextView = itemView.findViewById(R.id.timer)
        var foul: Button = itemView.findViewById(R.id.foul)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Player {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_player, parent, false)
        return Player(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Player, position: Int) {
        holder.number.text = seats[position]+"."

        if (roles[position]=="Мирный житель"){
            holder.roleLetter.text = "Ж"
        }
        else{
            holder.roleLetter.text = roles[position][0].toString()
        }

        holder.name.text = names[position]
        holder.timer.text = timers[position]

        holder.foul.setOnClickListener {
            DayPlayers.stopTimer(position)
        }
    }

    override fun getItemCount() = names.size
}