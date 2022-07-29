package com.ensightplus.faas.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ensightplus.faas.R;
import com.ensightplus.faas.model.Promo;

import java.util.ArrayList;
import java.util.List;

public class PromoListAdapter extends RecyclerView.Adapter<PromoListAdapter.ViewHolder> implements Filterable {

    private Context ctx;
    private List<Promo> original_items = new ArrayList<>();
    private List<Promo> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Promo obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView date;
        public TextView code;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.icon);
            title = (TextView) v.findViewById(R.id.tv_title);
            date = (TextView) v.findViewById(R.id.tv_date);
            code = (TextView) v.findViewById(R.id.tv_code);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }

    }

    public Filter getFilter() {
        return mFilter;
    }

    public PromoListAdapter(Context ctx, List<Promo> items) {
        this.ctx = ctx;
        original_items = items;
        filtered_items = items;
    }

    @Override
    public PromoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Promo p = filtered_items.get(position);
        Picasso.with(ctx).load(p.icon).into(holder.icon);
        holder.title.setText(p.title);
        holder.date.setText(p.date);
        holder.code.setText(p.code);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Promo> list = original_items;
            final List<Promo> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                Promo p = list.get(i);
                String text = p.title + p.code;
                if (text.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Promo>) results.values;
            notifyDataSetChanged();
        }

    }
}