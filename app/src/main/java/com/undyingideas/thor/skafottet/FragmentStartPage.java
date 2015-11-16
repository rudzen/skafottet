package com.undyingideas.thor.skafottet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentStartPage extends Fragment {

    private Button StartBtn, instructionBtn, preferenceBtn;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        View rot = i.inflate(R.layout.fragment_main,container,false);


        rot.findViewById(R.id.btnStart).setOnClickListener(new StartClick());
        rot.findViewById(R.id.btnInstructions).setOnClickListener(new InstructionClick());
        rot.findViewById(R.id.btnSettings).setOnClickListener(new PreferenceClick());
        rot.findViewById(R.id.btnWordList).setOnClickListener(new GetWordsClick());


//        findViewById(R.id.btnMultiplayer).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), MultiplayerActivity.class)
//                        .putExtra("muligeOrd",getIntent().getStringArrayListExtra("muligeOrd")));
//            }
//        });

        return rot;

    }



    public class StartClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Bundle b = new Bundle();
            b.putStringArrayList("muligeOrd", getArguments().getStringArrayList("muligeOrd"));
            HangmanButtonFragment fragment = new HangmanButtonFragment();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, fragment)
                    .commit();
        }
//        startGame.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
            //Intent startGame = new Intent(MainActivity.this, LoadingScreen.class);



    }


    public class PreferenceClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), Preferences.class));
        }
    }


    public class InstructionClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent Instillinger = new Intent(getActivity(), Instructions.class);
            startActivity(Instillinger);
        }
    }

    public class GetWordsClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
      //      Intent wordPicker = new Intent(getActivity(), WordPicker.class);
//        wordPicker.putExtra("muligeOrd", getIntent().getStringArrayListExtra("muligeOrd"));
       //     startActivity(wordPicker);
        WordPicker fragment = new WordPicker();
        getFragmentManager().beginTransaction()
               .replace(R.id.fragmentindhold, fragment)
                .commit();
        }
    }

}
