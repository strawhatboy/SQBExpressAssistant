package com.sqbnet.expressassistant;

import android.support.v4.app.Fragment;

/**
 * Created by virgil on 7/7/15.
 */
public abstract class BaseFragment extends Fragment {
    protected  boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            isVisible = true;
        }else {
            isVisible = false;
        }
    }
}
