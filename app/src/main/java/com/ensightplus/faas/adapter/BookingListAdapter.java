package com.ensightplus.faas.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ensightplus.faas.R;
import com.ensightplus.faas.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> {

    private Context ctx;
    private List<Booking> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Booking obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView status;
        public TextView date;
        public TextView pickup;
        public TextView destination;
        public TextView time;
        public TextView rider_class;
        public TextView payment;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            status = (TextView) v.findViewById(R.id.status);
            date = (TextView) v.findViewById(R.id.date);
            pickup = (TextView) v.findViewById(R.id.pickup);
            destination = (TextView) v.findViewById(R.id.destination);
            time = (TextView) v.findViewById(R.id.time);
            rider_class = (TextView) v.findViewById(R.id.tv_distance);
            payment = (TextView) v.findViewById(R.id.tv_first_ignition);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }

    }

    public BookingListAdapter(Context ctx, List<Booking> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public BookingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Booking b = items.get(position);

        holder.date.setText(b.date);
        holder.pickup.setText(b.pickup);
        holder.destination.setText(b.destination);
        holder.time.setText(b.time);
        holder.rider_class.setText(b.ride_class);
        holder.payment.setText(b.payment);

        holder.status.setText(b.status);
        if (b.status.equals("ON GOING")) {
            holder.status.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.shape_rectangle_ongoing));
        } else if (b.status.equals("FINISHED")) {
            holder.status.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.shape_rectangle_finished));
        } else if (b.status.equals("CANCELED")) {
            holder.status.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.shape_rectangle_canceled));
        }

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, b, position);
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