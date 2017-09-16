package myapp.treeheight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by johnroycepunay on 19/08/16.
 */
public class Cover extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
    }
}
