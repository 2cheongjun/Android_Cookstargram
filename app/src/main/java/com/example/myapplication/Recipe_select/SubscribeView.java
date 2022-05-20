package com.example.myapplication.Recipe_select;

import java.util.List;

public interface SubscribeView {

    void onGetResult(List<Note> notes);
    void onErrorLoading(String message);
}


