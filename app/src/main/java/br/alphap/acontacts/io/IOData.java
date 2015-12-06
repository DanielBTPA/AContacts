package br.alphap.acontacts.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by danielbt on 26/11/15.
 */
public class IOData {

    public static final String PATH_DEFAULT_APP = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "AContacts";
    public static String PATH_DEFAULT_SDCARD = Environment.getExternalStorageDirectory()
            + File.separator;

    public static File writeData(String pathFile, Object record) {
        String fileName = PATH_DEFAULT_SDCARD + pathFile;
        File file = new File(fileName);
        try {
            createFilePath(pathFile);

            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(record);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            Log.e("MyApp", "IO Exception: " + e);
        }
        return file;
    }

    public static boolean deleteIfExist(String path) {
        File file = new File(PATH_DEFAULT_SDCARD + path);

        boolean isDeleted = false;

        if (file.exists()) {
            isDeleted = file.delete();
        }

        return isDeleted;
    }

    public static Object readData(String pathName) throws IOException, ClassNotFoundException {
        Object object = null;
        String fileName = PATH_DEFAULT_SDCARD + "/" + pathName;

        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
        object = inputStream.readObject();
        inputStream.close();

        return object;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExistFile(String filePath) {
        return new File(PATH_DEFAULT_SDCARD + filePath).exists();
    }

    public static File createFilePath(String pathName) {
        File file = new File(PATH_DEFAULT_SDCARD + pathName);
        if (!file.exists()) {
            try {
                if (!file.getParent().equals("")) {
                    new File(file.getParent()).mkdirs();
                }

                if (!file.getName().equals("")) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static byte[] encodeBitmap(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (b != null) {
            b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap decodeBitmap(byte[] data) {
        Bitmap newImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        return newImage;
    }

}
