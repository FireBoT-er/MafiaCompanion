package com.fbt.mafia

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.databinding.PlayerInfoBinding

class PlayerInfo : Fragment() {
    private var _binding: PlayerInfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PlayerInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var editName : EditText
    private lateinit var name : String
    private lateinit var editInfo : EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.editName)
        name = arguments?.getString("name")!!
        editName.setText(name)

        binding.editNameButton.setOnClickListener {
            editNameWorker()
        }

        editInfo = view.findViewById(R.id.editInfo)
        val db = AppDatabase.getDB(requireActivity().applicationContext)
        val player = db.playerDao().findByNickname(name)
        editInfo.setText(player.info)

        binding.editInfoButton.setOnClickListener {
            editInfoWorker()
        }

        if (db.historyPlayersDao().loadAllByPLayersIds(intArrayOf(player.id)).isNotEmpty()){
            binding.deletePlayer.isVisible = false
        }
        else{
            binding.deletePlayer.setOnClickListener {
                deleteWorker()
            }
        }

        binding.backPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_playerInfo_to_playersDB)
        }
    }

    private fun editNameWorker(){
        editName.isEnabled = !editName.isEnabled

        if (editName.isEnabled){
            editName.requestFocus()
            editName.setSelection(editName.length())
            binding.editNameButton.setIconResource(R.drawable.checked)
        }
        else{
            if (editName.length()>0){
                val db = AppDatabase.getDB(requireActivity().applicationContext)
                val player = db.playerDao().findByNickname(name)

                player.nickname = editName.text.toString()

                if (db.playerDao().findByNickname(player.nickname) != null && name != player.nickname){
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("Ошибка")
                        .setMessage("Игрок с таким именем уже существует")
                        .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                        .show()
                    editName.isEnabled = true
                }
                else{
                    db.playerDao().update(player)
                    name = player.nickname

                    binding.editNameButton.setIconResource(R.drawable.pen)
                }
            }
            else{
                AlertDialog.Builder(context, R.style.AlertDialogStyle)
                    .setTitle("Ошибка")
                    .setMessage("Имя не может быть пустым")
                    .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                    .show()
                editName.isEnabled = true
            }
        }
    }

    private fun editInfoWorker(){
        editInfo.isEnabled = !editInfo.isEnabled

        if (editInfo.isEnabled){
            editInfo.requestFocus()
            editInfo.setSelection(editInfo.length())
            binding.editInfoButton.setIconResource(R.drawable.checked)
        }
        else{
            val db = AppDatabase.getDB(requireActivity().applicationContext)
            val player = db.playerDao().findByNickname(name)

            player.info = editInfo.text.toString()
            db.playerDao().update(player)

            binding.editInfoButton.setIconResource(R.drawable.pen)
        }
    }

    private fun deleteWorker(){
        AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
            .setTitle("Вы уверены?")
            .setMessage("Отменить операцию будет невозможно")
            .setPositiveButton("Да") {
                dialog, _ -> dialog.dismiss()

                val db = AppDatabase.getDB(requireActivity().applicationContext)
                val player = db.playerDao().findByNickname(name)
                db.playerDao().delete(player)

                findNavController().navigate(R.id.action_playerInfo_to_playersDB)
            }
            .setNegativeButton("Нет"){
                dialog, _ -> dialog.cancel()
            }
            .show()
    }
}