package br.alphap.acontacts.util;

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
    private byte[] picImageData;

    public PersonalContact() {
    }


    public PersonalContact(String name, String phone, byte[] picImageData) {
        this.name = name;
        this.phone = phone;
        this.picImageData = picImageData;
    }

    public PersonalContact(String name, String phone, byte[] picImageData, int contactType) {
        this.contactType = contactType;
        this.name = name;
        this.phone = phone;
        this.picImageData = picImageData;
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

    public void setImageData(byte[] imageData) {
        this.picImageData = imageData;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public byte[] getImageData() {
        return picImageData;
    }

    protected PersonalContact(Parcel in) {
        contactType = in.readInt();
        name = in.readString();
        phone = in.readString();
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
