package com.example.gymcompanion.muscleGroupsFilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcompanion.R;
import com.example.gymcompanion.components.MuscleGroupFilter;

import java.util.ArrayList;
import java.util.List;

public class MuscleGroupFiltersAdapter extends RecyclerView.Adapter<MuscleGroupFiltersAdapter.ViewHolder> {

    private static final String TAG = "ExerciseFiltersAdapter";

    private List<MuscleGroupFilter> muscleGroupFiltersList;
    private List<String> muscleGroupsSelected;
    private OnItemClickListener mListener;
    private Context mContext;

    public interface OnItemClickListener{
        void onSelectClick(List<String> muscleGroupsSelected);
    }

    public void setOnItemClickListener(MuscleGroupFiltersAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public MuscleGroupFiltersAdapter(List<MuscleGroupFilter> muscleGroupFiltersList, Context context){
        this.muscleGroupFiltersList = muscleGroupFiltersList;
        this.mContext = context;
        this.muscleGroupsSelected = new ArrayList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_exercise_filter, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MuscleGroupFilter filter = muscleGroupFiltersList.get(position);

        holder.name.setText(filter.getMuscleGroup());

        if(filter.isSelected()){
            holder.name.setBackground(mContext.getDrawable(R.drawable.rounded_bg_purple));
            holder.name.setTextColor(mContext.getResources().getColor(R.color.white));

        } else{
            holder.name.setBackground(mContext.getDrawable(R.color.white));
            holder.name.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        }
    }

    @Override
    public int getItemCount() {
        return muscleGroupFiltersList.size();
    }

    public void setMuscleGroupFiltersList(List<MuscleGroupFilter> filtersList){
        this.muscleGroupFiltersList = filtersList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView filterCard;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.muscle_group_filter_name);
            filterCard = itemView.findViewById(R.id.filter_card);

            filterCard.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MuscleGroupFilter filter = muscleGroupFiltersList.get(position);
                        String muscleGroup = filter.getMuscleGroup();

                        if(filter.isSelected())
                            muscleGroupsSelected.remove(muscleGroup);

                        else
                            muscleGroupsSelected.add(muscleGroup);

                        filter.setSelected(!filter.isSelected());
                        listener.onSelectClick(muscleGroupsSelected);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
