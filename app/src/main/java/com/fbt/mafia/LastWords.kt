package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.databinding.LastWordsBinding

class LastWords : Fragment() {
    private var _binding: LastWordsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LastWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var playerName: TextView
    private lateinit var timerLastWords: TextView
    private lateinit var nextWords: Button

    private var deadNames : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deadNames = arguments?.getStringArrayList("deadNames")!!

        playerName = view.findViewById(R.id.playerName)
        timerLastWords = view.findViewById(R.id.timerLastWords)
        nextWords = view.findViewById(R.id.nextTime)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val namesToMenu : ArrayList<String> = arrayListOf()
            for (item in Night.namesFull){
                namesToMenu.add(item.split('.')[2])
            }

            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Если вернуться, игра будет начата заново")
            builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_lastWords_to_AddPlayers, bundleOf("names" to namesToMenu)) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }

        nextWords.setOnClickListener {
            if (deadNumber>=deadNames.size){
                if (StartDayOrNight.isDay){
                    findNavController().navigate(R.id.action_lastWords_to_day)
                }
                else{
                    findNavController().navigate(R.id.action_lastWords_to_dayNightStart)
                }
            }
            else{
                startSpeaking()
            }
        }

        startSpeaking()
    }

    private var deadNumber : Int = 0
    private fun startSpeaking(){
        if (timer != null){
            timer?.cancel()
        }

        playerName.text = deadNames[deadNumber++].split('.')[1].removeRange(0, 1)
        timer()
    }

    private var timer: CountDownTimer? = null

    private fun timer(){
        timer = object : CountDownTimer(65000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished/1000>=60){
                    timerLastWords.text = "01:0"+millisUntilFinished/1000%10
                }
                else if (millisUntilFinished/1000<10){
                    timerLastWords.text = "00:0"+millisUntilFinished/1000
                }
                else {
                    timerLastWords.text = "00:" + millisUntilFinished/1000
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerLastWords.text = "00:00"
            }
        }.start()
    }
}