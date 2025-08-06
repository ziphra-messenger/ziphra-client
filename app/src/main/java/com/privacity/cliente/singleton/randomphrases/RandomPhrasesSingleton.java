package com.privacity.cliente.singleton.randomphrases;

import android.content.res.Resources;
import android.util.Log;

import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.SingletonReset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPhrasesSingleton implements SingletonReset {

    private static final String TAG = "RandomPhrasesSingleton";

    private static final String CONSTANT__RANDOM_PHRASE__PREFIX = "random_phrases_";
    private static final String CONSTANT__DEF_TYPE = "string";
    public static final int CONSTANT__RANDOM_PHRASES__QUANTITY = 81;

    private static RandomPhrasesSingleton instance;

    private List<Integer> lista = new ArrayList<Integer>();
    private int lastPhrase = -1;

    private RandomPhrasesSingleton() {
        Log.d(TAG,"construct");
    }

    public static RandomPhrasesSingleton getInstance() {
        if (instance == null) {
            instance = new RandomPhrasesSingleton();
        }
        return instance;
    }


    @Override
    public void reset() {
        Log.d(TAG,"reset");
        lista = null;
        instance = null;
    }

    public String getPhrase() {
        if (lista.size() == 0) {
            Log.d(TAG,"lista.size() == 0");
            lista = getPhrases();
        }

        Log.d(TAG,"lista.size(): " + lista.size());

        Integer actual = lista.remove(0);

        if (lastPhrase == actual.intValue()) {
            Log.d(TAG,"lastPhrase == actual. " + lastPhrase);
            return getPhrase();
        } else {
            lastPhrase = actual;
            return getPhraseText(actual);
        }
    }

    private String getPhraseText(int actual) {

        Log.d(TAG,"getPhraseText actual " + actual);

        Resources res = SingletonCurrentActivity.getInstance().get().getResources();
        String r = res.getString(res.getIdentifier(CONSTANT__RANDOM_PHRASE__PREFIX + actual
                , CONSTANT__DEF_TYPE, SingletonCurrentActivity.getInstance().get().getPackageName()));

        if ( SingletonServer.getInstance().isDeveloper()) {
            r = actual + " - " + r;
        }
        return r;
    }

    public List<Integer> getPhrases() {

        ArrayList<Integer> l = new ArrayList<Integer>();


        for (int i = 1; i <= CONSTANT__RANDOM_PHRASES__QUANTITY ; i++) {
            l.add(i);
        }

        Collections.shuffle(l);

        return l;

    }
}
