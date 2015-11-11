package com.undyingideas.thor.skafottet.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Just a Yes / No dialog fragment.
 *     <li>Calling class <b>MUST</b> implement 'YesNoResultListener'</li>
 *     <li>Instance <b>MUST</b> by created through YesNo.newInstance()</li>
 * by rudz.
 */
public class YesNo extends DialogFragment {

    private YesNoResultListener listener;

    public interface YesNoResultListener { void onDone(boolean result); }

    public YesNo() { }

    public static YesNo newInstance(String title, String text, String yesButton, String noButton) {
        YesNo frag = new YesNo();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putString("text", text);
        arg.putString("yesButton", yesButton);
        arg.putString("noButton", noButton);
        frag.setArguments(arg);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(getArguments().getString("title"));
        ad.setMessage(getArguments().getString("text"));
        ad.setPositiveButton(getArguments().getString("yesButton"), new OnResultClick(true));
        ad.setNegativeButton(getArguments().getString("noButton"), new OnResultClick(false));
        return ad.create();
    }

    private class OnResultClick implements DialogInterface.OnClickListener {
        private boolean re;

        public OnResultClick(boolean res) { re = res; }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            listener.onDone(re);
            dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (YesNoResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " says : 'aaaargh i'm dead!'");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}