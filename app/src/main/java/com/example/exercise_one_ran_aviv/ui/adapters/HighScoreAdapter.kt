package com.example.exercise_one_ran_aviv.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exercise_one_ran_aviv.R
import com.example.exercise_one_ran_aviv.data.HighScore

class HighScoreAdapter(
    private val scores: List<HighScore>,
    private val onItemClick: ((HighScore) -> Unit)? = null
) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.item_score_text)

        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(scores[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = scores[position]
        holder.text.text = "${score.name}: ${score.score} pts | ${score.distance}m"
    }

    override fun getItemCount(): Int = scores.size
}


