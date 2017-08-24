package com.tangledwebgames.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public static Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };

    private static final String AUTHOR_CONNECTOR = ", ";

    String title;
    String[] authors;
    String publishDate;

    Book(String title, String[] authors, String publishDate) {
        this.title = title;
        this.authors = authors;
        this.publishDate = publishDate;
    }

    private Book(Parcel parcel) {
        this.title = parcel.readString();
        parcel.readStringArray(this.authors);
        this.publishDate = parcel.readString();
    }

    String getAuthorString() {
        String authorString = "";
        if (authors == null || authors.length == 0) {
            return authorString;
        }
        authorString = authors[0];
        for (int i = 1; i < authors.length; i++) {
            authorString += AUTHOR_CONNECTOR + authors[i];
        }
        return authorString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeStringArray(authors);
        parcel.writeString(publishDate);
    }

}
