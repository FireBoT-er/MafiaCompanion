package com.fbt.mafia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.databinding.HistoryBinding

class History : Fragment() {
    private var _binding: HistoryBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = HistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var noGamesLabel: TextView
    private lateinit var gamesDBList: RecyclerView
    private var games : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gamesDBList = view.findViewById(R.id.gamesDBList)
        gamesDBList.layoutManager = LinearLayoutManager(this.context)

        val db = AppDatabase.getDB(requireActivity().applicationContext)
        for (game in db.gameDao().getAll()){
            games.add(game.dateTime)
            gamesDBList.adapter?.notifyItemInserted(games.size)
        }

        val adapter = RecyclerDBAdapter(games, this, false)
        gamesDBList.adapter = adapter

        if (games.size==0){
            noGamesLabel = view.findViewById(R.id.noGamesLabel)
            noGamesLabel.isGone = false
        }

        binding.backGames.setOnClickListener {
            findNavController().navigate(R.id.action_history_to_MainMenu)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_history_to_MainMenu)
        }
    }
}