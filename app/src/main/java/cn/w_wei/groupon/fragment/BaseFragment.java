package cn.w_wei.groupon.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.ui.MainActivity;

public abstract class BaseFragment extends Fragment {
    public View contentView;
    //一下代码也可提取到所属activity中
    public View skip(){
        Button btTurn = contentView.findViewById(R.id.bt_turn);
        btTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return contentView;
    }
}
