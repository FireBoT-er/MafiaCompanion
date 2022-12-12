package com.fbt.mafia

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class RecyclerAddPlayersAdapter(private val names: ArrayList<String>, private val dragStartListener: OnStartDragListener) : RecyclerView.Adapter<RecyclerAddPlayersAdapter.Player>(), ItemTouchHelperAdapter {
    class Player(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var number: TextView = itemView.findViewById(R.id.number)
        var name: TextView = itemView.findViewById(R.id.name)
        var handleView: ImageView = itemView.findViewById(R.id.handle)
        var delete: Button = itemView.findViewById(R.id.delete)
    }

    private var mDragStartListener: OnStartDragListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Player {
        mDragStartListener = dragStartListener
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_add_player, parent, false)
        return Player(itemView)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: Player, position: Int) {
        holder.number.text = (position+1).toString()+"."
        holder.name.text = names[position]

        holder.handleView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                mDragStartListener!!.onStartDrag(holder)
            }
            false
        }

        holder.delete.setOnClickListener {
            val playerNumber = names.indexOf(holder.name.text.toString())
            names.removeAt(playerNumber)
            notifyItemRemoved(playerNumber)

            AddPlayers.plusVisibilityChanger()

            for (i: Int in playerNumber..names.size) {
                notifyItemChanged(i)
            }
        }
    }

    override fun getItemCount() = names.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(names, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(names, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)

        for (i: Int in 0..names.size) {
            notifyItemChanged(i)
        }
    }

    override fun onItemDismiss(position: Int) {
        names.removeAt(position)
        notifyItemRemoved(position)
    }
}