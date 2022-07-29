package com.ensightplus.faas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ensightplus.faas.data.App;
import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;

public class ActivityLogin extends AppCompatActivity {

    private String TAG = ActivityLogin.class.getSimpleName();

    private EditText input_username, input_password;
    private Button btnSignUp;
    private View parent_view;
    private ProgressDialog dialog;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                Log.d(TAG, "MyFirebaseMessagingService TOKEN: " + fcmToken);
            }
        });

        parent_view = findViewById(android.R.id.content);

        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        btnSignUp = findViewById(R.id.btn_signup);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        dialog = ProgressDialog.show(ActivityLogin.this, "", "Loading...", true);
        loginUser(input_username.getText().toString().trim(), input_password.getText().toString().trim());

        Intent i = new Intent(ActivityLogin.this, ActivityMain.class);
        startActivity(i);
        finish();
    }

    public void loginUser(final String email, String password) {

        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);
        JSONObject jsonObject = new JSONObject(params);

        OkGo.<JSONObject>post(Constant.API_URL + Constant.METHOD_LOGIN).upJson(jsonObject).execute(new Callback<JSONObject>() {
            @Override
            public void onStart(Request<JSONObject, ? extends Request> request) {
                /*
                if (!CheckConnection.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
                 */
            }

            @Override
            public void onSuccess(Response<JSONObject> response) {
                try {
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        User user = gson.fromJson(response.body().toString(), User.class);

                        App.getInstance().setUser(user);
                        App.getInstance().createLoginSession(user.getUser(), "", user.getToken());

                        Log.w(TAG , "Email:" + user.getUser());
                        Log.w(TAG , "Token:" + user.getToken());
                        Log.w(TAG , "Expires:" + user.getExpires());

                        addFcmToken(email, fcmToken);
/*
                        Intent i = new Intent(ActivityLogin.this, ActivityMain.class);
                        startActivity(i);
                        finish();

 */
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e("loginerror", "cache sucess");
            }


            @Override
            public void onError(Response<JSONObject> response) {
                Log.e("loginerror", " error");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void uploadProgress(Progress progress) {
            }

            @Override
            public void downloadProgress(Progress progress) {
            }

            @Override
            public JSONObject convertResponse(okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (!jsonObject.getString("token").isEmpty()) {
                            return jsonObject;
                        } else {
                            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("catch", e.toString());
                }

                return null;
            }
        });
    }

    public void addFcmToken(String email, String fcmToken) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("fcmToken", fcmToken);
        params.put("deviceType", "android");
        JSONObject jsonObject = new JSONObject(params);

        OkGo.<JSONObject>post(Constant.API_URL + Constant.METHOD_ADD_FCM_TOKEN).upJson(jsonObject).execute(new Callback<JSONObject>() {
            @Override
            public void onStart(Request<JSONObject, ? extends Request> request) {
                /*
                if (!CheckConnection.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
                 */
            }

            @Override
            public void onSuccess(Response<JSONObject> response) {
                try {
                    if (response.body() != null) {
                        Log.w(TAG , "JSON:" + response.body().toString());
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e("loginerror", "cache sucess");
            }


            @Override
            public void onError(Response<JSONObject> response) {
                Log.e("loginerror", " error");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void uploadProgress(Progress progress) {
            }

            @Override
            public void downloadProgress(Progress progress) {
            }

            @Override
            public JSONObject convertResponse(okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        JSONObject jsonObject = new JSONObject(res);
                        if (!jsonObject.getString("token").isEmpty()) {
                            return jsonObject;
                        } else {
                            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("catch", e.toString());
                }

                return null;
            }
        });
    }

    private boolean validateName() {
        if (input_username.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(parent_view, getString(R.string.err_msg_name), Snackbar.LENGTH_SHORT).show();
            requestFocus(input_username);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String password = input_password.getText().toString().trim();

        if (password.isEmpty()) {
            Snackbar.make(parent_view, getString(R.string.err_msg_password), Snackbar.LENGTH_SHORT).show();
            requestFocus(input_password);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
