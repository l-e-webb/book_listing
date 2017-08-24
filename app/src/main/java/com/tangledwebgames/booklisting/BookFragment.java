package com.tangledwebgames.booklisting;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = BookFragment.class.getSimpleName();

    private static final String BOOK_ARRAY_KEY = "books";
    private static final String ITEM_POSITION_KEY = "item_position";

    ArrayAdapter<Book> bookAdapter;

    TextView emptyListTextView;
    View loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        Context context = getActivity();
        ListView listView = layout.findViewById(R.id.book_list_view);
        bookAdapter = new BookAdapter(context, getBookArray(savedInstanceState));
        listView.setAdapter(bookAdapter);
        if (savedInstanceState != null) {
            listView.setSelection(savedInstanceState.getInt(ITEM_POSITION_KEY));
        }
        emptyListTextView = layout.findViewById(R.id.empty_list_text_view);
        listView.setEmptyView(emptyListTextView);
        loadingIndicator = layout.findViewById(R.id.loading_indicator);
        layout.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSearch();
            }
        });

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            emptyListTextView.setText(R.string.no_internet_prompt);
        }

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        emptyListTextView = null;
        loadingIndicator = null;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        bookAdapter.clear();
        emptyListTextView.setText("");
        loadingIndicator.setVisibility(View.VISIBLE);
        String searchTerm = getSearchText();
        return new BookLoader(getActivity(), searchTerm);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        loadingIndicator.setVisibility(View.GONE);
        bookAdapter.clear();
        if (data == null) {
            emptyListTextView.setText(R.string.network_error);
        } else if (data.size() == 0) {
            emptyListTextView.setText(R.string.no_data_response_prompt);
        } else {
            bookAdapter.addAll(data);
            emptyListTextView.setText(R.string.no_data_search_prompt);
        }
        getLoaderManager().destroyLoader(BookLoader.BOOK_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookAdapter.clear();
        emptyListTextView.setText(R.string.no_data_search_prompt);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveBookArray(outState);
        outState.putInt(ITEM_POSITION_KEY,
                ((ListView)getActivity().findViewById(R.id.book_list_view)).getLastVisiblePosition()
        );
    }

    void submitSearch() {
        NetworkInfo networkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            emptyListTextView.setText(R.string.no_internet_prompt);
            bookAdapter.clear();
            return;
        } else {
            emptyListTextView.setText(R.string.no_data_search_prompt);
        }

        String searchTerm =  getSearchText();
        if (!TextUtils.isEmpty(searchTerm)) {
            getLoaderManager().initLoader(BookLoader.BOOK_LOADER_ID, null, this);
        }
    }

    String getSearchText() {
        return ((TextView) getActivity().findViewById(R.id.search_bar)).getText().toString();
    }

    protected List<Book> getBookArray(Bundle savedInstanceState) {
        if (savedInstanceState == null) return new ArrayList<>();
        return savedInstanceState.getParcelableArrayList(BOOK_ARRAY_KEY);
    }

    protected void saveBookArray(Bundle savedInstanceState) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < bookAdapter.getCount(); i++) {
            books.add(bookAdapter.getItem(i));
        }
        savedInstanceState.putParcelableArrayList(BOOK_ARRAY_KEY, books);
    }

}
