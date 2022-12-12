package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.database.Game
import com.fbt.mafia.database.HistoryPlayers
import com.fbt.mafia.databinding.EndGameBinding
import java.text.SimpleDateFormat
import java.util.*

class EndGame : Fragment() {
    private var _binding: EndGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EndGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var endGameLabel: TextView
    private var didMafiaWin = false

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        endGameLabel = view.findViewById(R.id.endGameLabel)

        if (arguments?.getBoolean("didMafiaWin")!=null){
            didMafiaWin = arguments?.getBoolean("didMafiaWin")!!
        }

        if (didMafiaWin){
            this.view?.setBackgroundColor(Color.BLACK)
            endGameLabel.text = "Победа мафии"
        }
        else{
            this.view?.setBackgroundColor(Color.rgb(176, 0, 32))
            endGameLabel.text = "Победа города"
        }

        binding.endGamePlayers.setOnClickListener {
            getPlayersListInDialog()
        }

        binding.theEnd.setOnClickListener {
            addToDB()
            findNavController().navigate(R.id.action_endGame_to_MainMenu)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) { }
    }

    private fun getPlayersListInDialog(){
        val viewInflated: View = LayoutInflater.from(context).inflate(R.layout.show_players_list, view as ViewGroup?, false)

        val playersListNight = viewInflated.findViewById<View>(R.id.playersListNight) as RecyclerView
        playersListNight.layoutManager = LinearLayoutManager(this.context)
        playersListNight.adapter = RecyclerEveryPlayersAdapter(Night.namesFull)

        AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("Игроки:")
            .setView(viewInflated)
            .setPositiveButton("Готово"){ dialog, _ -> dialog.dismiss() }
            .show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addToDB(){
        val db = AppDatabase.getDB(requireActivity().applicationContext)

        val time = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())
        if (didMafiaWin){
            db.gameDao().insert(Game(0, time, 1))
        }
        else{
            db.gameDao().insert(Game(0, time, 0))
        }
        val gameID = db.gameDao().findByTime(time).id

        for (item in Night.namesFull){
            val playerID = db.playerDao().findByNickname(item.split('.')[2]).id
            db.historyPlayersDao().insert(HistoryPlayers(0, gameID, playerID, item.split('.')[1]))
        }

        for (i in 0 until Day.historyActionsList.size){
            Day.historyActionsList[i].id_game = gameID
            db.historyActionsDao().insert(Day.historyActionsList[i])
        }
    }
}