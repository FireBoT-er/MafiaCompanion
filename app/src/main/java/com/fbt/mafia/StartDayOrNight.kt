package com.fbt.mafia

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.databinding.StartDayOrNightBinding

class StartDayOrNight : Fragment() {
    private var _binding: StartDayOrNightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = StartDayOrNightBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var dayNightLabel: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dayNightLabel = view.findViewById(R.id.dayNightLabel)

        var killed = ""

        if (isDay){
            this.view?.setBackgroundColor(Color.rgb(176, 0, 32))
            dayNightLabel.text = "День"

            if (arguments?.getString("killed")!=null){
                killed = arguments?.getString("killed")!!
            }
        }
        else{
            this.view?.setBackgroundColor(Color.BLACK)
            dayNightLabel.text = "Ночь"
        }

        binding.nextTime.setOnClickListener {
            if (isDay){
                if (Day.getMafiaCount() == 0 || Day.getCitizensCount() == 0 || Day.getMafiaCount() == Day.getCitizensCount()){
                    findNavController().navigate(R.id.action_dayNightStart_to_endGame, bundleOf("didMafiaWin" to (Day.getMafiaCount() != 0)))
                    return@setOnClickListener
                }

                if (killed != ""){
                    findNavController().navigate(R.id.action_dayNightStart_to_lastWords, bundleOf("deadNames" to arrayListOf(killed)))
                }
                else{
                    findNavController().navigate(R.id.action_dayNightStart_to_day)
                }
            }
            else{
                findNavController().navigate(R.id.action_dayNightStart_to_night)
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
            builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_dayNightStart_to_AddPlayers, bundleOf("names" to namesToMenu)) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }
    }

    companion object{
        var isDay : Boolean = false

        fun restart(){
            isDay = false
        }
    }
}