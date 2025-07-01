package com.privacity.cliente.singleton.randomphrases;

import android.content.res.Resources;

import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.common.SingletonReset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPhrasesSingleton implements SingletonReset {

    private static final String CONSTANT__RANDOM_PHRASE__PREFIX = "random_phrases_";

    private static RandomPhrasesSingleton instance;

    private List<Integer> lista = new ArrayList<Integer>();
    private int lastPhrase = -1;

    private RandomPhrasesSingleton() {
    }

    public static RandomPhrasesSingleton getInstance() {
        if (instance == null) {
            instance = new RandomPhrasesSingleton();
        }
        return instance;
    }


    @Override
    public void reset() {
        lista = null;
        instance = null;
    }

    public String getPhrase() {
        if (lista.size() == 0) {
            lista = getPhrases();
        }

        Integer actual = lista.remove(0);

        if (lastPhrase == actual.intValue()) {
            return getPhrase();
        } else {
            lastPhrase = actual;
            return getPhraseText(actual);
        }
    }

    private String getPhraseText(int actual) {
        Resources res = SingletonCurrentActivity.getInstance().get().getResources();
        return res.getString(res.getIdentifier(CONSTANT__RANDOM_PHRASE__PREFIX + actual
                , "string", SingletonCurrentActivity.getInstance().get().getPackageName()));
    }

    public List<Integer> getPhrases() {

        ArrayList<Integer> l = new ArrayList<Integer>();


        for (int i = 0; i <= 80; i++) {
            l.add(i);
        }

        Collections.shuffle(l);

        return l;

    }
}
