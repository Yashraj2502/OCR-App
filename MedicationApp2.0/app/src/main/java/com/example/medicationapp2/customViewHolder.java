package com.example.medicationapp2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class customViewHolder extends RecyclerView.ViewHolder {

    public TextView nameHolder;
    public CardView cardView;

    public customViewHolder(@NonNull View itemView) {
        super(itemView);

        nameHolder = itemView.findViewById(R.id.nameHolder);
        cardView = itemView.findViewById(R.id.mainContainer);
    }
}
