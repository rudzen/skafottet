package com.undyingideas.thor.skafottet.dialogs;

import android.support.v4.app.DialogFragment;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class Login extends DialogFragment {
 /*   public interface LoginResult{
        void onDone(boolean result);
    }

    public Login(){

    }
    public static Login newInstance(){
        final Login l = new Login();

        return l;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Login");
        ad.setMessage("Enter name");
        ad.setPositiveButton("Ok", new OnResultClick(true));
        ad.setNegativeButton("Cancel", new OnResultClick(false));
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
            listener =  (LoginResultListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " says : 'aaaargh i'm dead!'");
        }
    } */
}
