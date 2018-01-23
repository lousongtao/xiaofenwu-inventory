package com.shuishou.retailerinventory.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.shuishou.retailerinventory.bean.HttpResult;
import com.shuishou.retailerinventory.utils.CommonTool;


/**
 * Created by Administrator on 2017/7/21.
 */

class GoodsInfoDialog implements View.OnClickListener{
    private TextView txtLeftAmount;
    private TextView txtMemberPrice;
    private TextView txtSellPrice;
    private TextView txtGoodsName;
    private ImageButton btnChange;
    private ImageButton btnImport;
    private MainActivity mainActivity;

    private AlertDialog dlg;
    private Goods goods;

    private final static int MESSAGEWHAT_NOTIFYLISTCHANGE=1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dealHandlerMessage(msg);
            super.handleMessage(msg);
        }
    };

    private void dealHandlerMessage(Message msg){
        switch (msg.what){
            case MESSAGEWHAT_NOTIFYLISTCHANGE :
                mainActivity.notifyGoodsPropertyChange(goods);
                break;
        }
    }

    public GoodsInfoDialog(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initUI();
    }

    private void initUI(){
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.goodsinfo_layout, null);
        txtLeftAmount = (TextView) view.findViewById(R.id.txtLeftAmount);
        txtMemberPrice = (TextView) view.findViewById(R.id.txtMemberPrice);
        txtSellPrice = (TextView) view.findViewById(R.id.txtSellPrice);
        txtGoodsName = (TextView) view.findViewById(R.id.txtGoodsName);
        btnChange = (ImageButton) view.findViewById(R.id.btn_change);
        btnImport = (ImageButton) view.findViewById(R.id.btn_import);
        btnChange.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setNegativeButton("Close", null)
                .setView(view);
        dlg = builder.create();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
    }

    public void showDialog(Goods g){
        this.goods = g;
        txtLeftAmount.setText(g.getLeftAmount()+"");
        txtSellPrice.setText(g.getSellPrice() + "");
        txtMemberPrice.setText(g.getMemberPrice() +"");
        txtGoodsName.setText(g.getName());
        btnChange.setTag(g);
        btnImport.setTag(g);
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dlg.show();
    }

    public void dismiss(){
        dlg.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == btnChange){
            Goods g = (Goods)v.getTag();
            if (g == null) return;
            dismiss();
            mainActivity.getSaveNewAmountDialog().showDialog(g);
        } else if (v == btnImport){
            Goods g = (Goods)v.getTag();
            if (g == null) return;
            dismiss();
            mainActivity.getImportAmountDialog().showDialog(g);
        }
    }
}
