package com.stylezone.demo.models;

public class Booking {
    private int bookingId;
    private String bookingTime;
    private String bookingDate;
    private String bookingName;
    private int bookingPhone;
    private String bookingComment;
    private int staffId;

    public Booking() {
    }

    public Booking(int bookingId, String bookingTime, String bookingDate, String bookingName, int bookingPhone, String bookingComment, int staffId) {
        this.bookingId = bookingId;
        this.bookingTime = bookingTime;
        this.bookingDate = bookingDate;
        this.bookingName = bookingName;
        this.bookingPhone = bookingPhone;
        this.bookingComment = bookingComment;
        this.staffId = staffId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    public int getBookingPhone() {
        return bookingPhone;
    }

    public void setBookingPhone(int bookingPhone) {
        this.bookingPhone = bookingPhone;
    }

    public String getBookingComment() {
        return bookingComment;
    }

    public void setBookingComment(String bookingComment) {
        this.bookingComment = bookingComment;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", bookingTime='" + bookingTime + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", bookingName='" + bookingName + '\'' +
                ", bookingPhone=" + bookingPhone +
                ", bookingComment='" + bookingComment + '\'' +
                ", staffId=" + staffId +
                '}';
    }
}