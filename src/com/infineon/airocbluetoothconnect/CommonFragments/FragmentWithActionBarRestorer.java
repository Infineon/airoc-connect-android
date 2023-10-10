package com.infineon.airocbluetoothconnect.CommonFragments;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

/**
 * Fragment with store/restore ActionBar capabilities
 */
public class FragmentWithActionBarRestorer extends Fragment {
    private CharSequence mTitle;
    private ActionBar mActionBar;

    /**
     * Store current actionbar text
     *
     * @param actionBar source ActionBar
     */
    public void storeActionBar(ActionBar actionBar) {
        if (actionBar != null) {
            mActionBar = actionBar;
            mTitle = actionBar.getTitle();
        }
    }

    /**
     * Restore stored action bar
     */
    public void restoreActionBar() {
        if (mActionBar != null && mTitle != null) {
            mActionBar.setTitle(mTitle);
        }
    }
}
