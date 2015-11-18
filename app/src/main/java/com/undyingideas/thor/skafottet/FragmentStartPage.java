package com.undyingideas.thor.skafottet;

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
        rot.findViewById(R.id.btnMultiplayer).setOnClickListener(new StartMultiPlayerClick());

        return rot;
    }

    public class StartClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Bundle b = new Bundle();
            b.putBoolean("isHotSeat", false);
           PlayFragment fragment = new PlayFragment();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, fragment).addToBackStack(null)
                    .commit();
        }
    }

    public class PreferenceClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startActivity(
                    new Intent(getActivity(), Preferences.class));
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

        WordPicker fragment = new WordPicker();
        getFragmentManager().beginTransaction()
               .add(R.id.fragmentindhold, fragment).addToBackStack(null)
                .commit();
        }
    }

    private class StartMultiPlayerClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MultiPlayerFragment fragment = new MultiPlayerFragment();
            Bundle multiplayerData = new Bundle();
//            multiplayerData.putStringArrayList("muligeOrd", getArguments().getStringArrayList("muligeOrd"));
            fragment.setArguments(multiplayerData);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, fragment)
                        .addToBackStack(null).commit();
        }
    }
}
