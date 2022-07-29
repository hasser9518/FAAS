package com.ensightplus.faas;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ensightplus.faas.data.App;
import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.User;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = ActivitySplash.class.getSimpleName();
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent getIntent = getIntent();
        eventId = getIntent.getStringExtra("eventid");
        Log.d(TAG, "MyFirebaseMessagingService EVENT1: " + eventId);

        User user = App.getInstance().getUser();
        if (user != null && user.getRefreshToken() != null) {
            refreshToken(user.getRefreshToken());
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (App.getInstance().isLoggedIn()) {
                    Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("eventid", eventId);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                } else {
                    Intent mainIntent = new Intent(ActivitySplash.this, ActivityLogin.class);
                    ActivitySplash.this.startActivity(mainIntent);
                    finish();
                }
            }
        };

        new Timer().schedule(task, 1000);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    public void refreshToken(String refreshToken) {
        HttpUrl url = HttpUrl.parse(Constant.API_URL + Constant.METHOD_REFRESH_TOKEN).newBuilder()
                .addQueryParameter("refreshToken", refreshToken)
                .build();

        HttpParams params = new HttpParams();
        params.put("refreshToken", refreshToken);

        OkGo.<JSONObject>post(url.toString()).execute(new Callback<JSONObject>() {
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

                        Log.w(TAG, "Email:" + user.getUser());
                        Log.w(TAG, "Token:" + user.getToken());
                        Log.w(TAG, "Expires:" + user.getExpires());
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e(TAG, "cache sucess");
            }


            @Override
            public void onError(Response<JSONObject> response) {
                Log.e(TAG, " error");
            }

            @Override
            public void onFinish() {
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
                    Log.e(TAG, e.toString());
                }

                return null;
            }
        });
    }
}
