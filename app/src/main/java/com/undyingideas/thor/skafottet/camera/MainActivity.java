package com.undyingideas.thor.skafottet.camera;

import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    SensorManager sensorManager;
    String[] senesteMålinger = new String[100];
    TextView textView;
    int framerate = 250;
    int delay = 1000/framerate;

    double ts;
    double t;
    Camera cam;
    M3 Rz;
    V3 focusPoint;
    GridLayout layout;
    Button btnStopStart;
    boolean simulationOn;
    float phiPivotOnPhone;
    V3 u;
    M3 Su;
    V3[] cube=new V3[8];
    V3 c=new V3(0,0,0);
    double y;


    //Projectile motion
    double g=9.82;    // m/(s*s)
    V2 r0=new V2(0, 0);
    double ang = 45;
    double ang2 = 60;

    V2 v0=new V2(Math.cos(Math.toRadians(ang))*10, Math.sin(Math.toRadians(ang))*10);
    V2 v1 = new V2(Math.cos(Math.toRadians(ang2))*10, Math.sin(Math.toRadians(ang2))*10);
    V2 a=new V2(0,-g);
    private Button btnMoveCloser;
    private Button btnMoveAway;
    private Button btnMoveTowards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new GridLayout(this);
        GridLayout top = new GridLayout(this);

        top.setColumnCount(4);
        top.setRowCount(1);

        layout.setColumnCount(1);
        layout.setRowCount(2);


        btnStopStart = new Button(this);
        btnStopStart.setOnClickListener(new clickHandlerStopStart());
        btnStopStart.setText("Stop");

        btnMoveCloser = new Button(this);
        btnMoveCloser.setOnClickListener(new moveCloserClickhandler());
        btnMoveCloser.setText("+");

        btnMoveAway = new Button(this);
        btnMoveAway.setOnClickListener(new moveAwayClickhandler());
        btnMoveAway.setText("-");

        btnMoveTowards = new Button(this);
        btnMoveTowards.setOnClickListener(new moveTowardsClickhandler());
        btnMoveTowards.setText("Move");


//        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLUE);

        //Init camera
        cam = new Camera(this,100,100,500,500,paint);

        top.addView(btnStopStart);
        top.addView(btnMoveCloser);
        top.addView(btnMoveAway);
        top.addView(btnMoveTowards);
        layout.addView(top);
        layout.addView(cam);

        ts=System.currentTimeMillis();


        //Den axis som roteres om
        u = new V3(0,0,1).unit();
        Su = new M3(
                0,   -u.z,  u.y,
                u.z ,   0 , -u.x,
                -u.y, u.x , 0);
        double phi=Math.PI/100;
        //Rotation matric
        Rz=M3.inverse.add(Su.mul(Math.sin(phi))).add(Su.mul(Su).mul(1-Math.cos(phi)));

        y=0;



        setContentView(layout);

    }

    class clickHandlerStopStart implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(simulationOn){
                onPause();
                simulationOn = false;
                btnStopStart.setText("Start");

            }else{
                onResume();
                simulationOn = true;
                btnStopStart.setText("Stop");

            }
        }
    }

    class moveCloserClickhandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {

                cam.moveCameraTo(cam.getCameraCenter().add(-1));
                cam.setFocusPoint(cam.getFocusPoint().add(-1));

        }
    }

    class moveAwayClickhandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            cam.moveCameraTo(cam.getCameraCenter().add(1));
            cam.setFocusPoint(cam.getFocusPoint().add(1));

        }
    }

    class moveTowardsClickhandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            V3 a = cam.getCameraCenter();
            V3 b = cam.getFocusPoint();
            V3 c = b.sub(a).mul(0.05);
            cam.moveCameraTo(a.add(c));
            cam.setFocusPoint(b.add(c));

        }

    }




    @Override
    protected void onResume(){
        super.onResume();
        int hyppighed = SensorManager.SENSOR_DELAY_UI;
        //int hyppighed = 250000000; // 4 gange i sekundet

        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, hyppighed);

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("nu blev onPause() kaldt");
        sensorManager.unregisterListener(this);

    }

    public void onSensorChanged(SensorEvent e) {
        int sensortype = e.sensor.getType();

        String måling = "Type " + sensortype + " " + e.sensor.getName() + "\n"
                + "Fra: " + e.sensor.getVendor() + "  (" + e.sensor.getPower() + " mA)\n"
                + "Tid: " + e.timestamp + "  præcision: " + e.accuracy + "\n";

        for (float v : e.values)
            måling = måling + String.format("%9.4f", v); // Normalt 3, men det er set på en Nexus 5 at der er en sensor med kun 1 værdi og med 5 værdier


        //Velosity change
        t=(System.currentTimeMillis()-ts)/1000.0;



        V2 v=v(t,v0);
        V3 r2=r(t,v1);
        V2 v2=v(t,v1);


//        c.paintCube(g, c, S);



//      traj.add(r);
        cam.traj.add(r2);

        if(r2.z < 0){
            cam.traj.clear();
            ts = System.currentTimeMillis();

        }






//        cam.setFocusPoint(Rz.mul(cam.getFocusPoint().sub(cam.getCameraCenter())).add(cam.getCameraCenter()));


        double degreeY = e.values[1];
        double degreeX= e.values[0];

        //if degreeY is -90 phone is looking horizontal
        if(degreeY < 0) {
            //Plus with 90 and flip
            degreeY = -1 * (degreeY + 90);
            //
            if(degreeY < 0)
                degreeY = 360 + degreeY;
        }
        else{
            degreeY = 270 - degreeY;
        }



        cam.pivor = degreeY;
        cam.northDegree = degreeX;




        double xOffset = cam.getCameraCenter().x;
        double yOffset = cam.getCameraCenter().y;
        double zOffset = cam.getCameraCenter().z;


        //FINDING  HEIGHT OF Z projektet onto plane.

        V3 upDownPoint = new V3(xOffset-Math.cos(Math.toRadians(degreeY))*10,yOffset,zOffset+Math.sin(Math.toRadians(degreeY))*10);




        V3 turnPoint = new V3(xOffset+(-Math.sin(Math.toRadians(degreeX))*10),yOffset+(-Math.cos(Math.toRadians(degreeX))*10),0);

        V2 turn = new V2(xOffset+(-Math.sin(Math.toRadians(degreeX))*10),yOffset+(-Math.cos(Math.toRadians(degreeX))*10));
        V2 upDown = new V2(xOffset-Math.cos(Math.toRadians(degreeY))*10,yOffset);

        V2 projection = turn.projection(upDown);

        V3 cameraFocus = new V3(turnPoint.x,turnPoint.y,upDownPoint.z);


        cam.setFocusPoint(cameraFocus);
//        System.out.println(cameraFocusPoint.toString());


//        System.out.println(cam.getCameraCenter().x +" X "+ cam.getCameraCenter().y + " Y " + cameraFocusPoint.z + " Z ");
//        System.out.println("Camera looking at: " + cam.getFocusPoint().getX() + " X "
//                + cam.getFocusPoint().getY() + " Y "
//                + cam.getFocusPoint().getZ() + " Z ");

        cam.redraw();


    }


    V3 r(double t, V2 v0){
        V2 v = a.mul(0.5*t*t).add(v0.mul(t)).add(r0);
        return new V3(0,v.x,v.y);
//      return -0.5*g*t*t+v0*t+x0;
    }

    V2 v(double t, V2 v0){
        return a.mul(t).add(v0);
//      return -g*t+v0;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int præcision) {
        // ignorér - men vi er nødt til at have metoden for at implementere interfacet
    }






}

