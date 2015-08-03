package com.sqbnet.expressassistant;

import org.json.JSONObject;

/**
 * Created by virgil on 7/5/15.
 */
public abstract class OrderBaseFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    protected orderMainActivity.IWizardPageDelegate delegate;
    protected OrderContext mOrderContext;

    protected JSONObject mJSONData;

    @Override
    public void setNextDelegate(orderMainActivity.IWizardPageDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setOrderContext(OrderContext orderContext) {
        mOrderContext = orderContext;
    }

    abstract void loadData();
}
