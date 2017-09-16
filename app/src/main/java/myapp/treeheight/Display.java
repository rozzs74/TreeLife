package myapp.treeheight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by royce on 12/27/2015.
 */
public class Display extends Activity implements View.OnClickListener  {

    private static String usernames,three,locs;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);



        final  String diameter =getIntent().getStringExtra("diameter");
        final EditText editDiameter = (EditText) findViewById(R.id.etDiameter);
        editDiameter.setText(diameter);
        editDiameter.setFocusable(false);
        final String ages = getIntent().getStringExtra("age");
        final TextView txtAge = (TextView) findViewById(R.id.etTreeAge);
        txtAge.setText(ages);
        txtAge.setFocusable(false);



        String age = txtAge.getText().toString();
        //String.format("%.0f", age);

        //final double treeAge = Double.parseDouble(age);
        //final double diameter = Double.parseDouble(editDiameter.getText().toString());












    }

//    @Override
//    public void onBackPressed() {
//
//        new AlertDialog.Builder(this)
//                .setTitle("Really Exit?")
//                .setMessage("Are you sure you will not save this to the database?")
//                .setNegativeButton(android.R.string.no, null)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        String username = String.valueOf(usernames);
//                        String treees = String.valueOf(three);
//                        String loccs = String.valueOf(locs);
//
//                        Intent ourIntent2 = new Intent(Display.this, Camera_Luzon.class);
//                        ourIntent2.putExtra("Username",username);
//                        ourIntent2.putExtra("Treename",treees);
//                        ourIntent2.putExtra("Location",loccs);
//
//                        startActivity(ourIntent2);
//                        // finish();
//                    }
//                }).create().show();
//    }








    public void onClick (View view){

        //  dd.setText(String.format("%.2f", diameter));

        // TODO Auto-generated method stub
        try {



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

    //  @Override
    // public void onClick(View v) {
    // switch(v.getId()) {

    //   case R.id.btnCamera:
    //      Intent ourIntent2 = new Intent(this, Camera.class);
    //      startActivity(ourIntent2);
    //       break;

    //    default:
    //         break;
    // }
    // }

}
