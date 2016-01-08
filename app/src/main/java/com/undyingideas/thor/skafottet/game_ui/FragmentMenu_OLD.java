package com.undyingideas.thor.skafottet.game_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.undyingideas.thor.skafottet.HighScoreFragment;
import com.undyingideas.thor.skafottet.MultiPlayerFragment;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.SettingsActivity;
import com.undyingideas.thor.skafottet.utility.GameUtility;

public class FragmentMenu_OLD extends Fragment {

    private Button StartBtn, instructionBtn, preferenceBtn;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)  {
        onCreate(savedInstanceState);
        final View rot = inflater.inflate(R.layout.fragment_main,container,false);

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

            final Bundle bundle = new Bundle();
            bundle.putBoolean(GameUtility.KEY_IS_HOT_SEAT, false);
            final HangmanButtonFragment fragment = new HangmanButtonFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, fragment).addToBackStack(null)
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
            final Intent PlayerListActivity = new Intent(getActivity(), com.undyingideas.thor.skafottet.game_ui.hichscorecontent.PlayerListActivity.class);
            startActivity(PlayerListActivity);
        }
    }

    private class GetWordsClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {

        final WordPickerFragment fragment = new WordPickerFragment();
        getFragmentManager().beginTransaction()
               .add(R.id.fragment_content, fragment).addToBackStack(null)
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
                    .replace(R.id.fragment_content, fragment)
                        .addToBackStack(null).commit();
        }
    }

    private class HighScoreClick implements View.OnClickListener{

        @Override
        public void onClick(final View v) {
            final HighScoreFragment fragment = new HighScoreFragment();
        }
    }
}
