package com.ensightplus.faas.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ensightplus.faas.R;
import com.ensightplus.faas.adapter.PromoListAdapter;
import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.Promo;

public class FragmentDialogPromo extends DialogFragment {

    public CallbackResult callbackResult;
    private Promo promo = null;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_dialog_promo, container, false);
        initComponent();

        Tools.setSystemBarColorFragment(getActivity(), this, R.color.grey_soft);
        Tools.setSystemBarLightFragment(this);
        return root_view;
    }

    private void initComponent() {
        final ImageView img_clear = (ImageView) root_view.findViewById(R.id.img_clear);
        final EditText et_search = (EditText) root_view.findViewById(R.id.et_search);
        RecyclerView recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        TextView tv_remove = (TextView) root_view.findViewById(R.id.tv_remove);
        TextView tv_code = (TextView) root_view.findViewById(R.id.tv_code);
        final View lyt_current = (View) root_view.findViewById(R.id.lyt_current);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        final PromoListAdapter mAdapter = new PromoListAdapter(getActivity(), Constant.getPromoData(getActivity()));
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new PromoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Promo obj, int position) {
                sendDataResult(obj);
                dismissDialog();
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(et_search.getText().toString());
                if (!et_search.getText().toString().trim().equals("")) {
                    img_clear.setVisibility(View.VISIBLE);
                } else {
                    img_clear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
            }
        });

        ((ImageView) root_view.findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        if (promo != null) {
            tv_code.setText(promo.code);
        } else {
            lyt_current.setVisibility(View.GONE);
        }

        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePromo();
                dismissDialog();
            }
        });

        Tools.hideKeyboardFragment(this);
    }

    private void sendDataResult(Promo Promo) {
        if (callbackResult != null) {
            callbackResult.sendResult(request_code, Promo);
        }
    }

    private void removePromo() {
        if (callbackResult != null) {
            callbackResult.removePromo(request_code);
        }
    }

    private void dismissDialog() {
        Tools.hideKeyboardFragment(this);
        dismiss();
    }

    @Override
    public int getTheme() {
        return R.style.AppTheme_FullScreenDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setPromo(Promo promo) {
        this.promo = promo;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    public interface CallbackResult {
        void sendResult(int requestCode, Promo Promo);

        void removePromo(int requestCode);
    }

}