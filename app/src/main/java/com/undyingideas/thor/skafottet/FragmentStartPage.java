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
    public View onCreateView(final LayoutInflater i, final ViewGroup container, final Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        final View rot = i.inflate(R.layout.fragment_main,container,false);

        rot.findViewById(R.id.btnStart).setOnClickListener(new StartClick());
        rot.findViewById(R.id.btnInstructions).setOnClickListener(new InstructionClick());
        rot.findViewById(R.id.btnSettings).setOnClickListener(new PreferenceClick());
        rot.findViewById(R.id.btnWordList).setOnClickListener(new GetWordsClick());
        rot.findViewById(R.id.btnMultiplayer).setOnClickListener(new StartMultiPlayerClick());

        return rot;
    }

    /**
     * For starting a new singlePlayerGame
     */
    private class StartClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {

            final Bundle b = new Bundle();
            b.putBoolean("isHotSeat", false);
           final HangmanButtonFragment fragment = new HangmanButtonFragment();
            fragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, fragment).addToBackStack(null)
                    .commit();
        }
    }

    private class PreferenceClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {
            startActivity(
                    new Intent(getActivity(), SettingsActivity.class));
        }
    }

    private class InstructionClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {
            final Intent Instillinger = new Intent(getActivity(), Instructions.class);
            startActivity(Instillinger);
        }
    }

    private class GetWordsClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {

        final WordPicker fragment = new WordPicker();
        getFragmentManager().beginTransaction()
               .add(R.id.fragmentindhold, fragment).addToBackStack(null)
                .commit();
        }
    }

    private class StartMultiPlayerClick implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final MultiPlayerFragment fragment = new MultiPlayerFragment();
            final Bundle multiplayerData = new Bundle();
//            multiplayerData.putStringArrayList("muligeOrd", getArguments().getStringArrayList("muligeOrd"));
            fragment.setArguments(multiplayerData);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, fragment)
                        .addToBackStack(null).commit();
        }
    }
}
