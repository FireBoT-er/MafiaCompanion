package com.fbt.mafia

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.database.Player
import com.fbt.mafia.databinding.PlayersBinding

class Players : Fragment() {
    private var _binding: PlayersBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var playersDBList: RecyclerView
    private var names : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playersDBList = view.findViewById(R.id.playersDBList)
        playersDBList.layoutManager = LinearLayoutManager(this.context)

        val db = AppDatabase.getDB(requireActivity().applicationContext)
        for (player in db.playerDao().getAll()){
            names.add(player.nickname)
            playersDBList.adapter?.notifyItemInserted(names.size)
        }

        val adapter = RecyclerDBAdapter(names, this, true)
        playersDBList.adapter = adapter

        binding.buttonAddPlayer.setOnClickListener {
            addPlayer()
        }

        binding.backPlayers.setOnClickListener {
            findNavController().navigate(R.id.action_playersDB_to_MainMenu)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_playersDB_to_MainMenu)
        }
    }

    private fun addPlayer(){
        var newName : String

        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        builder.setTitle("Новый игрок")

        val viewInflated: View = LayoutInflater.from(context)
            .inflate(R.layout.add_temporary_player, view as ViewGroup?, false)

        val input = viewInflated.findViewById<View>(R.id.input) as EditText

        builder.setView(viewInflated)
        builder.setPositiveButton("ОК") {
            dialog, _ -> dialog.dismiss()

            if (input.text.toString().isNotEmpty()){
                newName = input.text.toString()

                if (!names.contains(newName)){
                    names.add(newName)
                    playersDBList.adapter?.notifyItemInserted(names.size)

                    val db = AppDatabase.getDB(requireActivity().applicationContext)
                    db.playerDao().insert(Player(0, newName, null))
                }
                else{
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("Ошибка")
                        .setMessage("Игрок с таким именем уже существует")
                        .setPositiveButton("Ок") { anotherDialog, _ -> anotherDialog.dismiss() }
                        .show()
                }

            }
        }
        builder.setNegativeButton("Отмена") {
            dialog, _ -> dialog.cancel()
        }

        builder.show()
    }
}