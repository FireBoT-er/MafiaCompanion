package com.fbt.mafia

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.databinding.MainMenuBinding
import kotlin.system.exitProcess


class MainMenu : Fragment() {

    private var _binding: MainMenuBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainMenu_to_AddPlayers)
        }

        binding.playersButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainMenu_to_playersDB)
        }

        binding.historyButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainMenu_to_history)
        }

        binding.aboutButton.setOnClickListener {
            AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
                .setTitle("Об авторе")
                .setMessage("Емельянов Владислав\nvlademel2016@yandex.ru\nМИВлГУ, ФИТР, ПИн-119\n2022 г.")
                .setPositiveButton("Ок") {
                    dialog, _ -> dialog.dismiss()
                }
                .show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Вы действительно хотите выйти?")
            builder.setPositiveButton("Да") { _, _ -> finishAffinity(requireActivity()); exitProcess(0) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }

        restartGame()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun restartGame(){
            AddPlayers.restart()
            Day.restart()
            DayPlayers.restart()
            DayVote.restart()
            Night.restart()
            StartDayOrNight.restart()
        }
    }
}