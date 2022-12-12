package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.database.HistoryActions
import com.fbt.mafia.databinding.NightBinding

class Night : Fragment() {
    private var _binding: NightBinding? = null
    private val binding get() = _binding!!

    private lateinit var roleImageNight: ImageView
    private lateinit var roleNight: TextView
    private lateinit var nightRoleFunction: Button
    private lateinit var timerNight: TextView
    private lateinit var nextNight: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roleImageNight = view.findViewById(R.id.roleImageNight)

        roleNight = view.findViewById(R.id.roleNight)
        nightRoleFunction = view.findViewById(R.id.nightRoleFunction)
        timerNight = view.findViewById(R.id.timerNight)

        nextNight = view.findViewById(R.id.nextNight)
        nextNight.isVisible = false

        nextNight.setOnClickListener {
            changeRole()
        }

        if (arguments?.getBoolean("nullNight")!=null){
            nightRoleFunction.isVisible = false
            timerNight.isVisible = false
            nextNight.isVisible = true
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Если вернуться, игра будет начата заново")
            if (arguments?.getStringArrayList("names")!=null){
                builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_night_to_AddPlayers, bundleOf("names" to arguments?.getStringArrayList("names")!!)) }
            }
            else{
                val namesToMenu : ArrayList<String> = arrayListOf()
                for (item in namesFull){
                    namesToMenu.add(item.split('.')[2])
                }

                builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_night_to_AddPlayers, bundleOf("names" to namesToMenu)) }
            }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }

        changeRole()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = NightBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var roleNumber = 0
    private fun changeRole(){
        when(roleNumber){
            0 -> {
                this.view?.setBackgroundColor(Color.BLACK)
                roleImageNight.setImageResource(R.drawable.hat)
                roleNight.text = "Ход мафии"
                nightRoleFunction.text = "Убийство"
                timerNight.text = ""

                nightRoleFunction.setOnClickListener {
                    killList()
                }
            }
            1 -> {
                concreteShoes()

                this.view?.setBackgroundColor(Color.BLACK)
                roleImageNight.setImageResource(R.drawable.godfather)
                roleNight.text = "Ход дона"
                nightRoleFunction.text = "Поиск"

                nightRoleFunction.setOnClickListener {
                    getPlayersListInDialog()
                }

                timer()
            }
            2 -> {
                this.view?.setBackgroundColor(Color.rgb(176, 0, 32))
                roleImageNight.setImageResource(R.drawable.police_badge)
                roleNight.text = "Ход шерифа"
                nightRoleFunction.text = "Поиск"

                timer.cancel()
                timer()
            }
            3 -> {
                StartDayOrNight.isDay = true
                if (!nightRoleFunction.isVisible){
                    findNavController().navigate(R.id.action_night_to_day, bundleOf("nullDay" to true, "names" to arguments?.getStringArrayList("names")!!, "seats" to arguments?.getStringArrayList("seats")!!, "roles" to arguments?.getStringArrayList("roles")!!))
                }
                else{
                    if (killedIndex != 0){
                        findNavController().navigate(R.id.action_night_to_dayNightStart, bundleOf("killed" to targets[killedIndex]))
                    }
                    else{
                        findNavController().navigate(R.id.action_night_to_dayNightStart)
                    }
                }
            }
        }

        roleNumber++
    }

    private var killedIndex = 0
    private lateinit var targets : ArrayList<String>

    private fun killList(){
        targets = arrayListOf("Промах")
        val namesOrdered = Day.seats.map { it.toInt() }.zip(Day.names).toMap().toSortedMap()
        for((key, value) in namesOrdered){
            targets.add("$key. $value")
        }

        AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
            .setSingleChoiceItems(targets.toArray(arrayOfNulls<String>(targets.size)), 0, null)
            .setTitle("Выберите игрока:")
            .setPositiveButton("Выбрать") {
                dialog, _ -> dialog.dismiss()
                killedIndex = (dialog as AlertDialog).listView.checkedItemPosition
                nextNight.isVisible = true
            }
            .setNegativeButton("Отмена"){
                dialog, _ -> dialog.cancel()
            }
            .show()
    }

    private fun concreteShoes(){
        if (killedIndex != 0){
            Day.kill(targets[killedIndex].split('.')[1].removeRange(0, 1), 3)
        }
        else if (arguments?.getBoolean("nullNight")==null){
            val db = AppDatabase.getDB(requireActivity().applicationContext)
            Day.historyActionsList.add(HistoryActions(0, -1, Day.round, 4, db.playerDao().getFirstID()))
        }
    }

    private fun getPlayersListInDialog(){
        val viewInflated: View = LayoutInflater.from(context).inflate(R.layout.show_players_list, view as ViewGroup?, false)

        val playersListNight = viewInflated.findViewById<View>(R.id.playersListNight) as RecyclerView
        playersListNight.layoutManager = LinearLayoutManager(this.context)
        playersListNight.adapter = RecyclerEveryPlayersAdapter(namesFull)

        AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("Игроки:")
            .setView(viewInflated)
            .setPositiveButton("Готово"){ dialog, _ -> dialog.dismiss() }
            .show()
    }

    private lateinit var timer: CountDownTimer

    private fun timer(){
        timer = object : CountDownTimer(35000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished/1000<10){
                    timerNight.text = "00:0"+millisUntilFinished/1000
                }
                else{
                    timerNight.text = "00:"+millisUntilFinished/1000
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerNight.text = "00:00"
                nextNight.isVisible = true
            }
        }.start()
    }

    companion object {
        var namesFull : ArrayList<String> = arrayListOf()

        fun restart(){
            namesFull = arrayListOf()
        }
    }
}