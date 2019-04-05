package com.example.tripvanguard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class mainDatabase extends AppCompatActivity {

    EditText from;
    EditText to;
    TextView info;
    Button query;
    TextView title;
    TextView title2;
    TextView info2;

    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_database);

        from = (EditText)findViewById(R.id.fromfield);
        to = (EditText)findViewById(R.id.tofield);
        query = (Button)findViewById(R.id.buttonquery);
        info = (TextView)findViewById(R.id.infofield);
        info2 = (TextView)findViewById(R.id.info2_field);
        title = (TextView)findViewById(R.id.title_field);
        title2 = (TextView)findViewById(R.id.title2_field);

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }

    public void getData(){
        databaseAccess.open();

        String fromData = from.getText().toString().trim();
        String toData = to.getText().toString().trim();

        ArrayList<String> arrayList = databaseAccess.getInfo(fromData,toData);

        getBusDetails(arrayList);
        // getOtherVehicle(arrayList);
    }

    public void getBusDetails(ArrayList<String> arrayList){
        if(arrayList.size()==2){
            String routeName = arrayList.get(0)+arrayList.get(1);
            String titleroute= arrayList.get(0)+" to "+arrayList.get(1);
            title.setText(titleroute);
            String data=null;

            data =  databaseAccess.getBusInfo(routeName)+"\n";
            data =data+databaseAccess.getOtherVehicle(routeName)+"\n";
            data =data+"\n"+ databaseAccess.getTrainInfo(routeName)+"\n";
            info.setText(data);
        }
        else if(arrayList.size()==3){
            String routeName1 = arrayList.get(0)+arrayList.get(1);
            String routeName2 = arrayList.get(1)+arrayList.get(2);
            String title1route = arrayList.get(0)+" to "+arrayList.get(1);
            String title2route = arrayList.get(1)+" to "+arrayList.get(2);
            title.setText(title1route);
            title2.setText(title2route);
            String data=null;

            if(databaseAccess.getBusInfo(routeName1)!=null){
                data = databaseAccess.getBusInfo(routeName1);
            }
            if(databaseAccess.getOtherVehicle(routeName1)!=null){
                data =data+"\n" +databaseAccess.getOtherVehicle(routeName1);
            }
            if(databaseAccess.getTrainInfo(routeName1)!=null){
                data =data+"\n"+ databaseAccess.getTrainInfo(routeName1)+"\n";
            }
            info.setText(data);

            String route2Data=null ;
            route2Data =databaseAccess.getBusInfo(routeName2)+"\n";
            route2Data =route2Data +databaseAccess.getOtherVehicle(routeName2)+"\n";
            route2Data =route2Data+ databaseAccess.getTrainInfo(routeName2)+"\n";

            info2.setText(route2Data);
        }
    }
}
