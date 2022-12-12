package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.database.HistoryActions
import com.fbt.mafia.databinding.DayBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class Day : Fragment() {
    private var _binding: DayBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = view.findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener(navListener)

        binding.navigationCrutch.setOnClickListener {
            if (getMafiaCount() == 0 || getCitizensCount() == 0 || (getMafiaCount() == 1 && getCitizensCount() == 1)){
                findNavController().navigate(R.id.action_day_to_endGame, bundleOf("didMafiaWin" to (getMafiaCount() != 0)))
                return@setOnClickListener
            }

            DayPlayers.isWorking = false

            var firstPlayerNowDead = false
            for (name in DayVote.namesToVote){
                if (name.split('.')[0] == firstPlayerNow){
                    firstPlayerNowDead = true
                    break
                }
            }

            if (!firstPlayerNowDead){
                names.add(names.removeAt(0))
                seats.add(seats.removeAt(0))
                roles.add(roles.removeAt(0))
            }

            round++

            StartDayOrNight.isDay = false
            if (emptyVoting){
                findNavController().navigate(R.id.action_day_to_dayNightStart)
            }
            else{
                findNavController().navigate(R.id.action_day_to_lastWords, bundleOf("deadNames" to DayVote.namesToVote))
            }
        }

        binding.indicateStartVotingCrutch.setOnClickListener {
            bottomNav.menu.performIdentifierAction(R.id.vote, 0)
            bottomNav.selectedItemId = R.id.vote
        }

        pseudoConstructor(binding, this.requireContext(), context?.resources?.displayMetrics!!.density)
        binding.pause.setOnClickListener {
            changeButtonState()
        }

        if (arguments?.getStringArrayList("names")!=null){
            names.addAll(arguments?.getStringArrayList("names")!!)
        }

        if (arguments?.getStringArrayList("seats")!=null){
            seats.addAll(arguments?.getStringArrayList("seats")!!)
        }

        if (arguments?.getStringArrayList("roles")!=null){
            roles.addAll(arguments?.getStringArrayList("roles")!!)
        }

        nullDay = arguments?.getBoolean("nullDay")!=null

        firstPlayerNow = seats[0]

        if (Night.namesFull.isEmpty()){
            for (i : Int in 0 until names.size){
                val name = names[i]
                val seat = seats[i]
                val role = roles[i]
                var fullInfo = "$seat."

                fullInfo += if (role=="Мирный житель"){
                    "Ж."
                } else{
                    role[0].toString()+'.'
                }

                fullInfo+=name
                Night.namesFull.add(fullInfo)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val namesToMenu : ArrayList<String> = arrayListOf()
            for (item in Night.namesFull){
                namesToMenu.add(item.split('.')[2])
            }

            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Если вернуться, игра будет начата заново")
            builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_day_to_AddPlayers, bundleOf("names" to namesToMenu)) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }

        DayVote.names = arrayListOf()
        DayVote.namesToVote = arrayListOf()

        timers = arrayListOf()
        for (i : Int in 0 until names.size){
            timers.add("01:05")
        }

        DayVote.namesToVote = arrayListOf()

        val dayPlayers = DayPlayers()
        dayPlayers.arguments = bundleOf("names" to names, "seats" to seats, "roles" to roles)
        parentFragmentManager.beginTransaction().replace(R.id.fragmentСontainer, dayPlayers).commit()
    }

    private val navListener =  NavigationBarView.OnItemSelectedListener { item: MenuItem ->
        lateinit var selectedFragment: Fragment
        when (item.itemId) {
            R.id.players -> {
                selectedFragment = DayPlayers()
                screenChanged = false
            }
            R.id.vote -> {
                selectedFragment = DayVote()
                screenChanged = true
            }
            R.id.dead -> {
                selectedFragment = DayDead()
                screenChanged = true
            }
        }

        selectedFragment.arguments = bundleOf("names" to names, "seats" to seats, "roles" to roles)
        parentFragmentManager.beginTransaction().replace(R.id.fragmentСontainer, selectedFragment).commit()

        true
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var binding: DayBinding? = null
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private var scale: Float = 0.0f
        fun pseudoConstructor(_binding: DayBinding, _context : Context, _scale : Float){
            binding = _binding
            context = _context
            scale = _scale
        }

        var timers : ArrayList<String> = arrayListOf()
        private var isPaused: Boolean = false
        var screenChanged: Boolean = false
        var readyToVote: Boolean = false

        fun changeButtonState(){
            if (binding != null) {
                if (readyToVote){
                    startVoting()
                    return
                }

                isPaused = !isPaused
                when (isPaused) {
                    true -> {
                        binding?.pause?.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_media_play)
                        DayPlayers.pauseTimer()
                    }
                    else -> {
                        binding?.pause?.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause)
                        DayPlayers.resumeTimer()
                    }
                }
            }
        }

        fun indicateStartVoting(){
            if (binding != null) {
                binding?.pause?.icon = ContextCompat.getDrawable(context, R.drawable.vote)
                binding?.pause?.iconSize = ((35 * scale + 0.5f).toInt())

                binding?.indicateStartVotingCrutch?.performClick()
            }
        }

        var names : ArrayList<String> = arrayListOf()
        var seats : ArrayList<String> = arrayListOf()
        var roles : ArrayList<String> = arrayListOf()
        var dead : ArrayList<String> = arrayListOf()

        var emptyVoting: Boolean = false
        var nullDay: Boolean = false

        private fun startVoting(){
            if (DayVote.namesToVote.size==0 || (DayVote.namesToVote.size==1 && nullDay)){
                AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
                    .setTitle("Вы уверены?")
                    .setMessage("Голосование не произойдёт")
                    .setPositiveButton("Да") {
                        dialog, _ -> dialog.dismiss()

                        readyToVote = false
                        isPaused = false
                        screenChanged = false

                        emptyVoting = true
                        binding?.navigationCrutch?.performClick()
                    }
                    .setNegativeButton("Нет"){
                        dialog, _ -> dialog.cancel()
                    }
                    .show()

                return
            }
            else if (DayVote.namesToVote.size==1){
                readyToVote = false
                isPaused = false
                screenChanged = false

                val db = AppDatabase.getDB(context.applicationContext)
                historyActionsList.add(HistoryActions(0, -1, round, 1, db.playerDao().findByNickname(DayVote.namesToVote[0].split('.')[1].removeRange(0, 1)).id))
                kill(DayVote.namesToVote[0].split('.')[1].removeRange(0, 1), 2)

                emptyVoting = false
                binding?.navigationCrutch?.performClick()
                return
            }

            if (!DayPlayers.isExtra){
                AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
                    .setTitle("Игроки совершили выбор?")
                    .setPositiveButton("Да") {
                        dialog, _ -> dialog.dismiss()
                        endVoting()
                    }
                    .setNegativeButton("Нет"){
                        dialog, _ -> dialog.dismiss()

                        DayPlayers.extraTimer()

                        readyToVote = false
                        isPaused = false
                        binding?.pause?.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause)
                    }
                    .setNeutralButton("Отмена"){
                        dialog, _ -> dialog.dismiss()
                    }
                    .show()
            }
            else{
                endVoting()
            }
        }

        private fun endVoting(){
            val voted = MutableList(DayVote.namesToVote.size) { false }

            AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
                .setMultiChoiceItems(DayVote.namesToVote.toTypedArray(), voted.toBooleanArray()) {
                    _, which, isChecked -> voted[which] = isChecked
                }
                .setTitle("Введите результат голосования:")
                .setPositiveButton("Готово") {
                    dialog, _ -> dialog.dismiss()
                    if (voted.contains(true)){
                        readyToVote = false
                        isPaused = false
                        screenChanged = false

                        DayPlayers.isExtra = false

                        val db = AppDatabase.getDB(context.applicationContext)
                        for (name in DayVote.namesToVote){
                            historyActionsList.add(HistoryActions(0, -1, round, 1, db.playerDao().findByNickname(name.split('.')[1].removeRange(0, 1)).id))
                        }

                        DayVote.namesToVote = ArrayList(DayVote.namesToVote.zip(voted).toMap().filter { it.value }.keys)

                        for (i : Int in 0 until DayVote.namesToVote.size){
                            kill(DayVote.namesToVote[i].split('.')[1].removeRange(0, 1), 2)
                        }

                        emptyVoting = false
                        binding?.navigationCrutch?.performClick()
                    }
                }
                .setNegativeButton("Отмена"){
                    dialog, _ -> dialog.cancel()
                }
                .show()
        }

        fun kill(doomed : String, action : Int){
            val deadNumber = names.indexOf(doomed)
            val name = names.removeAt(deadNumber)
            val seat = seats.removeAt(deadNumber)
            val role = roles.removeAt(deadNumber)
            var deadInfo = "$seat."

            deadInfo += if (role=="Мирный житель"){
                "Ж."
            } else{
                role[0].toString()+'.'
            }

            val db = AppDatabase.getDB(context.applicationContext)
            historyActionsList.add(HistoryActions(0, -1, round, action, db.playerDao().findByNickname(name).id))

            deadInfo+=name
            dead.add(deadInfo)
        }

        fun getMafiaCount() : Int {
            return roles.filter { r -> r == "Мафия" }.size + roles.filter { r -> r == "Дон" }.size
        }

        fun getCitizensCount() : Int {
            return roles.filter { r -> r == "Мирный житель" }.size + roles.filter { r -> r == "Шериф" }.size
        }

        private var firstPlayerNow = ""
        var round : Int = 0
        var historyActionsList : ArrayList<HistoryActions> = arrayListOf()

        fun restart(){
            binding = null
            //context
            scale = 0.0f
            timers = arrayListOf()
            isPaused = false
            screenChanged = false
            readyToVote = false
            names = arrayListOf()
            seats = arrayListOf()
            roles = arrayListOf()
            dead = arrayListOf()
            emptyVoting = false
            nullDay = false
            firstPlayerNow = ""
            round = 0
            historyActionsList = arrayListOf()
        }
    }
}