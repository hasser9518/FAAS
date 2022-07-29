package com.ensightplus.faas.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ensightplus.faas.R;
import com.ensightplus.faas.model.Vehicle;

import java.util.List;

public class AssetsListAdapter extends RecyclerView.Adapter<AssetsListAdapter.ViewHolder> {

    private Context ctx;
    private List<Vehicle> items;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Vehicle obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView subtitle;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.icon);
            title = v.findViewById(R.id.title);
            subtitle = v.findViewById(R.id.subtitle);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }

    }

    public AssetsListAdapter(Context ctx, List<Vehicle> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public AssetsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Vehicle n = items.get(position);
        int resource = R.drawable.ic_marker_move;
        switch (n.status) {
            case "offline":
                resource = R.drawable.ic_marker_offline;
                break;
            case "stop":
                resource = R.drawable.ic_marker_stop;
                break;
            case "idle":
                resource = R.drawable.ic_marker_idle;
                break;
        }

        holder.icon.setBackgroundResource(resource);
        //Picasso.with(ctx).load(resource).into(holder.icon);
        holder.title.setText(n.vin);
        holder.subtitle.setText(n.driver);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, n, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}