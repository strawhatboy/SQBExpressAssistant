package com.sqbnet.expressassistant;

import org.json.JSONObject;

/**
 * Created by virgil on 7/5/15.
 */
public abstract class OrderBaseFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    protected orderMainActivity.IWizardPageDelegate delegate;
    protected String mUserId;
    protected String mOrderId;
    protected String mStatus;

    protected JSONObject mJSONData;

    @Override
    public void setNextDelegate(orderMainActivity.IWizardPageDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setParameters(String user_id, String order_id, String status) {
        mUserId = user_id;
        mOrderId = order_id;
        mStatus = status;
    }

    abstract void loadData();
}
