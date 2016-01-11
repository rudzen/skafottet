package com.undyingideas.thor.skafottet.fragments.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import java.lang.ref.WeakReference;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
public class Login extends DialogFragment {
    public interface LoginListener {
        void onFinishLoginDialog(String title, String pass);
        void onCancel();
    }

    public static Login newInstance(final String title, final String okButton, final String cancelButton, final boolean cancelable) {
        final Login frag = new Login();
        final Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putString("okButton", okButton);
        arg.putString("cancelButton", cancelButton);
        arg.putBoolean("cancelable", cancelable);
        frag.setArguments(arg);
        return frag;
    }

    public Login() {

    }

    private boolean isValid() {
        if (ViewHolder.s_pass == null || ViewHolder.s_name == null) return false;
        final String pass = ViewHolder.s_pass.getText().toString();
        final boolean validStuff = !ViewHolder.s_name.getText().toString().isEmpty() && !pass.isEmpty();
        Log.d("Login", ""+GameUtility.mpc.login(ViewHolder.s_name.getText().toString()));
        if (validStuff) return true;
        Toast.makeText(getActivity(), "Forkert indtastede informationer.", Toast.LENGTH_SHORT).show();
        return false;
    }

    private final static class ViewHolder {
        public static EditText s_name, s_pass;
        public static Button s_btnOk, s_btnCancel;
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialogfragment_login, container);

        getDialog().setTitle(getArguments().getString("title"));
        getDialog().setCancelable(getArguments().getBoolean("cancelable"));

        ViewHolder.s_name = (EditText) view.findViewById(R.id.loginName);
        ViewHolder.s_pass = (EditText) view.findViewById(R.id.LoginPass);
        ViewHolder.s_btnOk = (Button) view.findViewById(R.id.btnLoginOk);
        ViewHolder.s_btnCancel = (Button) view.findViewById(R.id.btnLoginCancel);

        final EditTextClickHandler handler = new EditTextClickHandler(this);
        ViewHolder.s_name.setOnKeyListener(handler);
        ViewHolder.s_pass.setOnKeyListener(handler);


        ViewHolder.s_btnOk.setOnClickListener(new OnResultClick(this, true));
        ViewHolder.s_btnCancel.setOnClickListener(new OnResultClick(this, false));
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) ((LoginListener) getActivity()).onCancel();
            }
        });

        ViewHolder.s_btnOk.setText(getArguments().getString("okButton"));
        ViewHolder.s_btnCancel.setText(getArguments().getString("cancelButton"));

        ViewHolder.s_name.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    @SuppressWarnings({"AssignmentToNull", "AssignmentToStaticFieldFromInstanceMethod"})
    private void postResult() {
        final LoginListener activity = (LoginListener) getActivity();

        /* grab what we need from the views */
        final String title = ViewHolder.s_name.getText().toString();
        final String pass = ViewHolder.s_pass.getText().toString();

        /* null them */
        ViewHolder.s_btnCancel = null;
        ViewHolder.s_btnOk = null;
        ViewHolder.s_name = null;
        ViewHolder.s_pass = null;

        activity.onFinishLoginDialog(title, pass);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    private static class EditTextClickHandler implements View.OnKeyListener {

        private final WeakReference<Login> loginDialogWeakReference;

        public EditTextClickHandler(final Login loginDialog) {
            loginDialogWeakReference = new WeakReference<>(loginDialog);
        }

        @Override
        public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
            final Login addWordListDialog = loginDialogWeakReference.get();
            if (addWordListDialog != null) {
                if (isCorrectAction(keyCode, event, addWordListDialog)) {
                    Log.d("AddDialog", "Ok for katten");
                    addWordListDialog.postResult();
                    return true;
                }
            }
            return false;
        }

        @SuppressWarnings("OverlyComplexBooleanExpression")
        private static boolean isCorrectAction(final int keyCode, final KeyEvent event, final Login addWordListDialog) {
            return event.getAction() == KeyEvent.ACTION_DOWN
                    && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                    && addWordListDialog.isValid();
        }
    }

    private class OnCancel implements DialogInterface.OnCancelListener{

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @SuppressWarnings("StandardVariableNames")
    private class OnResultClick implements View.OnClickListener {
        private final boolean res;
        private final DialogFragment df;

        public OnResultClick(final DialogFragment df, final boolean b) {
            this.df = df;
            res = b;
        }

        @Override
        public void onClick(final View v) {
            if (res) { // ok clicked
                Log.d("AddDialog", "JODA");
                if (isValid()) postResult();
            } else { // cancel was clicked
                Log.d("AddDialog", "NEJDA");
                getActivity().getSupportFragmentManager().beginTransaction().remove(df).commit();
            }
        }
    }
}
