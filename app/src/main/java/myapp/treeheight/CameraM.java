package myapp.treeheight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

/**
 * Created by johnroycepunay on 19/08/16.
 */
public class CameraM extends Activity implements CvCameraViewListener2, View.OnTouchListener, View.OnClickListener {

    EditText txtDBH,txtCRL; Button btnNext,btnNextCam,btnPrevCam,btngetAge;
    private static final String TAG = "Open Computer Vision!";
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mRgbaF;
    private Mat mRgbaT;
    private CameraBridgeViewBase mOpenCvCameraView;

    double age,diameter,growthfactor,circumference;
    double distance = 200;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraM.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_medium_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camVertical);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        txtDBH = (EditText) findViewById(R.id.etDBH);
        txtCRL = (EditText) findViewById(R.id.etCRL);
        btngetAge = (Button) findViewById(R.id.btnAge);
        btngetAge.setOnClickListener(this);
        btnNextCam = (Button) findViewById(R.id.btnNext);
        btnNextCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CameraM.this, CameraL.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
//                new AlertDialog.Builder(CameraM.this)
//                        .setTitle("Really Exit?")
//                        .setMessage("Are you sure you want to exit Camera?")
//                        .setNegativeButton(android.R.string.no, null)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                Intent ourIntent2 = new Intent(CameraM.this, CameraL.class);
//                                startActivity(ourIntent2);
//                                finish();
//                            }
//                        }).create().show();
            }
        });
        btnPrevCam = (Button) findViewById(R.id.btnPrev);
        btnPrevCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CameraM.this, CameraS.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
//                Intent ourIntent2 = new Intent(CameraM.this, CameraS.class);
//                startActivity(ourIntent2);
//                finish();
//                new AlertDialog.Builder(CameraM.this)
//                        .setTitle("Really Exit?")
//                        .setMessage("Are you sure you want to exit Camera?")
//                        .setNegativeButton(android.R.string.no, null)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                Intent ourIntent2 = new Intent(CameraM.this, CameraS.class);
//                                startActivity(ourIntent2);
//                                finish();
//                            }
//                        }).create().show();
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        new AlertDialog.Builder(this)
                .setTitle("Testing?")
                .setMessage("Test the alert dialog")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
    }

    public CameraM() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        //ChangeVersion if use latest ver
    }


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC4);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat();
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



    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

//
//    public void checkValues(){
//
//
//    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit Camera?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intentBackhome = new Intent(CameraM.this, Home.class);
                        startActivity(intentBackhome);
                        finish();
                    }
                }).create().show();
    }


    @Override
    public void onClick(View v) {



        diameter = ((distance / 0.0328084) * 2) / 3.7321;
        circumference = diameter * 3.1416;

        if (diameter <= 9) {
            growthfactor = 0.21;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }   else if (diameter >= 10 && diameter <= 14) {
            growthfactor = 0.29;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 15 && diameter <= 19) {
            growthfactor = 0.37;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 20 && diameter <= 24) {
            growthfactor = 0.45;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 25 && diameter <= 29) {
            growthfactor = 0.56;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 30 && diameter <= 34) {
            growthfactor = 0.55;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 35 && diameter <= 39) {
            growthfactor = 0.59;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }  else if (diameter >= 40 && diameter <= 44) {
            growthfactor = 0.61;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 45 && diameter <= 49) {
            growthfactor = 0.63;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 50 && diameter <= 54) {
            growthfactor = 0.64;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }  else if (diameter >= 55 && diameter <= 59) {
            growthfactor = 0.64;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 60 && diameter <= 64) {
            growthfactor = 0.63;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 65 && diameter <= 69) {
            growthfactor = 0.61;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }  else if (diameter >= 70 && diameter <= 74) {
            growthfactor = 0.58;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 75 && diameter <= 79) {
            growthfactor = 0.55;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else if (diameter >= 80 && diameter <= 84) {
            growthfactor = 0.52;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }  else if (diameter >= 85 && diameter <= 89) {
            growthfactor = 0.51;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        } else  if (diameter >= 90) {
            growthfactor = 0.48;
            age = (diameter * growthfactor);
            Intent intent = new Intent(CameraM.this, Display.class);
            intent.putExtra("diameter", (String.format("%.2f", diameter)));
            intent.putExtra("age", (String.format("%.0f", age)));
            startActivity(intent);
            finish();
        }

    }
}




