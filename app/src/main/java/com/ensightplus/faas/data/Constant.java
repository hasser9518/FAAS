package com.ensightplus.faas.data;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.maps.model.LatLng;
import com.ensightplus.faas.R;
import com.ensightplus.faas.model.Booking;
import com.ensightplus.faas.model.CarAround;
import com.ensightplus.faas.model.Notification;
import com.ensightplus.faas.model.Promo;
import com.ensightplus.faas.model.RideClass;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Constant {

    private static Random rnd = new Random();

    public static String API_URL = "https://my.fleetasaservice.com/api/v1/";
    public static String METHOD_LOGIN = "Auth/login";
    public static String METHOD_ADD_FCM_TOKEN = "Notification/AddFCMToken";
    public static String METHOD_REFRESH_TOKEN = "Auth/refreshToken";
    public static String METHOD_VEHICLES = "Tracking/map/allvehicles";
    public static String METHOD_GROUPS = "Tracking/map/groups";
    public static String METHOD_VEHICLE_GROUPS = "Tracking/map/vehiclegroups";
    public static String METHOD_TRACKING_HISTORY = "Tracking/map/trackinghistory";

    private static int getRandomIndex(Random r, int min, int max) {
        return r.nextInt(max - min) + min;
    }

    private static int getRandomIndexUnique(Random r, int min, int max, int reserved) {
        int value = r.nextInt(max - min) + min;
        if (reserved == value) getRandomIndexUnique(r, min, max, reserved);
        return value;
    }

    public static List<RideClass> getRideClassData(Context ctx) {
        List<RideClass> items = new ArrayList<>();
        TypedArray images = ctx.getResources().obtainTypedArray(R.array.ride_image);
        String[] names = ctx.getResources().getStringArray(R.array.ride_name);
        String[] prices = ctx.getResources().getStringArray(R.array.ride_price);
        String[] paxs = ctx.getResources().getStringArray(R.array.ride_pax);
        String[] durations = ctx.getResources().getStringArray(R.array.ride_duration);

        for (int i = 0; i < names.length; i++) {
            RideClass item = new RideClass();
            item.class_name = names[i];
            item.image = images.getResourceId(i, -1);
            item.price = prices[i];
            item.pax = paxs[i] + " pax";
            item.duration = durations[i] + " min";
            items.add(item);
        }
        return items;
    }

    public static List<Promo> getPromoData(Context ctx) {
        List<Promo> items = new ArrayList<>();
        TypedArray icons = ctx.getResources().obtainTypedArray(R.array.promo_icon);
        String[] titles = ctx.getResources().getStringArray(R.array.promo_title);
        String[] dates = ctx.getResources().getStringArray(R.array.promo_date);
        String[] codes = ctx.getResources().getStringArray(R.array.promo_code);

        for (int i = 0; i < titles.length; i++) {
            Promo item = new Promo();
            item.icon = icons.getResourceId(i, -1);
            item.title = titles[i];
            item.date = dates[i];
            item.code = codes[i];
            items.add(item);
        }
        return items;
    }

    public static List<CarAround> getCarAroundData(Context ctx) {
        List<CarAround> items = new ArrayList<>();
        String[] locations = ctx.getResources().getStringArray(R.array.car_location);
        for (int i = 0; i < locations.length; i++) {
            CarAround item = new CarAround();
            String[] loc = locations[i].split("#");
            item.latLng = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
            item.rotation = getRandomIndex(rnd, 0, 360);
            items.add(item);
        }
        return items;
    }

    private static List<Booking> getBooking(Context ctx) {
        List<Booking> items = new ArrayList<>();
        String[] status = ctx.getResources().getStringArray(R.array.booking_status);
        String[] date = ctx.getResources().getStringArray(R.array.booking_date);
        String[] pickup = ctx.getResources().getStringArray(R.array.booking_pickup);
        String[] destination = ctx.getResources().getStringArray(R.array.booking_destination);
        String[] time = ctx.getResources().getStringArray(R.array.booking_time);
        String[] ride_class = ctx.getResources().getStringArray(R.array.booking_ride_class);
        String[] payment = ctx.getResources().getStringArray(R.array.booking_payment);

        for (int i = 0; i < status.length; i++) {
            Booking item = new Booking();
            item.status = status[i];
            item.date = date[i];
            item.pickup = pickup[i];
            item.destination = destination[i];
            item.time = time[i];
            item.ride_class = ride_class[i];
            item.payment = payment[i];
            item.booking_code = getBookingCode();
            if (ride_class.equals("Economy")) {
                item.fare = "$6.75";
            } else if (ride_class.equals("Large")) {
                item.fare = "$10.4";
            } else if (ride_class.equals("Premium")) {
                item.fare = "$13.99";
            } else {
                item.fare = "$9.25";
            }
            items.add(item);
        }
        return items;
    }

    public static List<Notification> getNotificationData(Context ctx) {
        List<Notification> items = new ArrayList<>();
        TypedArray icon = ctx.getResources().obtainTypedArray(R.array.notification_icon);
        String[] title = ctx.getResources().getStringArray(R.array.notification_title);
        String[] subtitle = ctx.getResources().getStringArray(R.array.notification_subtitle);
        for (int i = 0; i < title.length; i++) {
            Notification item = new Notification();
            item.icon = icon.getResourceId(i, -1);
            item.title = title[i];
            item.subtitle = subtitle[i];
            items.add(item);
        }
        return items;
    }

    public static List<Booking> getBookingActive(Context ctx) {
        return getBooking(ctx).subList(0, 1);
    }

    public static List<Booking> getBookingHistory(Context ctx) {
        String[] status = ctx.getResources().getStringArray(R.array.booking_status);
        return getBooking(ctx).subList(1, status.length);
    }

    public static String getBookingCode() {
        char[] numbs = "1234567890".toCharArray();
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 2; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        for (int i = 0; i < 4; i++) {
            char c = numbs[random.nextInt(numbs.length)];
            sb.append(c);
        }
        for (int i = 0; i < 2; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getOrderID() {
        char[] numbs = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 7; i++) {
            char c = numbs[random.nextInt(numbs.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}
