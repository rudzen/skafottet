package com.undyingideas.thor.skafottet;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class FragmentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);



        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putStringArrayList("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd"));

            FragmentStartPage fragment = new FragmentStartPage();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentindhold, fragment)  // tom container i layout
                    .commit();
        }

    }

}
