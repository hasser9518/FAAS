package com.ensightplus.faas.model;

import java.io.Serializable;

public class Booking implements Serializable {
    public String status;
    public String date;
    public String pickup;
    public String destination;
    public String time;
    public String ride_class;
    public String payment;
    public String fare;
    public String booking_code;
}
