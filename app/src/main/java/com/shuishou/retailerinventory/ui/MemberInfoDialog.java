package com.shuishou.retailerinventory.ui;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.retailerinventory.InstantValue;
import com.shuishou.retailerinventory.R;
import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.bean.Member;


/**
 * Created by Administrator on 2017/7/21.
 */

class MemberInfoDialog implements View.OnClickListener{
    private TextView txtMemberName;
    private TextView txtMemberCard;
    private TextView txtTelephone;
    private TextView txtDiscountRate;
    private TextView txtJoinDate;
    private TextView txtScore;
    private TextView txtBalance;
    private EditText txtLookforWord;
    private ImageButton btnLookfor;
    private MainActivity mainActivity;

    private AlertDialog dlg;

    public MemberInfoDialog(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initUI();
    }

    private void initUI(){
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.memberinfo_layout, null);
        txtMemberName = (TextView) view.findViewById(R.id.txtMemberName);
        txtMemberCard = (TextView) view.findViewById(R.id.txtMemberCard);
        txtTelephone = (TextView) view.findViewById(R.id.txtTelephone);
        txtDiscountRate = (TextView) view.findViewById(R.id.txtMemberDiscount);
        txtJoinDate = (TextView) view.findViewById(R.id.txtJoinDate);
        txtScore = (TextView) view.findViewById(R.id.txtMemberScore);
        txtBalance = (TextView) view.findViewById(R.id.txtMemberBalance);
        btnLookfor = (ImageButton) view.findViewById(R.id.btnLookfor);
        txtLookforWord = (EditText) view.findViewById(R.id.txtSearchCode);
        btnLookfor.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setNegativeButton("Close", null)
                .setView(view);
        dlg = builder.create();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
    }

    public void showDialog(){
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dlg.show();
    }

    public void dismiss(){
        dlg.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == btnLookfor) {
            txtMemberName.setText(null);
            txtMemberCard.setText(null);
            txtTelephone.setText(null);
            txtDiscountRate.setText(null);
            txtJoinDate.setText(null);
            txtScore.setText(null);
            txtBalance.setText(null);
            if (txtLookforWord.getText() != null && txtLookforWord.getText().length() > 0){
                Member m = mainActivity.lookforMember(txtLookforWord.getText().toString());
                if (m != null){
                    txtMemberName.setText(m.getName());
                    txtMemberCard.setText(m.getMemberCard());
                    txtTelephone.setText(m.getTelephone());
                    txtDiscountRate.setText(String.valueOf(m.getDiscountRate()));
                    txtJoinDate.setText(InstantValue.DFYMD.format(m.getCreateTime()));
                    txtScore.setText(String.valueOf(m.getScore()));
                    txtBalance.setText(String.valueOf(m.getBalanceMoney()));
                } else {
                    Toast.makeText(mainActivity, "cannot find member by " + txtLookforWord.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
