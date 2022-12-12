package com.fbt.mafia

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerEveryPlayersAdapter(private val dead: ArrayList<String>) : RecyclerView.Adapter<RecyclerEveryPlayersAdapter.Player>() {
    class Player(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var everyPlayerBase: LinearLayout = itemView.findViewById(R.id.everyPlayerBase)
        var number: TextView = itemView.findViewById(R.id.number)
        var roleLetter: TextView = itemView.findViewById(R.id.roleLetter)
        var name: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Player {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_every_player, parent, false)
        return Player(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Player, position: Int) {
        val deadInfo = dead[position].split('.')

        holder.number.text = deadInfo[0]+"."
        holder.roleLetter.text = deadInfo[1]
        holder.name.text = deadInfo[2]

        if (Day.names.contains(holder.name.text)){
            holder.everyPlayerBase.setBackgroundColor(Color.rgb(76, 175, 80))
        }
        else if (GameInfo.cityWin == true){
            holder.everyPlayerBase.setBackgroundColor(Color.rgb(176, 0, 32))
        }
        else if (GameInfo.cityWin == false){
            holder.everyPlayerBase.setBackgroundColor(Color.BLACK)
        }
    }

    override fun getItemCount() = dead.size
}