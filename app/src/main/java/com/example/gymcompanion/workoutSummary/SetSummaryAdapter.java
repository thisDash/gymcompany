package com.example.gymcompanion.workoutSummary;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseLog;
import com.example.gymcompanion.components.SetLog;

import org.w3c.dom.Text;

public class SetSummaryAdapter extends RecyclerView.Adapter<SetSummaryAdapter.ViewHolder>{

    private static final String TAG = "SetAdapter";

    private int numberSets;
    private ExerciseLog exerciseLog;
    private Context mContext;

    public SetSummaryAdapter(int numberSets, ExerciseLog exerciseLog, Context context){
        this.numberSets = numberSets;
        this.exerciseLog = exerciseLog;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SetSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_set_summary, parent, false);
        return new SetSummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetSummaryAdapter.ViewHolder holder, int position) {
        SetLog set = exerciseLog.getSetsList().get(position);

        holder.setNumberText.setText(String.valueOf(position+1));
        holder.weightUsed.setText(String.valueOf(set.getWeightUsed()));
        holder.numberReps.setText(String.valueOf(set.getNumberReps()));
        holder.finalRpe.setText(String.valueOf(set.getSetRpe()));
    }

    @Override
    public int getItemCount() {
        return numberSets;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView setNumberText;
        TextView finalRpe;
        TextView weightUsed;
        TextView numberReps;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNumberText = itemView.findViewById(R.id.set_summary_set_number);
            weightUsed = itemView.findViewById(R.id.set_summary_weight);
            numberReps = itemView.findViewById(R.id.set_summary_reps);
            finalRpe = itemView.findViewById(R.id.set_summary_rpe);
        }
    }

}
