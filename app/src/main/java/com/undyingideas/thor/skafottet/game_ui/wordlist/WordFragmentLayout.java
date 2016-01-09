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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.dialogs.YesNo;
import com.undyingideas.thor.skafottet.game_ui.wordlist.data.WordItem;

/**
 * WordFragmentLayout class.
 * @author rudz
 */
public class WordFragmentLayout extends AppCompatActivity implements YesNo.YesNoResultListener, AddWordListDialog.AddWordListListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordlist_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

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
            final DialogFragment yn = YesNo.newInstance("Tilføj", "Vil du tilføje en ordliste?", getString(R.string.dialog_yes), getString(R.string.dialog_no));
            yn.show(getSupportFragmentManager(), "YNDialog");
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
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onDone(final boolean result) {
        if (result) {
            /* generate edittext dialog and show it */
            final DialogFragment add = AddWordListDialog.newInstance("Indtast information", "Ok", "Afbryd", true);
            add.show(getSupportFragmentManager(), "ADDialog");
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
}
