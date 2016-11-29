package com.org.iii.shine20;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

import static android.content.Context.TELEPHONY_SERVICE;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager tmgr;
    private AccountManager amgr;
    private ContentResolver contentResolver;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView)findViewById(R.id.img);

        contentResolver = getContentResolver();

        // 取得權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    123);
        }else{
            init();
        }
    }

    private void init(){
        tmgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Log.v("brad", "機馬" +tmgr.getDeviceId());
        Log.v("brad", "ｓｉｍ" +tmgr.getSubscriberId());
        tmgr.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        // 取得帳號
        amgr = (AccountManager)getSystemService(ACCOUNT_SERVICE);
        Account[] as = amgr.getAccounts();
        for (Account a: as){
            Log.v("brad", a.name + ":" +a.type );
        }

        //getContact();
        getPhoto();


    }

    //聯絡人
    private void getContact(){
        Log.v("brad", "OK");

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        Log.v("brad", "count: " + cursor.getCount());

        while ( cursor.moveToNext()){
            String name = cursor.getString(0);
            String tel = cursor.getString(1);
            Log.v("brad", name + ":" + tel);
        }

    }

    //取得照片

    private void getPhoto(){
        Cursor c = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        //Log.v("brad", "photo: " + c.getCount());

        c.moveToLast();
        String data = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
        Log.v("brad", "photo: " + data);

        Bitmap photo = BitmapFactory.decodeFile(data);
        img.setImageBitmap(photo);

    }


    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 來電拿起話筒 開始電話錄音
                    break;
                case TelephonyManager.CALL_STATE_RINGING: // 響鈴中  顯示對方來電
                    Log.v("shine", incomingNumber);
                    break;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }
}