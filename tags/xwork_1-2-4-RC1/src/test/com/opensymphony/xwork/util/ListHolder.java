package com.opensymphony.xwork.util;

import java.util.List;

/**
 * User: patrick Date: Dec 20, 2005 Time: 11:15:29 AM
 */
public class ListHolder {
    List longs;
    List strings;
    List dates;

    public List getLongs() {
        return longs;
    }

    public void setLongs(List longs) {
        this.longs = longs;
    }

    public List getStrings() {
        return strings;
    }

    public void setStrings(List strings) {
        this.strings = strings;
    }

    public List getDates() {
        return dates;
    }

    public void setDates(List dates) {
        this.dates = dates;
    }
}
