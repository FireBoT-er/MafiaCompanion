package com.fbt.mafia

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.databinding.GameInfoBinding

class GameInfo : Fragment() {
    private var _binding: GameInfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GameInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var dateTimeLabel : TextView
    private lateinit var dateTime : String
    private lateinit var resultLabel : TextView

    private lateinit var gamePlayersList: RecyclerView
    private lateinit var gameActionsList: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateTimeLabel = view.findViewById(R.id.dateTimeLabel)
        resultLabel = view.findViewById(R.id.resultLabel)

        dateTime = arguments?.getString("dateTime")!!
        dateTimeLabel.text = dateTime

        val db = AppDatabase.getDB(requireActivity().applicationContext)
        val game = db.gameDao().findByTime(dateTime)
        if (game.didMafiaWin == 1){
            resultLabel.text = "Победа мафии"
        }
        else{
            resultLabel.text = "Победа города"
        }

        cityWin = if (resultLabel.text == "Победа города"){
            this.view?.setBackgroundColor(Color.rgb(176, 0, 32))
            true
        } else{
            this.view?.setBackgroundColor(Color.BLACK)
            false
        }

        gamePlayersList = view.findViewById(R.id.gamePlayersList)
        gamePlayersList.layoutManager = LinearLayoutManager(this.context)

        val historyPlayers = db.historyPlayersDao().loadAllByGamesIds(intArrayOf(game.id))
        val playersIDs : ArrayList<Int> = arrayListOf()
        for (record in historyPlayers){
            playersIDs.add(record.id_player)
        }

        val players = db.playerDao().loadAllByIds(playersIDs.toIntArray())
        val playersRecycler : ArrayList<String> = arrayListOf()
        for (i : Int in players.indices){
            playersRecycler.add((i+1).toString() + '.' + historyPlayers[i].role + '.' + players[i].nickname)
        }

        val adapter = RecyclerEveryPlayersAdapter(playersRecycler)
        gamePlayersList.adapter = adapter

        gameActionsList = view.findViewById(R.id.gameActionsList)
        gameActionsList.layoutManager = LinearLayoutManager(this.context)

        val historyActions = db.historyActionsDao().loadAllByGamesIds(intArrayOf(game.id))
        val actionsRecycler : ArrayList<String> = arrayListOf()
        var roundTmp = -1
        for (record in historyActions){
            if (roundTmp != record.round){
                if (actionsRecycler.isNotEmpty()){
                    actionsRecycler.add(" ")
                }

                if (record.action == 3 || record.action == 4){
                    actionsRecycler.add("Ночь " + record.round + ":")
                }
                else{
                    actionsRecycler.add("День " + record.round + ":")
                    roundTmp = record.round
                }
            }

            val nickname = db.playerDao().findByID(record.id_player).nickname
            actionsRecycler.add(when (record.action){
                1 -> "$nickname выставлен(а) на голосование"
                2 -> "$nickname выбыл(а) в результате голосования"
                3 -> "$nickname убит(а) мафией"
                else -> "Мафия промахнулась"
            })
        }
        actionsRecycler.add(" ")
        actionsRecycler.add("Игра окончена")

        val adapter2 = RecyclerGameActionAdapter(actionsRecycler)
        gameActionsList.adapter = adapter2

        binding.backGame.setOnClickListener {
            cityWin = null
            findNavController().navigate(R.id.action_gameInfo_to_history)
        }
    }

    companion object{
        var cityWin : Boolean? = null
    }
}