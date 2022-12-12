package com.fbt.mafia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView

class RecyclerDBAdapter(private val items: ArrayList<String>, private val fragment : Fragment, private val isPlayers: Boolean) : RecyclerView.Adapter<RecyclerDBAdapter.DBItem>() {
    class DBItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button = itemView.findViewById(R.id.openDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBItem {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_db, parent, false)
        return DBItem(itemView)
    }

    override fun onBindViewHolder(holder: DBItem, position: Int) {
        holder.button.text = items[position]

        holder.button.setOnClickListener {
            if (isPlayers){
                findNavController(fragment).navigate(R.id.action_playersDB_to_playerInfo, bundleOf("name" to items[position]))
            }
            else{
                findNavController(fragment).navigate(R.id.action_history_to_gameInfo, bundleOf("dateTime" to items[position]))
            }
        }
    }

    override fun getItemCount() = items.size
}