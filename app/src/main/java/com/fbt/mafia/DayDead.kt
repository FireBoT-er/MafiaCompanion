package com.fbt.mafia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.databinding.DayDeadBinding

class DayDead : Fragment() {
    private var _binding: DayDeadBinding? = null
    private val binding get() = _binding!!

    private lateinit var deadPlayersList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DayDeadBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var dead : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deadPlayersList = view.findViewById(R.id.deadPlayersList)
        deadPlayersList.layoutManager = LinearLayoutManager(this.context)

        dead = Day.dead

        val adapter = RecyclerDeadPlayersAdapter(dead)
        deadPlayersList.adapter = adapter
    }
}