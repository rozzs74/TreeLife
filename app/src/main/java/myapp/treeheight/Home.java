package myapp.treeheight;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by johnroycepunay on 19/08/16.
 */
public class Home extends  TabActivity {

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        Resources resources = getResources();
        final TabHost tabHost = getTabHost();

        //HomeTab(FirstTab)
        Intent intentHometab = new Intent().setClass(this, Cover.class);
        final TabHost.TabSpec tabSpecHometab = tabHost
                .newTabSpec("Home")
                .setIndicator("", resources.getDrawable(R.drawable.icon_home_config))
                .setContent(intentHometab);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++){
                    //UnselectedState
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));
                }
                //SelectedState
                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#678C3F"));
            }
        });

        //SelectTab(SecondTab)
        Intent intentSelecttab = new Intent().setClass(this, Cover.class);
        final TabHost.TabSpec tabSpecSelecttab = tabHost
                .newTabSpec("Select")
                .setIndicator("", resources.getDrawable(R.drawable.icon_select_config))
                .setContent(intentSelecttab);

        //CameraTab(ThirdTab)
        Intent intentCameratab = new Intent().setClass(this, Cover.class);
        final TabHost.TabSpec tabCameratab = tabHost
                .newTabSpec("Camera")
                .setIndicator("", resources.getDrawable(R.drawable.icon_camera_config))
                .setContent(intentCameratab);

        //CameraTab(ThirdTab)
        Intent intentSettingtab = new Intent().setClass(this, Cover.class);
        final TabHost.TabSpec tabSettingtab = tabHost
                .newTabSpec("Camera")
                .setIndicator("", resources.getDrawable(R.drawable.iconsetting))
                .setContent(intentSettingtab);


        //LogoutTab(FourthTab)
        Intent intentLogouttab = new Intent().setClass(this, Cover.class);
        final TabHost.TabSpec tabSpecLogouttab = tabHost
                .newTabSpec("Logout")
                .setIndicator("", resources.getDrawable(R.drawable.icon_logout_config))
                .setContent(intentLogouttab);

        //Add all tabs
        tabHost.addTab(tabSpecHometab);
        tabHost.addTab(tabSpecSelecttab);
        tabHost.addTab(tabCameratab);
        tabHost.addTab(tabSettingtab);
        tabHost.addTab(tabSpecLogouttab);
        tabHost.setCurrentTab(0);

        final int viewcamera = 2;
        getTabWidget().getChildAt(viewcamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTabHost().setCurrentTab(viewcamera);
                final Dialog dialog = new Dialog(Home.this);
                dialog.setContentView(R.layout.choosetrees);
                dialog.setTitle("Choose Trees");
                final TextView valueTextView = (TextView) dialog.findViewById(R.id.selected);
//                final TextView valueTextViews = (TextView) dialog.findViewById(R.id.selectedtree);
                final EditText etTreename = (EditText) dialog.findViewById(R.id.etTreename);
               // valueTextViews.setVisibility(View.INVISIBLE);
               // valueTextView.setText(etTreename.getText().toString());
                valueTextView.setVisibility(View.INVISIBLE);
                final String Treename = valueTextView.getText().toString();
                Button btnSignIn = (Button) dialog.findViewById(R.id.buttonCamera);
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        String t = etTreename.getText().toString();

                        if(t.matches("")) {
                            Toast.makeText(getApplicationContext(), "Please fill the textfield!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Intent intent = new Intent(Home.this, CameraVertical.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);
                            overridePendingTransition(0,0);
                        }

                    }
                });



//                                Button btnCancel = (Button) dialog.findViewById(R.id.buttonCancel);
//                                btnCancel.setOnClickListener(new View.OnClickListener() {
//                                    public void onClick(View v) {
//                                        Intent intent2 = new Intent(Home.this, AndroidTabLayoutActivity.class);
//                                        intent2.putExtra("Username", username);
//                                        startActivity(intent2);
//                                        getTabHost().setCurrentTab(viewcamera);
//                                        finish();
//
//
//                                    }
//                                });







                dialog.show();

            }


        });


    }

    class MyData {
        public MyData( String spinnerText ) {
            this.spinnerText = spinnerText;
            //  this.value = value;
        }

        public String getSpinnerText()
        {
            return spinnerText;
        }

        //  public String getValue() {
        //    return value;
        //  }

        public String toString() {
            return spinnerText;
        }

        String spinnerText;
        String value;
    }
}
