package com.undyingideas.thor.skafottet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Just a Yes / No dialog fragment.
 *     <li>Calling class <b>MUST</b> implement 'YesNoResultListener'</li>
 *     <li>Instance <b>MUST</b> by created through YesNo.newInstance()</li>
 * by rudz.
 */
public class YesNo extends DialogFragment {
    // TODO : Re-write

    private YesNoResultListener listener;

    public interface YesNoResultListener { void onDone(boolean result); }

    public YesNo() { }

    public static YesNo newInstance(final String title, final String text, final String yesButton, final String noButton) {
        final YesNo frag = new YesNo();
        final Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putString("text", text);
        arg.putString("yesButton", yesButton);
        arg.putString("noButton", noButton);
        frag.setArguments(arg);
        Log.d("lol", "yesNo");
        return frag;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(getArguments().getString("title"));
        ad.setMessage(getArguments().getString("text"));
        ad.setPositiveButton(getArguments().getString("yesButton"), new OnResultClick(true));
        ad.setNegativeButton(getArguments().getString("noButton"), new OnResultClick(false));
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