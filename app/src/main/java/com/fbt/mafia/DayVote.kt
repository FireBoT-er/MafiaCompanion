package com.fbt.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbt.mafia.databinding.DayVoteBinding


class DayVote : Fragment() {
    private var _binding: DayVoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var playersVoteList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DayVoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playersVoteList = view.findViewById(R.id.playersVoteList)
        playersVoteList.layoutManager = LinearLayoutManager(this.context)

        if (names.isEmpty() && namesToVote.isEmpty()){
            val namesOrdered = arguments?.getStringArrayList("seats")!!.map { it.toInt() }.zip(arguments?.getStringArrayList("names")!!).toMap().toSortedMap()
            for((key, value) in namesOrdered){
                names.add("$key. $value")
            }
        }
        else{
            pseudoConstructor(binding)
            toVoteVisibilityChanger()
        }

        val adapter = RecyclerVotePlayersAdapter(namesToVote)
        playersVoteList.adapter = adapter

        binding.buttonToVote.setOnClickListener {
            addOnClick()
        }
    }

    private fun addOnClick(){
        AlertDialog.Builder(this.context, R.style.AlertDialogStyle)
            .setSingleChoiceItems(names.toArray(arrayOfNulls<String>(names.size)), 0, null)
            .setTitle("Выберите игрока:")
            .setPositiveButton("Выбрать") {
                dialog, _ -> dialog.dismiss()
                namesToVote.add(names.removeAt((dialog as AlertDialog).listView.checkedItemPosition))
                playersVoteList.adapter?.notifyItemInserted(namesToVote.size)

                pseudoConstructor(binding)
                toVoteVisibilityChanger()
            }
            .setNegativeButton("Отмена"){
                dialog, _ -> dialog.cancel()
            }
            .show()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var binding: DayVoteBinding? = null
        fun pseudoConstructor(_binding: DayVoteBinding){
            binding = _binding
        }

        var names : ArrayList<String> = arrayListOf()
        var namesToVote : ArrayList<String> = arrayListOf()

        fun toVoteVisibilityChanger(){
            if (binding != null){
                binding?.buttonToVote?.isVisible = names.isNotEmpty()
            }
        }

        fun restart(){
            binding = null
            names = arrayListOf()
            namesToVote = arrayListOf()
        }
    }
}