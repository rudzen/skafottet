package com.undyingideas.thor.skafottet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Theis' on 07-01-2016.
 */
public class createUser extends DialogFragment {
    @Nullable
    private YesNoResultListener listener;

    public interface YesNoResultListener { void onDone(boolean result); }

    public createUser() { }

    public static createUser newInstance(final String title, final String text, final String yesButton) {
        final createUser frag = new createUser();
        final Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putString("text", text);
        arg.putString("yesButton", yesButton);
        frag.setArguments(arg);
        Log.d("lol", "yesNo");
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(getArguments().getString("title"));
        ad.setMessage(getArguments().getString("text"));
        ad.setView(new EditText(null)); // TODO
        ad.setCancelable(true);
        ad.setPositiveButton(getArguments().getString("okButton"), new OnResultClick(true));
        return ad.create();
    }

    private class OnResultClick implements DialogInterface.OnClickListener {
        private final boolean re;

        public OnResultClick(final boolean res) { re = res; }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            listener.onDone(re);
            dismiss();
        }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            listener =  (YesNoResultListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " says : 'aaaargh i'm dead!'");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
