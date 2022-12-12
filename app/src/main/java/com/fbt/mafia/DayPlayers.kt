package com.fbt.mafia

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.databinding.DayPlayersBinding

class DayPlayers : Fragment() {
    private var _binding: DayPlayersBinding? = null
    private val binding get() = _binding!!

    private lateinit var playersList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DayPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var seats : ArrayList<String> = arrayListOf()
    private var roles : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playersList = view.findViewById(R.id.playersList)
        playersList.layoutManager = LinearLayoutManager(this.context)

        names = arguments?.getStringArrayList("names")!!
        seats = arguments?.getStringArrayList("seats")!!
        roles = arguments?.getStringArrayList("roles")!!

        val adapter = RecyclerPlayersAdapter(names, seats, roles, Day.timers)
        playersList.adapter = adapter

        pseudoConstructor(playersList)
        if (!isWorking){
            isWorking = true
            timer()
        }
    }

    companion object{
        private lateinit var playersList: RecyclerView
        private fun pseudoConstructor(_playersList: RecyclerView){
            playersList = _playersList
        }

        private var names : ArrayList<String> = arrayListOf()

        private var timerNumber: Int = 0
        private var timerExtraNumber: Int = 0
        private var timer: CountDownTimer? = null
        private var time: Long = 65000
        var isWorking : Boolean = false
        var isExtra : Boolean = false

        private fun timer(){
            timer = object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if(timerNumber>=Day.names.size){
                        timer?.cancel()
                        return
                    }

                    if (millisUntilFinished/1000>=60){
                        Day.timers[timerNumber] = "01:0"+millisUntilFinished/1000%10
                    }
                    else if (millisUntilFinished/1000<10){
                        Day.timers[timerNumber] = "00:0"+millisUntilFinished/1000
                    }
                    else{
                        Day.timers[timerNumber] = "00:"+millisUntilFinished/1000
                    }

                    playersList.adapter?.notifyItemChanged(timerNumber)
                    time = millisUntilFinished
                }

                override fun onFinish() {
                    if(timerNumber>=Day.names.size){
                        return
                    }

                    Day.timers[timerNumber] = "00:00"
                    playersList.adapter?.notifyItemChanged(timerNumber++)

                    if(timerNumber<Day.names.size){
                        if (isExtra){
                            time = 35000

                            if (timerExtraNumber < DayVote.namesToVote.size){
                                timerNumber = names.indexOf(DayVote.namesToVote[timerExtraNumber++].split('.')[1].removeRange(0, 1))
                            }
                            else{
                                timerNumber = 0
                                timerExtraNumber = 0
                                time = 65000
                                Day.readyToVote = true
                                Day.indicateStartVoting()
                                return
                            }
                        } else{
                            time = 65000
                        }

                        if (!Day.screenChanged){
                            timer()
                        }
                        else{
                            Day.changeButtonState()
                        }
                    }
                    else{
                        timerNumber = 0
                        timerExtraNumber = 0
                        time = 65000
                        Day.readyToVote = true
                        Day.indicateStartVoting()
                    }
                }
            }.start()
        }

        fun pauseTimer(){
            timer?.cancel()
        }

        fun resumeTimer(){
            timer()
        }

        fun stopTimer(timerToStop : Int){
            if (isWorking){
                if (timerNumber == timerToStop){
                    timer?.cancel()
                    time = 1000
                    timer()
                }
            }
        }

        fun extraTimer(){
            for (i : Int in 0 until DayVote.namesToVote.size){
                val extraNumber = names.indexOf(DayVote.namesToVote[i].split('.')[1].removeRange(0, 1))
                Day.timers[extraNumber] = "00:35"
                playersList.adapter?.notifyItemChanged(extraNumber)
            }

            isExtra = true
            timerNumber = names.indexOf(DayVote.namesToVote[timerExtraNumber++].split('.')[1].removeRange(0, 1))
            time = 35000
            timer()
        }

        fun restart(){
            //playersList
            names = arrayListOf()
            timerNumber = 0
            timerExtraNumber = 0
            timer = null
            time = 65000
            isWorking = false
            isExtra = false
        }
    }
}