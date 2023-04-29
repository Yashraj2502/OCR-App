package com.example.medicationapp2;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class medicineAdapter extends RecyclerView.Adapter<customViewHolder> {

    private Context context;
    private List<medicines> list;
    private SingleMedicineActivity singleMedicineActivity;

    public medicineAdapter(Context context, List<medicines> list, SingleMedicineActivity singleMedicineActivity) {
        this.context = context;
        this.list = list;
        this.singleMedicineActivity = singleMedicineActivity;
    }

    @NonNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new customViewHolder(LayoutInflater.from(context).inflate(R.layout.single_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull customViewHolder holder, int position) {
        holder.nameHolder.setText(list.get(position).getmName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleMedicineActivity.onItemClicked(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<medicines> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }
}
