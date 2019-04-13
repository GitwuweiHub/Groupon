package cn.w_wei.groupon.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.w_wei.groupon.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFour extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView= inflater.inflate(R.layout.fragment_guide_four, container, false);

        return skip();
    }

}
