package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.CustomConstants;

/**
 * Created by Andy on 6/30/2015.
 */
public class orderFinishFragment extends OrderBaseFragment {

    public static String Remuneration;

    EditText et_delivery_code;
    Button btn_confirm;
    Button btn_revalid;
    RelativeLayout valideLayout;
    RelativeLayout valideLayoutAgain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_finish_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_confirm = (Button) view.findViewById(R.id.btn_order_finish_confirm);
        btn_revalid = (Button) view.findViewById(R.id.btn_order_re_valide);
        valideLayout = (RelativeLayout) view.findViewById(R.id.rl_order_input_valid);
        valideLayoutAgain = (RelativeLayout) view.findViewById(R.id.rl_order_input_valid_again);
        et_delivery_code = (EditText) view.findViewById(R.id.et_order_finish_delivery_code);

        et_delivery_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                btn_confirm.setEnabled(!content.equals(""));
            }
        });

        btn_revalid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valideLayout.setVisibility(View.VISIBLE);
                valideLayoutAgain.setVisibility(View.GONE);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    String code = et_delivery_code.getText().toString();
                    SQBProvider.getInst().verifyCode(mOrderId, code, new SQBResponseListener() {
                        @Override
                        public void onResponse(SQBResponse response) {
                            if (response == null) {
                                return;
                            }
                            Log.i("virgil", "order confirm");
                            Log.i("virgil", response.getCode());
                            Log.i("virgil", response.getMsg());
                            Log.i("virgil", response.getData().toString());

                            //delegate.exit(ResultCode.ORDER_DONE, /* balance got */ Remuneration);

                            if (response.getCode().equals("1000")) {
                                // add rest to server to confirm the order
                                SQBProvider.getInst().updateOrderStatus(mOrderId, CustomConstants.ORDER_ARRIVED, new SQBResponseListener() {
                                    @Override
                                    public void onResponse(final SQBResponse response) {
                                        if (response == null) {
                                            return;
                                        }
                                        Log.i("virgil", response.getCode());
                                        Log.i("virgil", response.getMsg());
                                        Log.i("virgil", response.getData().toString());
                                        if (response.getCode().equals("1000")) {
                                            delegate.exit(ResultCode.ORDER_DONE, /* balance got */ Remuneration);
                                        }
                                    }
                                });
                            }else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        valideLayoutAgain.setVisibility(View.VISIBLE);
                                        valideLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    void loadData() {

    }
}
