package com.tangledwebgames.booklisting;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    static final int BOOK_LOADER_ID = 0;

    String searchTerm;

    public BookLoader(Context context, String searchTerm) {
        super(context);
        this.searchTerm = searchTerm;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        return TextUtils.isEmpty(searchTerm) ? null : BookQueryUtil.getBookData(searchTerm);
    }
}
