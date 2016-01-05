/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.wordlist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.undyingideas.thor.skafottet.R;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

/**
 * VERY Double edit text input dialogfragment subclass.<br>
 * "Bookmark" add style with url regex matching.<br>
 * Created by rudz on 17-11-2015.
 * @author rudz
 */
public class AddWordListDialog extends DialogFragment {

    public interface AddWordListListener {
        void onFinishAddWordListDialog(String title, String url, boolean startDownload);
    }

    private Pattern s_validHttp;

    public static AddWordListDialog newInstance(final String title, final String okButton, final String cancelButton, final boolean cancelable) {
        final AddWordListDialog frag = new AddWordListDialog();
        final Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putString("okButton", okButton);
        arg.putString("cancelButton", cancelButton);
        arg.putBoolean("cancelable", cancelable);
        frag.setArguments(arg);
        return frag;
    }

    public AddWordListDialog() {
        s_validHttp = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    private boolean isValid() {
        if (ViewHolder.s_editURL == null) return false;
        final String url = ViewHolder.s_editURL.getText().toString();
        final boolean validStuff = !ViewHolder.s_editTitle.getText().toString().isEmpty() && !url.isEmpty() && s_validHttp.matcher(url).find();
        if (validStuff) return true;
        Toast.makeText(getActivity(), "Forkert indtastede informationer.", Toast.LENGTH_SHORT).show();
        return false;
    }

    private final static class ViewHolder {
        public static EditText s_editTitle, s_editURL;
        public static Button s_btnOk, s_btnCancel;
        public static CheckBox s_chkDLnow;
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_add_wordlist, container);

        getDialog().setTitle(getArguments().getString("title"));
        getDialog().setCancelable(getArguments().getBoolean("cancelable"));

        ViewHolder.s_editTitle = (EditText) view.findViewById(R.id.dialog_add_word_list_edit_title);
        ViewHolder.s_editURL = (EditText) view.findViewById(R.id.dialog_add_word_list_edit_url);
        ViewHolder.s_btnOk = (Button) view.findViewById(R.id.btn_dialog_add_wordlist_ok);
        ViewHolder.s_btnCancel = (Button) view.findViewById(R.id.btn_dialog_add_wordlist_cancel);
        ViewHolder.s_chkDLnow = (CheckBox) view.findViewById(R.id.chk_dialog_add_wordlist_download);

        final EditTextClickHandler handler = new EditTextClickHandler(this);
        ViewHolder.s_editTitle.setOnKeyListener(handler);
        ViewHolder.s_editURL.setOnKeyListener(handler);

        ViewHolder.s_btnOk.setOnClickListener(new OnResultClick(this, true));
        ViewHolder.s_btnCancel.setOnClickListener(new OnResultClick(this, false));

        ViewHolder.s_btnOk.setText(getArguments().getString("okButton"));
        ViewHolder.s_btnCancel.setText(getArguments().getString("cancelButton"));

        ViewHolder.s_editTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    @SuppressWarnings({"AssignmentToNull", "AssignmentToStaticFieldFromInstanceMethod"})
    private void postResult() {
        final AddWordListListener activity = (AddWordListListener) getActivity();

        /* grab what we need from the views */
        final String title = ViewHolder.s_editTitle.getText().toString();
        final String url = ViewHolder.s_editURL.getText().toString();
        final boolean dlnow = ViewHolder.s_chkDLnow.isChecked();

        /* null them */
        ViewHolder.s_btnCancel = null;
        ViewHolder.s_btnOk = null;
        ViewHolder.s_editTitle = null;
        ViewHolder.s_editURL = null;
        ViewHolder.s_chkDLnow = null;

        activity.onFinishAddWordListDialog(title, url, dlnow);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    private static class EditTextClickHandler implements View.OnKeyListener {

        private final WeakReference<AddWordListDialog> addWordListDialogWeakReference;

        public EditTextClickHandler(final AddWordListDialog addWordListDialog) {
            addWordListDialogWeakReference = new WeakReference<>(addWordListDialog);
        }

        @Override
        public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
            final AddWordListDialog addWordListDialog = addWordListDialogWeakReference.get();
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
        private static boolean isCorrectAction(final int keyCode, final KeyEvent event, final AddWordListDialog addWordListDialog) {
            return event.getAction() == KeyEvent.ACTION_DOWN
                    && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                    && addWordListDialog.isValid();
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
