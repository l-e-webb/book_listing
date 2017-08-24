package com.tangledwebgames.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            item = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        Context context = getContext();

        int color;
        if ((position % 2) == 0) {
            color = context.getResources().getColor(R.color.item_background_dark);
        } else {
            color = context.getResources().getColor(R.color.item_background_light);
        }
        item.setBackgroundColor(color);

        Book book = getItem(position);

        if (book != null) {
            String title = !TextUtils.isEmpty(book.title) ?
                    book.title : context.getString(R.string.no_title);
            String publishDate = !TextUtils.isEmpty(book.publishDate) ?
                    book.publishDate : context.getString(R.string.no_publish_date);
            String authors = book.getAuthorString();
            if (authors.length() == 0) authors = context.getString(R.string.no_author);
            ((TextView) item.findViewById(R.id.title)).setText(title);
            ((TextView) item.findViewById(R.id.authors)).setText(authors);
            ((TextView) item.findViewById(R.id.date)).setText(publishDate);
        }

        return item;
    }
}
