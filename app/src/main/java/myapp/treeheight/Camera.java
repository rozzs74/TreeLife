package myapp.treeheight;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;



public class Camera extends Activity implements CvCameraViewListener2, OnTouchListener, OnClickListener {

    private static final String TAG = "OCVSample::Activity";

    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mRgbaF;
    private Mat mRgbaT;

    Button buttonAge;


    double circumference;
    double diameter;
    double distanceFloat;
    double distanceft;

    private CameraBridgeViewBase mOpenCvCameraView;

    //bluetooth
    private static final String TAG1 = "bluetooth2";
    TextView txtArduino;
    TextView dd;
    Handler h;
    private static String sbprint;
    private static String passingdata;
    private static String passingdatas;
    private static String usernames;
    private static String sbprint2;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "20:15:03:12:13:33";

    private View rootView;

    float test = 5;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(Camera.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    public Camera() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        buttonAge = (Button) findViewById(R.id.btAge);

        setContentView(R.layout.content_main);

        String Treename = getIntent().getStringExtra("Treename");
        TextView txtTreename = (TextView) findViewById(R.id.textGrowthfactor);
        txtTreename.setText(Treename);
        txtTreename.setVisibility(View.INVISIBLE);
        String location = getIntent().getStringExtra("Location");
        TextView txtLocation = (TextView) findViewById(R.id.textTree);
        txtLocation.setText(location);
        txtLocation.setVisibility(View.INVISIBLE);
        final String username = getIntent().getStringExtra("Username");
        TextView tv = (TextView) findViewById(R.id.TVusername);
        tv.setText(username);
        tv.setVisibility(View.INVISIBLE);

        passingdata = txtTreename.getText().toString();
        passingdatas = txtLocation.getText().toString();
        usernames = tv.getText().toString();
        final View line1 = (View) findViewById(R.id.view);
        final View line2 = (View) findViewById(R.id.view3);
        //final View line3 = (View) findViewById(R.id.view3);
        // final View line4 = (View) findViewById(R.id.view4);
        // line3.setVisibility(View.INVISIBLE);
        // line4.setVisibility(View.INVISIBLE);
        // final View line5 = (View) findViewById(R.id.view5);
        // final View line6 = (View) findViewById(R.id.view6);
        //  line5.setVisibility(View.INVISIBLE);
        //  line6.setVisibility(View.INVISIBLE);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.MainActivityCameraView);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        txtArduino = (TextView) findViewById(R.id.txtArduino); // for display the received data from the Arduino

        h = new Handler() {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());   // and clear
                            //  String sbprint2 = String.valueOf(String.format("%.2d", sbprint));
                            txtArduino.setText("Distance: " + sbprint + " ft");            // update TextView

                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };




        // String distance = String.valueOf(sbprint );
        //Float fDistance = (Float.parseFloat(distance));
        //distanceFloat = (fDistance.floatValue());
        //distanceft = distanceFloat * 0.0328084;

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        buttonAge = (Button) findViewById(R.id.btAge);
        buttonAge.setOnClickListener(this);
        // txtArduino.setText("Distance: " + test);


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG1, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        Log.d(TAG1, "...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
        finish();
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null

        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            try {
                if (btAdapter.isEnabled()) {
                    Log.d(TAG1, "...Bluetooth ON...");

                } else {
                    //Prompt user to turn on Bluetooth
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
            } catch (Exception e) {

            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);

        Log.d(TAG1, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG1, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG1, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG1, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {

        //3
        // mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);  // NOTE width,width is NOT a typo
        // mIntermediateMat = new Mat();

        mGray = new Mat(height, width, CvType.CV_8UC4);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat();

        // 2   // mRgba = new Mat(height, width, CvType.CV_8UC4);
        // mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        // mGray = new Mat(height, width, CvType.CV_8UC1);

    }

    public void onCameraViewStopped() {
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        //90degrees
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, mRgba, 1);


        return mRgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }


    }


    public void onClick(View view) {


        try {
            double age;
            double growthfactor;
            // double degrees = 75.0;
            //  double radians = Math.toRadians(degrees);
            String gr = String.valueOf(passingdata);
            String tr = String.valueOf(passingdatas);
            String username = String.valueOf(usernames);
            //distanceft = distanceFloat * 0.0328084;
            String distance = String.valueOf(sbprint);
            Float fDistance = (Float.parseFloat(distance));
            distanceFloat = (fDistance.floatValue());

            //1  diameter = ((distanceFloat / 0.0328084) * 2 ) / Math.tan(radians);
            //2 circumference = diameter / 3.141592654;

            diameter = ((distanceFloat / 0.0328084) * 2) / 3.7321;
            circumference = diameter * 3.1416;
            // orig diameter = (((distanceFloat / 0.0328084)  * 2 ) - 15) / 3.141592654;
            //orig circumference = diameter *3.141592654;
            // circumference = ((distanceFloat * 2) - 0.5) / 3.141592654*12;
            //diameter = (circumference / 2.54);


            if (diameter <= 9) {
                growthfactor = 0.21;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }   else if (diameter >= 10 && diameter <= 14) {
                growthfactor = 0.29;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 15 && diameter <= 19) {
                growthfactor = 0.37;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 20 && diameter <= 24) {
                growthfactor = 0.45;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 25 && diameter <= 29) {
                growthfactor = 0.56;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 30 && diameter <= 34) {
                growthfactor = 0.55;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 35 && diameter <= 39) {
                growthfactor = 0.59;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }  else if (diameter >= 40 && diameter <= 44) {
                growthfactor = 0.61;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 45 && diameter <= 49) {
                growthfactor = 0.63;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 50 && diameter <= 54) {
                growthfactor = 0.64;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }  else if (diameter >= 55 && diameter <= 59) {
                growthfactor = 0.64;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 60 && diameter <= 64) {
                growthfactor = 0.63;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 65 && diameter <= 69) {
                growthfactor = 0.61;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }  else if (diameter >= 70 && diameter <= 74) {
                growthfactor = 0.58;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 75 && diameter <= 79) {
                growthfactor = 0.55;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else if (diameter >= 80 && diameter <= 84) {
                growthfactor = 0.52;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }  else if (diameter >= 85 && diameter <= 89) {
                growthfactor = 0.51;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            } else  if (diameter >= 90) {
                growthfactor = 0.48;
                age = (diameter * growthfactor);
                Intent intent = new Intent(Camera.this, Display.class);
                intent.putExtra("diameter", (String.format("%.2f", diameter)));
                intent.putExtra("Treename", gr);
                intent.putExtra("Location", tr);
                intent.putExtra("age", (String.format("%.0f", age)));
                intent.putExtra("Username", username);
                intent.putExtra("Circumference",(String.format("%.2f", circumference)) );
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Hardware must be set!", Toast.LENGTH_SHORT).show();
        }


        ///  AlertDialog.Builder alert = new AlertDialog.Builder(this);
        ////  alert.setTitle("Tree's Diameter");
        // alert.setMessage("Message");
// Create TextView
        //   final TextView dd = new TextView (this);
        //  alert.setView(dd);
        ////  dd.setText(String.format("Diameter:%.2f", diameter));
        // alert.show();


    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit Camera?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        String username = String.valueOf(usernames);
                        Intent ourIntent2 = new Intent(Camera.this, Home.class);
                        ourIntent2.putExtra("Username", username);
                        startActivity(ourIntent2);
                        finish();
                    }
                }).create().show();
    }

}
