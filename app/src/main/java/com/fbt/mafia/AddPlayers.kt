package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.database.AppDatabase
import com.fbt.mafia.databinding.AddPlayersBinding


class AddPlayers : Fragment(), OnStartDragListener {

    private var _binding: AddPlayersBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addPlayersList: RecyclerView
    private var mItemTouchHelper: ItemTouchHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AddPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var names : ArrayList<String> = arrayListOf()
    private var seats : ArrayList<String> = arrayListOf()
    private var namesDB : ArrayList<String> = arrayListOf()
    private var namesDBInitCount : Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainMenu.restartGame()

        addPlayersList = view.findViewById(R.id.addPlayersList)
        addPlayersList.layoutManager = LinearLayoutManager(this.context)

        val adapter = RecyclerAddPlayersAdapter(names, this)
        addPlayersList.adapter = adapter

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(addPlayersList)

        if (arguments?.getStringArrayList("names")!=null){
            val names : ArrayList<String> = arguments?.getStringArrayList("names")!!

            for (i : Int in 0 until names.size){
                this.names.add(names[i])
                addPlayersList.adapter?.notifyItemInserted(i)
            }

            pseudoConstructor(binding, this.names)
            plusVisibilityChanger()
        }

        binding.buttonPlus.setOnClickListener {
            plusOnClick()
        }

        binding.backNewGame.setOnClickListener {
            getBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            getBack()
        }

        val db = AppDatabase.getDB(requireActivity().applicationContext)
        for (player in db.playerDao().getAll()){
            namesDB.add(player.nickname)
        }

        namesDBInitCount = namesDB.size
    }

    private fun getBack(){
        if (names.size>0){
            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)

            builder.setTitle("Вы уверены?")
            builder.setMessage("Если вернуться, все добавленные игроки будут исключены из игры")
            builder.setPositiveButton("Да") { _, _ -> findNavController().navigate(R.id.action_AddPlayers_to_MainMenu) }
            builder.setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }

            builder.show()
        }
        else{
            findNavController().navigate(R.id.action_AddPlayers_to_MainMenu)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun plusOnClick(){
        if (binding.buttonPlus.text.equals("Далее")){
            for (i : Int in 0 until names.size){
                seats.add((i+1).toString())
            }

            findNavController().navigate(R.id.action_AddPlayers_to_GetRole, bundleOf("names" to names, "seats" to seats))
            return
        }

        if (namesDBInitCount>9){
            val namesUnique = namesDB.toList().minus(names.toSet()).toTypedArray()
            val chosen = MutableList(namesUnique.size) { false }

            AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
                .setMultiChoiceItems(namesUnique, chosen.toBooleanArray()) {
                    _, which, isChecked -> chosen[which] = isChecked
                }
                .setTitle("Выберите игроков:")
                .setPositiveButton("Готово") {
                    dialog, _ -> dialog.dismiss()
                    val chosenNames = ArrayList(namesUnique.zip(chosen).toMap().filter { it.value }.keys)

                    val playersMax = if (chosenNames.size<=10-names.size){
                        chosenNames.size
                    }
                    else{
                        AlertDialog.Builder(context, R.style.AlertDialogStyle)
                            .setTitle("Внимание")
                            .setMessage("Вы выбрали слишком много игроков. Игроки после 10-го исключены")
                            .setPositiveButton("Ок") { anotherDialog, _ -> anotherDialog.dismiss() }
                            .show()

                        10-names.size
                    }

                    for (i : Int in 0 until playersMax){
                        names.add(chosenNames[i])
                        addPlayersList.adapter?.notifyItemInserted(names.size)
                    }

                    pseudoConstructor(binding, names)
                    plusVisibilityChanger()
                }
                .setNegativeButton("Отмена"){
                    dialog, _ -> dialog.cancel()
                }
                .show()
        }
        else{
            AlertDialog.Builder(context, R.style.AlertDialogStyle)
                .setTitle("Мало игроков")
                .setMessage("Добавьте больше игроков через главное меню (не менее 10)")
                .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder != null) {
            mItemTouchHelper?.startDrag(viewHolder)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var binding: AddPlayersBinding? = null
        private lateinit var names : ArrayList<String>
        fun pseudoConstructor(_binding: AddPlayersBinding, _names: ArrayList<String>){
            binding = _binding
            names = _names
        }

        fun plusVisibilityChanger(){
            if (binding != null){
                if (names.size==10){
                    binding?.buttonPlus?.text = "Далее"
                }
                else{
                    binding?.buttonPlus?.text = "Добавить игроков"
                }
            }
        }

        fun restart(){
            binding = null
            names = arrayListOf()
        }
    }
}