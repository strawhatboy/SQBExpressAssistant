package com.sqbnet.expressassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sqbnet.expressassistant.service.LocalService;

/**
 * Created by Andy on 6/30/2015.
 *
 * this fragment is in the main activity's viewpager
 */
public class orderDoneFragment extends OrderBaseFragment {

    public static String Remuneration;

    public  IGetOrder IGetOrder;

    public interface IGetOrder{
        void run();
    }

    private TextView tv_done_balance;
    private Button btn_go;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_done_fragment, container, false);
        initView(view);
        return view;
    }

    void initView(View view) {
        tv_done_balance = (TextView) view.findViewById(R.id.tv_order_done_balance);
        btn_go = (Button) view.findViewById(R.id.btn_order_done);

        // stop gps
        if (getActivity() != null) {
            getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), LocalService.class));
        }
        if(Remuneration != null){
            tv_done_balance.setText(Remuneration);
        }

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IGetOrder !=null){
                    IGetOrder.run();
                }
            }
        });
    }

    @Override
    void loadData() {

    }
}
