package com.wenbchen.android.imdb.activities;

import android.util.Log;

import com.wenbchen.android.imdb.R;
import com.wenbchen.android.imdb.util.HttpUrlBuilder;
import com.wenbchen.android.imdb.util.UtilsString;

import java.util.LinkedHashMap;

import static com.wenbchen.android.imdb.util.UtilsString.API_KEY;

public class TVListViewActivity extends BaseListViewActivity {
    // Log tag
    private static final String TAG = "TVListViewActivity";

    @Override
    protected void setUpPageTitle() {
        setTitle(getResources().getString(R.string.tv_list));
    }

    @Override
    protected StringBuffer buildSearchRequest(String title, String year) {
        StringBuffer mUrlStringBuffer = new StringBuffer();
        mUrlStringBuffer.append(UtilsString.BASE_URL);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("keymap", API_KEY);
        map.put("s", title);
        map.put("y", year);
        map.put("type", "series");
        HttpUrlBuilder builder = new HttpUrlBuilder();
        mUrlStringBuffer.append("?");
        mUrlStringBuffer.append(builder.getUrlEncodeUTF8(map));
        Log.i(TAG, "Url is: " + mUrlStringBuffer.toString());
        return mUrlStringBuffer;
    }
}