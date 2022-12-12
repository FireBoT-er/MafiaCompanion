package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.databinding.GetRoleBinding

class GetRole : Fragment() {
    private var _binding: GetRoleBinding? = null

    private val binding get() = _binding!!

    private lateinit var playerName: TextView
    private lateinit var role: TextView
    private lateinit var roleImage: ImageView
    private lateinit var nextRole: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GetRoleBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var names : ArrayList<String> = arrayListOf()
    private var seats : ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        names = arguments?.getStringArrayList("names")!!
        seats = arguments?.getStringArrayList("seats")!!

        playerName = view.findViewById(R.id.playerName)
        role = view.findViewById(R.id.role)
        roleImage = view.findViewById(R.id.roleImage)
        nextRole = view.findViewById(R.id.nextRole)

        nextRole.setOnClickListener {
            if (playerNumber>9){
                findNavController().navigate(R.id.action_getRole_to_night, bundleOf("nullNight" to true, "names" to names, "seats" to seats, "roles" to roles))
            }
            else{
                showRole()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Если вернуться, раздача будет начата заново")
            builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_getRole_to_AddPlayers, bundleOf("names" to names)) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        roles.shuffle()
        showRole()
    }

    private var playerNumber : Int = 0
    private var roles : ArrayList<String> = arrayListOf("Мафия", "Мафия", "Дон", "Шериф",
        "Мирный житель", "Мирный житель", "Мирный житель", "Мирный житель", "Мирный житель", "Мирный житель")

    @SuppressLint("SetTextI18n")
    private fun showRole(){
        playerName.text = names[playerNumber]
        val name = roles[playerNumber++]

        when (name) {
            "Мафия" -> { this.view?.setBackgroundColor(Color.BLACK); roleImage.setImageResource(R.drawable.hat) }
            "Дон" -> { this.view?.setBackgroundColor(Color.BLACK); roleImage.setImageResource(R.drawable.godfather) }
            "Мирный житель" -> { this.view?.setBackgroundColor(Color.rgb(176, 0, 32)); roleImage.setImageResource(R.drawable.humans) }
            "Шериф" -> { this.view?.setBackgroundColor(Color.rgb(176, 0, 32)); roleImage.setImageResource(R.drawable.police_badge) }
        }

        role.text = name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}