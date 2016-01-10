/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.game_ui.wordlist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.game_ui.wordlist.data.WordItem;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;

/**
 * WordFragmentLayout class.
 * @author rudz
 */
public class WordFragmentLayout extends AppCompatActivity implements AddWordListDialog.AddWordListListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordlist_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        toolbar.setLogo(R.mipmap.ic_launcher);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (final NullPointerException npe) {
            Log.e("WordFragmentLayout", "Error for setDisplayHomeAsUpEnabled()\n" + npe.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_word_list, menu);
            return true;
        } catch (final InflateException ie) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        /* TESTING */
        final int id = item.getItemId();
        if (id == R.id.action_word_list_add) {
            /* show Yes/No dialog here! */
            new MaterialDialog.Builder(this)
                    .content("Vil du tilføje en ordliste?")
                    .cancelable(true)
                    .onAny(new OnYesNoResponseDialog(this))
                    .positiveText(R.string.dialog_yes)
                    .negativeText(R.string.dialog_no)
                    .title("Tilføj Ordliste")
                    .backgroundColor(Color.BLACK)
                    .contentColor(getResources().getColor(R.color.colorAccent))
                    .buttonRippleColor(getResources().getColor(R.color.colorPrimaryDark))
                    .show()
            ;
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private static class OnYesNoResponseDialog implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<WordFragmentLayout> wordFragmentLayoutWeakReference;

        public OnYesNoResponseDialog(final WordFragmentLayout wordFragmentLayout) {
            wordFragmentLayoutWeakReference = new WeakReference<>(wordFragmentLayout);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordFragmentLayout wordFragmentLayout = wordFragmentLayoutWeakReference.get();
            if (wordFragmentLayout != null) {
                if (which == DialogAction.POSITIVE) {
                    final DialogFragment add = AddWordListDialog.newInstance("Indtast information", "Ok", "Afbryd", true);
                    add.show(wordFragmentLayout.getSupportFragmentManager(), "ADDialog");
                }
            }
        }
    }

    @Override
    public void onFinishAddWordListDialog(final String title, final String url, final boolean startDownload) {
        /* the recieved input from the dual-edittext dialog fragment */
        /* this function is only triggered if the user input was valid */
        Log.d("AddListFinished", "Title : "  + title);
        Log.d("AddListFinished", "URL   : "  + url);
        Log.d("AddListFinished", "Title : "  + startDownload);
        final Intent returnIntent = new Intent();
        returnIntent.setType("new_word_item");
        returnIntent.putExtra("result", new WordItem(title, url, startDownload));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }

    }

}
