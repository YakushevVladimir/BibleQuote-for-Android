package com.BibleQuote.ui.handlers;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.ui.ReaderActivity;
import com.BibleQuote.ui.dialogs.BookmarksDialog;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.share.ShareBuilder;

import java.util.TreeSet;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 01.2016
 */
public final class SelectTextHandler implements ActionMode.Callback {

    private static final String VIEW_REFERENCE = "org.scripturesoftware.intent.action.VIEW_REFERENCE";

    private ReaderActivity readerActivity;
    private ReaderWebView webView;

    public SelectTextHandler(ReaderActivity readerActivity, ReaderWebView webView) {
        this.readerActivity = readerActivity;
        this.webView = webView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        readerActivity.getMenuInflater().inflate(R.menu.menu_action_text_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        TreeSet<Integer> selVerses = webView.getSelectedVerses();
        if (selVerses.size() == 0) {
            return true;
        }

        Librarian myLibrarian = BibleQuoteApp.getInstance().getLibrarian();

        switch (item.getItemId()) {
            case R.id.action_bookmarks:
                myLibrarian.setCurrentVerseNumber(selVerses.first());
                DialogFragment bmDial = BookmarksDialog.newInstance(new Bookmark(myLibrarian.getCurrentOSISLink()));
                bmDial.show(readerActivity.getSupportFragmentManager(), "bookmark");
                break;

            case R.id.action_share:
                myLibrarian.shareText(readerActivity, selVerses, ShareBuilder.Destination.ActionSend);
                break;

            case R.id.action_copy:
                myLibrarian.shareText(readerActivity, selVerses, ShareBuilder.Destination.Clipboard);
                Toast.makeText(readerActivity, readerActivity.getString(R.string.added), Toast.LENGTH_LONG).show();
                break;

            case R.id.action_references:
                myLibrarian.setCurrentVerseNumber(selVerses.first());
                Intent intParallels = new Intent(VIEW_REFERENCE);
                intParallels.putExtra("linkOSIS", myLibrarian.getCurrentOSISLink().getPath());
                readerActivity.startActivityForResult(intParallels, ReaderActivity.ID_PARALLELS);
                break;

            default:
                return false;
        }

        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        webView.clearSelectedVerse();
        //currActionMode = null;
    }
}
