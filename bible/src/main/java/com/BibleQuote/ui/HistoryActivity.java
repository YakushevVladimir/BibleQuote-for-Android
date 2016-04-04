/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.base.BibleQuoteActivity;

import java.util.LinkedList;

public class HistoryActivity extends BibleQuoteActivity {

    //private final String TAG = "HistoryActivity";

    private ListView vHistoryList;
    private Librarian myLibrarian;
    private LinkedList<ItemList> list = new LinkedList<>();
    private AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("linkOSIS", list.get(position).get(ItemList.ID));
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorits);

        BibleQuoteApp app = (BibleQuoteApp) getApplication();
        myLibrarian = app.getLibrarian();

        setListAdapter();
        vHistoryList.setOnItemClickListener(OnItemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_bar_history_clear:
                myLibrarian.clearHistory();
                setListAdapter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListAdapter() {
        list = myLibrarian.getHistoryList();
        vHistoryList = (ListView) findViewById(R.id.FavoritsLV);
        vHistoryList.setAdapter(new SimpleAdapter(this, list,
                R.layout.item_list_no_id,
                new String[]{ItemList.ID, ItemList.Name}, new int[]{
                R.id.id, R.id.name}));
    }

}
