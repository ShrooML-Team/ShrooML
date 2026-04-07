package com.shrooml.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.shrooml.R;

public class NavbarAdapter extends RecyclerView.Adapter<NavbarAdapter.NavViewHolder> {

    private final List<String> items;
    private final int activeId; // L'ID réel (0 à 3)
    private final OnItemClickListener listener;

    // On définit une constante pour simuler l'infini
    public static final int LOOP_COUNT = 10000;

    public NavbarAdapter(List<String> items, int activeId, OnItemClickListener listener) {
        this.items = items;
        this.activeId = activeId;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return LOOP_COUNT; // On simule beaucoup d'éléments
    }

    @Override
    public void onBindViewHolder(@NonNull NavViewHolder holder, int position) {
        int realPos = position % items.size();
        holder.title.setText(items.get(realPos));

        // CONDITION : Si c'est l'ID de l'activité actuelle, on affiche le fond sombre
        if (realPos == activeId) {
            holder.indicator.setVisibility(View.VISIBLE);
            holder.indicator.setAlpha(1.0f); // Toujours visible pour l'activité en cours
        } else {
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(realPos));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav, parent, false);
        return new NavViewHolder(view);
    }

    static class NavViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View indicator;
        NavViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nav_title);
            indicator = itemView.findViewById(R.id.nav_indicator);
        }
    }
}