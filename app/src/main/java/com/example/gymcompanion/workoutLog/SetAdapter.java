package com.example.gymcompanion.workoutLog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.ExerciseLog;
import com.example.gymcompanion.components.SetLog;

import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder>{

    private static final String TAG = "SetAdapter";

    private int numberSets;
    private ExerciseLog exerciseLog;
    private Context mContext;
    private List<SetLog> prevWeekSetLogs;

    public SetAdapter(int numberSets, ExerciseLog exerciseLog, Context context){
        this.numberSets = numberSets;
        this.exerciseLog = exerciseLog;
        this.mContext = context;
        this.prevWeekSetLogs = null;
    }

    public SetAdapter(int numberSets, ExerciseLog exerciseLog, Context context, List<SetLog> prevWeekSetLogs){
        this.numberSets = numberSets;
        this.exerciseLog = exerciseLog;
        this.mContext = context;
        this.prevWeekSetLogs = prevWeekSetLogs;
    }

    @NonNull
    @Override
    public SetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_set, parent, false);
        return new SetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAdapter.ViewHolder holder, int position) {
        holder.setNumberText.setText(String.valueOf(position+1));
        SetLog set = exerciseLog.getSetsList().get(position);

        if(set.getWeightUsed() == 0){
            if(prevWeekSetLogs == null)
                holder.weightUsed.setHint("  ");

            else {
                SetLog prevWeekSetLog = prevWeekSetLogs.get(position);
                holder.weightUsed.setHint(String.valueOf(prevWeekSetLog.getWeightUsed()));
            }

        } else
            holder.weightUsed.setText(String.valueOf(set.getWeightUsed()));

        if(set.getNumberReps() == 0){
            if(prevWeekSetLogs == null)
                holder.numberReps.setHint("  ");

            else {
                SetLog prevWeekSetLog = prevWeekSetLogs.get(position);
                holder.numberReps.setHint(String.valueOf(prevWeekSetLog.getNumberReps()));
            }

        } else
            holder.numberReps.setText(String.valueOf(set.getNumberReps()));

        if(position == numberSets - 1)
            holder.setDivider.setVisibility(View.GONE);

        holder.weightUsed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                    exerciseLog.setWeightForSet(position, 0);

                else if(!charSequence.toString().endsWith("."))
                    exerciseLog.setWeightForSet(position, Float.parseFloat(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.numberReps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty())
                    exerciseLog.setRepsForSet(position, Integer.parseInt(charSequence.toString()));

                else
                    exerciseLog.setRepsForSet(position, 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.finalRpe.setText(String.valueOf(set.getSetRpe()));

        holder.addRPE.setOnClickListener(view -> {
            int prevRPE = set.getSetRpe();

            if(prevRPE < 10) {
                set.setSetRpe(prevRPE + 1);
                holder.finalRpe.setText(String.valueOf(prevRPE + 1));
            }
        });

        holder.subtractRPE.setOnClickListener(view -> {
            int prevRPE = set.getSetRpe();

            if(prevRPE > 1) {
                set.setSetRpe(prevRPE - 1);
                holder.finalRpe.setText(String.valueOf(prevRPE - 1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return numberSets;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView setNumberText;
        TextView finalRpe;
        EditText weightUsed;
        EditText numberReps;
        ImageView addRPE;
        ImageView subtractRPE;
        View setDivider;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNumberText = itemView.findViewById(R.id.set_number);
            weightUsed = itemView.findViewById(R.id.set_weight);
            numberReps = itemView.findViewById(R.id.set_reps);
            finalRpe = itemView.findViewById(R.id.set_rpe);
            addRPE = itemView.findViewById(R.id.set_add_rpe_button);
            subtractRPE = itemView.findViewById(R.id.set_subtract_rpe_button);
            setDivider = itemView.findViewById(R.id.set_divider);
        }
    }

}
