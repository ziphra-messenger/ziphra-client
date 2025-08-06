package com.privacity.cliente.activity.loading;

import android.os.AsyncTask;

public class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {

    private final LoadingAsyncTaskActionInterface action;
    @Override
    protected Void doInBackground(Void... voids) {
        action.execute();

        return null;
    }

    public LoadingAsyncTask(LoadingAsyncTaskActionInterface action) {
        this.action = action;

    }
}
