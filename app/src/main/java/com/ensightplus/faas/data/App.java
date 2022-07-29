package com.ensightplus.faas.data;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ensightplus.faas.ActivityLogin;
import com.ensightplus.faas.model.User;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.ssl.TrustStrategy;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class App extends Application {

    static App app;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String userEmail = "user_email";
    public static final String KEY_ACCESSTOKEN = "Authorization";
    public static final String KEY_FCMTOKEN = "fcm_token";
    public static final String KEY_HEADER = "header";
    public static final String KEY_Phone = "phone";
    public static final String AVATAR = "avatar";
    private static final String TAG = "check_out";
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_FNAME = "fname";
    public static final String KEY_LNAME = "lname";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String UNIT = "$";
    public static String USER = "user";

    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public SharedPreferences getPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        return pref;
    }

    public void initOkGo() {
        getPref();

        Log.w(TAG, "**** Allow untrusted SSL connection ****");
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] cArrr = new X509Certificate[0];
                return cArrr;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
        }};

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d(TAG, "Trust Host :" + hostname);
                    return true;
                }
            };
            builder.hostnameVerifier(hostnameVerifier);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Content-Type", "application/x-www-form-urlencoded");

        if (isLoggedIn()) {
            httpHeaders.put(KEY_ACCESSTOKEN, "Bearer " + getUser().getToken());
        }

        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build())
                .addCommonHeaders(httpHeaders)
                .setCacheMode(CacheMode.DEFAULT)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);
    }

    public User getUser() {
        return new Gson().fromJson(pref.getString(USER, null), User.class);
    }

    public void setUser(User user) {
        Gson gson = new Gson();
        String s = gson.toJson(user);
        editor.putString(USER, s);
        editor.commit();
    }

    public void createLoginSession(String email, String fcmtoken, String accesstoken) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FCMTOKEN, fcmtoken);
        editor.putString(KEY_ACCESSTOKEN, accesstoken);

        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public SharedPreferences setrPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        return pref;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //  Fabric.with(this, new Crashlytics());
        //FirebaseApp.initializeApp(getApplicationContext());
        // FacebookSdk.sdkInitialize(getApplicationContext());
        app = this;

        initOkGo();
    }

    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        app.setrPref();
        return app;
    }

    public SharedPreferences.Editor getDb() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        return editor;
    }

    public void clearAll() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
