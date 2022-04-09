package com.example.rdv;

import android.os.Parcel;
import android.os.Parcelable;

public class Moment implements Parcelable {
    long id;
    String category;
    String title;
    String num;
    String contact;
    String location;
    String date;
    String time;
    String reminder;
    String comments;
    //int photo;
    //String address;



    public Moment() {
    }

    public Moment(String category, String title, String contact, String num, String location, String date,
                  String time, String reminder, String comments) {
        this.category = category;
        this.title = title;
        this.contact = contact;
        this.num = num;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
        this.comments = comments;
    }

    public Moment(long id, String category, String title, String contact, String num, String location, String date,
                  String time, String reminder, String comments) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.contact = contact;
        this.num = num;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContact(){return contact;}

    public void setContact(String contact){this.contact= contact;}

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(category);
        dest.writeString(title);
        dest.writeString(contact);
        dest.writeString(num);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(reminder);
        dest.writeString(comments);
    }

    public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>() {
        @Override
        public Moment createFromParcel(Parcel parcel) {
            return new Moment(parcel);
        }

        @Override
        public Moment[] newArray(int size) {
            return new Moment[size];
        }
    };

    public Moment(Parcel parsel) {
        id = parsel.readLong();
        category = parsel.readString();
        title = parsel.readString();
        contact = parsel.readString();
        num = parsel.readString();
        location = parsel.readString();
        date = parsel.readString();
        time = parsel.readString();
        reminder = parsel.readString();
        comments = parsel.readString();
    }

}
