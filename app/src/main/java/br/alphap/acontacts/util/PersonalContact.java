package br.alphap.acontacts.util;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContact implements Parcelable {

    private int contactId;
    private int contactType = 0;
    private String name;
    private String phone;
    private Bitmap imageData;

    public PersonalContact() {
    }


    public PersonalContact(String name, String phone, Bitmap imageData) {
        this.name = name;
        this.phone = phone;
        this.imageData = imageData;
    }

    public PersonalContact(String name, String phone, Bitmap imageData, int contactType) {
        this.contactType = contactType;
        this.name = name;
        this.phone = phone;
        this.imageData = imageData;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public int getContactType() {
        return contactType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageData(Bitmap imageData) {
        this.imageData = imageData;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Bitmap getImageData() {
        return imageData;
    }

    public PersonalContact(Parcel in) {
        contactType = in.readInt();
        name = in.readString();
        phone = in.readString();
        imageData = in.readParcelable(Bitmap.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contactType);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeParcelable(imageData, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersonalContact) {
            PersonalContact contact = (PersonalContact) o;
            return this.name.equals(contact.name) &&
                    this.phone.equals(contact.phone) &&
                    this.contactType == contact.contactType &&
                    (imageData != null ?  imageData.sameAs(contact.imageData) : true);
        }
        return false;
    }
}
