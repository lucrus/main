package com.lucrus.main.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lucrus.main.R;
import com.lucrus.main.Utility;

import java.util.ArrayList;

/**
 * Created by lucrus on 22/11/14.
 */
public class PopupListActivity extends ListActivity {
    public static final String ITEMS_NAME = "com.lucrus.main.items";
    private String mSel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_PopupTheme);

        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        final WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        mSel = null;
        Point size = Utility.getDisplaySize(this);


        int width = (int) Math.floor(size.x * 0.7);
        int height = (int) Math.floor(size.y * 0.7);

        // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
        this.getWindow().setLayout(width, height);

        setContentView(R.layout.activity_popup_list);
        super.onCreate(savedInstanceState);

        ArrayList<ListItem> items = getIntent().getParcelableArrayListExtra(ITEMS_NAME);

        if (items != null) {
            ArrayAdapter adp = new ArrayAdapter<ListItem>(this, android.R.layout.simple_list_item_1, items);

            setListAdapter(adp);
        }

//        int ix = getIntent().getIntExtra("sel", -1);
//        if(ix>=0 && ix<items.size()){
//            String s = items.get(ix);
//            mSel = s;
//            getListView().setSelection(ix);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getListView().getChildAt(0).setBackgroundColor(Color.parseColor("#8000A8CA"));
//                }
//            }, 500);
//        }


        int title = getIntent().getIntExtra("title", 0);
        if (title > 0) {
            ((TextView) findViewById(R.id.tvTitle)).setText(title);
        }


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        mSel = l.getAdapter().getItem(position).toString();
//        for(int i=0;i<getListView().getChildCount();i++){
//            getListView().getChildAt(i).setBackgroundColor(Color.WHITE);
//        }
//        //getListView().setSelection(position);
//        v.setBackgroundColor(Color.parseColor("#8000A8CA"));
        Intent intent = new Intent();
        intent.putExtra("data", (ListItem) l.getAdapter().getItem(position));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void cancel(View v) {
        onBackPressed();
    }

    public void ok(View v) {
        if (mSel != null) {
            Intent intent = new Intent();
            intent.putExtra("data", mSel);
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    public static class ListItem implements Parcelable {
        private String id;
        private String value;

        public ListItem() {
            super();
        }

        public ListItem(String id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public ListItem(Parcel in) {
            super();
            id = in.readString();
            value = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(value);
        }

        public static final Creator CREATOR = new Creator() {
            public ListItem createFromParcel(Parcel in) {
                return new ListItem(in);
            }

            public ListItem[] newArray(int size) {
                return new ListItem[size];
            }
        };

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}


