package com.example.gymcompanion.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;

import java.util.List;

public class MuscleGroupsAdapter extends RecyclerView.Adapter<MuscleGroupsAdapter.ViewHolder> {

    private List<String> muscleGroupsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public MuscleGroupsAdapter(List<String> muscleGroupsList){
        this.muscleGroupsList = muscleGroupsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_muscle_group_selected, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String muscleGroup = muscleGroupsList.get(position);
        holder.muscleGroupName.setText(muscleGroup);
    }

    @Override
    public int getItemCount() {
        return muscleGroupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView muscleGroupName;
        ImageView deleteMuscleGroupButton;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            muscleGroupName = itemView.findViewById(R.id.muscle_group_name);
            deleteMuscleGroupButton = itemView.findViewById(R.id.delete_muscle_group_button);

            deleteMuscleGroupButton.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onDeleteClick(position);
                }
            });
        }
    }
}
