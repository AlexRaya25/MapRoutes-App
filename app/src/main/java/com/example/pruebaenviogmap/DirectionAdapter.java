package com.example.pruebaenviogmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> {
    private List<Direction> adresses;
    private Context context;






    public DirectionAdapter(Context context, List<Direction> adresses) {
        this.adresses = adresses;
        this.context = context;;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView textViewDirection;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            textViewDirection = itemView.findViewById(R.id.text_view_direction);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Direction direction = adresses.get(position);
        holder.checkBox.setChecked(direction.isSelected());
        holder.textViewDirection.setText(direction.getName());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direction.setSelected(holder.checkBox.isChecked());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !direction.isSelected();
                direction.setSelected(isChecked);
                holder.checkBox.setChecked(isChecked);

                    if (direction.isSelected()) {
                        //holder.itemView.setBackgroundResource(R.drawable.border_black_checked);
                    } else {
                        holder.itemView.setBackgroundResource(R.drawable.border_black);
                    }

                int selectedCount = getSelectedAddresses().size();

                    if(MainActivity.map != 1)
                    {
                        if (selectedCount > 10) {
                            ((MainActivity) context).handleLimitReached();
                            holder.checkBox.setChecked(false);
                            adresses.get(position).setSelected(false);
                            holder.itemView.setBackgroundResource(R.drawable.border_black);
                        }
                }else{
                        if (selectedCount > 1) {
                            ((MainActivity) context).handleWazeLimitReached();
                            holder.checkBox.setChecked(false);
                            adresses.get(position).setSelected(false);
                            holder.itemView.setBackgroundResource(R.drawable.border_black);
                        }
                    }
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = holder.checkBox.isChecked();
                direction.setSelected(isChecked);
                int selectedCount = getSelectedAddresses().size();
                if(MainActivity.map != 1)
                {
                if (selectedCount > 10) {
                    ((MainActivity) context).handleLimitReached();
                    holder.checkBox.setChecked(false);
                    adresses.get(position).setSelected(false);
                    holder.itemView.setBackgroundResource(R.drawable.border_black);
                }
                }else{
                    if (selectedCount > 1) {
                        ((MainActivity) context).handleWazeLimitReached();
                        holder.checkBox.setChecked(false);
                        adresses.get(position).setSelected(false);
                        holder.itemView.setBackgroundResource(R.drawable.border_black);
                    }
                }
            }
        });
    }
    public void resetBackgrounds(boolean isSelected) {
        for (Direction direction : adresses) {
            direction.setSelected(isSelected);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return adresses.size();
    }

    public void updateAddresses(List<Direction> newDirections) {
        adresses.clear();
        adresses.addAll(newDirections);
        notifyDataSetChanged();
    }

    public List<Direction> getSelectedAddresses() {
        List<Direction> selectedAddresses = new ArrayList<>();
        for (Direction direction : adresses) {
            if (direction.isSelected()) {
                selectedAddresses.add(direction);
            }
        }
        return selectedAddresses;
    }

}
