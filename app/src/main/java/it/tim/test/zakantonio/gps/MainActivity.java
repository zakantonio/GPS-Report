package it.tim.test.zakantonio.gps;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    TextView txv1, txv2, txv3, txv4, txv5, txv6, txv7;
    ImageView imgStatusGPS;
    Button btnStartGPS;

    private Context context;

    LocationManager locationManager;
    private BroadcastReceiver br;
    private SpeedService s;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        txv1 = (TextView) findViewById(R.id.txv_speed_ms);
        txv2 = (TextView) findViewById(R.id.txv_speed_kmh);
        txv3 = (TextView) findViewById(R.id.txv_latitude);
        txv4 = (TextView) findViewById(R.id.txv_longitude);
        txv5 = (TextView) findViewById(R.id.txv_altitude);
        txv6 = (TextView) findViewById(R.id.txv_altitude);
        txv7 = (TextView) findViewById(R.id.txv_bearing);

        imgStatusGPS = (ImageView) findViewById(R.id.img_status_gps);
        btnStartGPS = (Button) findViewById(R.id.btn_start_gps);

        btnStartGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGPS();

            }
        });

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == "it.test.speed") {
                    speed = s.getSpeed();
                    txv1.setText(txv1.getText() + " " + speed);
                    txv2.setText(txv2.getText() + " " + (speed * 3.6));
                    txv3.setText(txv3.getText() + " " + s.getLatitude());
                    txv4.setText(txv4.getText() + " " + s.getLongitude());
                    txv5.setText(txv5.getText() + " " + s.getAltitude());
                    txv6.setText(txv6.getText() + " " + s.getAccuracy());
                    txv7.setText(txv7.getText() + " " + s.getBearing());
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(br, new IntentFilter("it.test.speed"));
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, SpeedService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        verifyGPS();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            SpeedService.MyBinder b = (SpeedService.MyBinder) binder;
            s = b.getService();
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

    private void verifyGPS () {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(((ColorDrawable)imgStatusGPS.getBackground()).getColor() == getResources().getColor(R.color.grey)) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Per una rilevazione più precisa della tua velocità è necessario attivare il GPS.\n\nVuoi attivarlo?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                builder2.setMessage("La geolocalizzazione potrebbe non essere precisa!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder2.create();
                                alert.show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                imgStatusGPS.setBackgroundColor(getResources().getColor(R.color.green));
                startService(new Intent(context, SpeedService.class));

            }
        } else {
            imgStatusGPS.setBackgroundColor(getResources().getColor(R.color.grey));
            s.onDestroy();
        }
    }

}
