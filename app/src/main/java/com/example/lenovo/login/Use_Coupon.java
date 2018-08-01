package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Use_Coupon extends AppCompatActivity {

    String today;
    String userRoll;
    String mealValue="";
    private Button approve;
    private ImageView qrcode;
    Calendar c = Calendar.getInstance();
    private TextView roll,time,tdDAY,date,meal,msg;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_coupon);
        setVar();
        setRoll();

        Bundle extras = getIntent().getExtras();
        if(extras != null) mealValue = extras.getString("value");
        meal.setText(mealValue);

        Date d = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        today = dateFormat.format(d);
        date.setText(today);

        Thread myThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();

        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userRoll).child(today).child(mealValue).exists())
                {
                    String qrdata=userRoll+":"+today+":"+mealValue;
                    MultiFormatWriter multiFormatWriter=new MultiFormatWriter();

                    try{
                        BitMatrix bitMatrix=multiFormatWriter.encode(qrdata, BarcodeFormat.QR_CODE,256,256);
                        BarcodeEncoder barcodeEncoder= new BarcodeEncoder();
                        Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                        qrcode.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    qrcode.setVisibility(View.INVISIBLE);
                    msg.setText("Enjoy Your Meal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



    }

    private void setVar(){
        roll= findViewById(R.id.tvUser_Roll);
        time= findViewById(R.id.tvTime);
        date= findViewById(R.id.tvDATE);
        meal= findViewById(R.id.tvMEAL);
        msg=findViewById(R.id.tvMsg);
        qrcode=findViewById(R.id.iv_qrcode);
    }

    private void setRoll(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        userRoll = user.getDisplayName();
        roll.setText(userRoll);
    }

   /* public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }*/

   //running timer
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date dt = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String curTime = dateFormat.format(dt);
                    time.setText(curTime);
                }catch (Exception ignored) {}
            }
        });
    }
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) try {
                doWork();
                Thread.sleep(1000); // Pause of 1 Second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
            }
        }
    }

}
