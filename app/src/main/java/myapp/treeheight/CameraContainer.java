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
public class CameraContainer extends  TabActivity {

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_container_layout);
        Resources resources = getResources();
        final TabHost tabHost = getTabHost();

        //HomeTab(FirstTab)
        Intent intentHometab = new Intent().setClass(this, CameraVertical.class);
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
        tabHost.addTab(tabSpecLogouttab);
        tabHost.setCurrentTab(0);





    }


}
