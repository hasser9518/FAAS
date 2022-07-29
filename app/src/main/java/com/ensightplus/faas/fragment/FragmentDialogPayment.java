package com.ensightplus.faas.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ensightplus.faas.ActivityPayment;
import com.ensightplus.faas.R;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.Payment;

public class FragmentDialogPayment extends DialogFragment {

    public CallbackResult callbackResult;
    private Payment payment = null;
    private AppCompatRadioButton radio_cash, radio_visa, radio_master;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_dialog_payment, container, false);
        initComponent();

        Tools.setSystemBarColorFragment(getActivity(), this, R.color.grey_soft);
        Tools.setSystemBarLightFragment(this);
        return root_view;
    }

    private void initComponent() {
        radio_cash = (AppCompatRadioButton) root_view.findViewById(R.id.radio_cash);
        radio_visa = (AppCompatRadioButton) root_view.findViewById(R.id.radio_visa);
        radio_master = (AppCompatRadioButton) root_view.findViewById(R.id.radio_master);

        refreshRadioButton();
        if (payment != null) {
            if (payment.type.equals("Cash")) {
                radio_cash.setChecked(true);
            } else if (payment.type.equals("Visa Card")) {
                radio_visa.setChecked(true);
            } else if (payment.type.equals("Master Card")) {
                radio_master.setChecked(true);
            }
        } else {
            payment = new Payment();
            payment.type = "Cash";
            radio_cash.setChecked(true);
        }

        ((View) root_view.findViewById(R.id.lyt_cash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshRadioButton();
                radio_cash.setChecked(true);
                payment.type = "Cash";
                sendDataResult(payment);
                dismissDialog();
            }
        });
        ((View) root_view.findViewById(R.id.lyt_visa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshRadioButton();
                radio_visa.setChecked(true);
                payment.type = "Visa Card";
                sendDataResult(payment);
                dismissDialog();
            }
        });
        ((View) root_view.findViewById(R.id.lyt_master)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshRadioButton();
                radio_master.setChecked(true);
                payment.type = "Master Card";
                sendDataResult(payment);
                dismissDialog();
            }
        });

        ((ImageView) root_view.findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        ((Button) root_view.findViewById(R.id.bt_payment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityPayment.class));
            }
        });
    }

    private void refreshRadioButton() {
        radio_cash.setChecked(false);
        radio_visa.setChecked(false);
        radio_master.setChecked(false);
    }

    private void sendDataResult(Payment payment) {
        if (callbackResult != null) {
            callbackResult.sendResult(request_code, payment);
        }
    }

    private void dismissDialog() {
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

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    public interface CallbackResult {
        void sendResult(int requestCode, Payment payment);
    }

}