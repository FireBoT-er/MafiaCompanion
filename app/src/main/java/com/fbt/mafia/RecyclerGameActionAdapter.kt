package com.fbt.mafia

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerGameActionAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<RecyclerGameActionAdapter.DBItem>() {
    class DBItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var actionBase: LinearLayout = itemView.findViewById(R.id.actionBase)
        var actionInfo: TextView = itemView.findViewById(R.id.actionInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBItem {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_game_action, parent, false)
        return DBItem(itemView)
    }

    override fun onBindViewHolder(holder: DBItem, position: Int) {
        holder.actionInfo.text = items[position]

        if (GameInfo.cityWin == true){
            holder.actionBase.setBackgroundColor(Color.rgb(176, 0, 32))
        }
    }

    override fun getItemCount() = items.size
}