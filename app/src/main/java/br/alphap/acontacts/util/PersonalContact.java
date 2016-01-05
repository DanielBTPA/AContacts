package br.alphap.acontacts.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContact implements Parcelable {

    private int contactId;
    private int contactType = 0;
    private String name;
    private String phone;
    private int sizeArrayImageData;
    private byte[] imageData;

    public PersonalContact() {
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getContactId() {
        return contactId;
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

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setImageData(byte[] imageData) {
        this.sizeArrayImageData = imageData != null ? imageData.length : 0;
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageAsBitmap(Bitmap src) {
        if (src != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            src.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            this.sizeArrayImageData = byteArrayOutputStream.size();
            this.imageData = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getImageAsBitmap() {
        if (imageData != null) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        return null;
    }

    public boolean haveName() {
        return name != null && !name.equals("");
    }

    public boolean havePhone() {
        return phone != null && !phone.equals("");
    }

    public boolean haveImage() {
        return imageData != null;
    }

    private PersonalContact(Parcel in) {
        contactType = in.readInt();
        name = in.readString();
        phone = in.readString();

        int size = in.readInt();

        if (size > 0) {
            in.readByteArray(imageData = new byte[size]);
        }
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
        dest.writeInt(sizeArrayImageData);

        Log.i("Size", "" + sizeArrayImageData);
        if (sizeArrayImageData > 0) {
            dest.writeByteArray(imageData);
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof PersonalContact) {
            PersonalContact c = (PersonalContact) o;

            return this.name.equals(c.name) && this.phone.equals(c.phone) &&
                    Arrays.equals(this.imageData, c.imageData);
        }

        return false;
    }
}
