package com.azweb.scheellarsen.fragments;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Thaind on 12/10/2015.
 */
public class BaseFragment extends Fragment {

    public void saveStateToArguments(Bundle savedState, String keyState) {
        Bundle b = getArguments();
        if (b != null) {
            b.putBundle(keyState, savedState);
        }
    }

    public Bundle restoreStateFromArguments(String keyState) {
        Bundle b = getArguments();
        if (b != null) {
            Bundle savedState = b.getBundle(keyState);
            return savedState;
        }
        return null;
    }

}
