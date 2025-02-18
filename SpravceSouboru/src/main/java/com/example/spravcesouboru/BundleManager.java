package com.example.spravcesouboru;

import java.util.Locale;
import java.util.Observable;

/**
 * Trida BundleManager slouží k poznamenání změny jazyka
 *
 * @author  Michal Vojtuš
 * @version 1.1
 */
public class BundleManager extends Observable {
    private Locale currentLocale;

    public BundleManager(Locale locale) {
        this.currentLocale = locale;
    }

    public void changeAndNotify(Locale locale) {
        this.currentLocale = locale;
        this.setChanged();
        this.notifyObservers(currentLocale);
    }
}
