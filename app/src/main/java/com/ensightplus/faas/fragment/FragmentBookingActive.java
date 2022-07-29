package com.ensightplus.faas.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ensightplus.faas.R;
import com.ensightplus.faas.adapter.BookingListAdapter;
import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.model.Booking;

import java.util.List;

public class FragmentBookingActive extends Fragment {

    private View root_view;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_booking_active, container, false);
        initComponent();
        return root_view;
    }

    private void initComponent() {
        recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        List<Booking> bookingList = Constant.getBookingActive(getActivity());
        BookingListAdapter mAdapter = new BookingListAdapter(getActivity(), bookingList);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        /*
        mAdapter.setOnItemClickListener(new BookingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Booking obj, int position) {
                ActivityBookingActiveDetails.navigate(getActivity(), obj);
            }
        });

         */
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
