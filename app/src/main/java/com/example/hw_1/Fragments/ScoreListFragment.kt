package com.example.hw_1.Fragments

import Models.ScoreList
import Utilites.DataManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.hw_1.R
import Interfaces.OnScoreSelected
import Utilites.SharedPreferencesManager
import android.content.Context
import android.content.SharedPreferences
import com.example.hw_1.ScoreActivity
import com.google.android.gms.maps.model.LatLng

class ScoreListFragment : Fragment(){
    private lateinit var tableLayout : TableLayout
    private var listener: OnScoreSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_score, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
        val highScoreList = DataManager.getHighScoreList()
        fillScoreTable(tableLayout, highScoreList)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreSelected) {
            listener = context
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    private fun fillScoreTable(tableLayout: TableLayout, scoreList: MutableList<ScoreList>) {
        scoreList.forEachIndexed { index, score ->
            val tableRow = TableRow(requireContext())

            val scoreTextView = TextView(requireContext()).apply {
                text = score.getScore().toString()
                textSize = resources.getDimension(R.dimen.table_text_size)
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
                setBackgroundColor(resources.getColor(android.R.color.white, null))
            }

            val locationTextView = TextView(requireContext()).apply {
                text = score.getLocation().address
                textSize = resources.getDimension(R.dimen.table_text_size)
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
                setBackgroundColor(resources.getColor(android.R.color.white, null))
            }
            tableRow.addView(scoreTextView)
            tableRow.addView(locationTextView)
            tableLayout.addView(tableRow)
            tableRow.setOnClickListener {
                val selectedScore = scoreList[index]
                val latLng = LatLng(selectedScore.getLocation().latitude,selectedScore.getLocation().longitude)
                listener?.onScoreSelected(latLng,selectedScore.getDate())
            }
        }
    }
}
