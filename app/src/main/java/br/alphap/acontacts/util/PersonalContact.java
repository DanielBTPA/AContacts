package br.alphap.acontacts.util;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContact implements Parcelable, Serializable {

    private int contactType = 0;
    private String name;
    private String phone;
    private Bitmap image;
    private String phoneType;


    public void setContactTypePosition(int contactType) {
        this.contactType = contactType;
    }

    public int getContactTypePosition() {
        return contactType;
    }

    public void setContactType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getContactType() {

        return phoneType;
    }

    public PersonalContact() {
    }

    public PersonalContact(String name, String phone, Bitmap image) {
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public PersonalContact(String name, String phone, Bitmap image, int contactType) {
        this.contactType = contactType;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    protected PersonalContact(Parcel in) {
        contactType = in.readInt();
        name = in.readString();
        phone = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        phoneType = in.readString();
    }

    public static final Creator<PersonalContact> CREATOR = new Creator<PersonalContact>() {
        @Override
        public PersonalContact createFromParcel(Parcel in) {
            return new PersonalContact(in);
        }

        @Override
        public PersonalContact[] newArray(int size) {
            return new PersonalContact[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Bitmap getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contactType);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeParcelable(image, flags);
        dest.writeString(phoneType);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersonalContact) {
            PersonalContact contact = (PersonalContact) o;
            return this.name.equals(contact.name) &&
                    this.phone.equals(contact.phone) &&
                    this.contactType == contact.contactType;
        }
        return false;
    }
}
