package br.alphap.acontacts.util;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContactList implements Parcelable, Serializable {

    private ArrayList<PersonalContact> list;

    public PersonalContactList() {
        list = new ArrayList<>();
    }

    public PersonalContactList(PersonalContact[] list) {
        this();
        this.list.addAll(Arrays.asList(list));
    }

    public PersonalContactList(ArrayList<PersonalContact> list) {
        this();
        if (this.list.isEmpty()) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }
    }

    public PersonalContactList putContact(int position, PersonalContact contact) {
        list.add(position, contact);
        return this;
    }

    public PersonalContactList putContact(PersonalContact contact) {
        list.add(contact);
        return this;
    }

    public PersonalContactList putContact(int position, String name, String phone, Bitmap image) {
        list.add(position, new PersonalContact(name, phone, image));
        return this;
    }

    public PersonalContactList putContact(String name, String phone, Bitmap image) {
        list.add(new PersonalContact(name, phone, image));
        return this;
    }

    public PersonalContactList putAllContacts(ArrayList<PersonalContact> list) {
        this.list.addAll(list);
        return this;
    }

    public PersonalContactList replaceContact(int pos, PersonalContact newContact) {
        this.list.set(pos, newContact);
        return this;
    }

    public PersonalContact getContact(int pos) {
        return this.list.get(pos);
    }

    public ArrayList<PersonalContact> getContacts() {
        return list;
    }

    public PersonalContactList removeContact(int pos) {
        this.list.remove(pos);
        return this;
    }

    public PersonalContactList removeContact(PersonalContact object) {
        this.list.remove(object);
        return this;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    protected PersonalContactList(Parcel in) {
        list = in.createTypedArrayList(PersonalContact.CREATOR);
    }

    public static final Creator<PersonalContactList> CREATOR = new Creator<PersonalContactList>() {
        @Override
        public PersonalContactList createFromParcel(Parcel in) {
            return new PersonalContactList(in);
        }

        @Override
        public PersonalContactList[] newArray(int size) {
            return new PersonalContactList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
    }
}
