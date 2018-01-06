package com.shuishou.retailerinventory.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.R;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/12/25.
 */

public class RecyclerGoodsItemAdapter extends RecyclerView.Adapter<RecyclerGoodsItemAdapter.ViewHolder> {
    private ChangeAmountClickListener changeAmountClickListener = new ChangeAmountClickListener();
    private ImportAmountClickListener importAmountClickListener = new ImportAmountClickListener();
    private final int resourceId;
    private final ArrayList<Goods> goods;
    private final MainActivity mainActivity;
    static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView txtName;
        final TextView txtLeftAmount;
//        final TextView txtPurchasePrice;
        final TextView txtMemberPrice;
        final TextView txtSellPrice;
        final ImageButton btnChange;
        final ImageButton btnImport;
        final TextView txtDescription;
        public ViewHolder(View view){
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtGoodsName);
            txtLeftAmount = (TextView) view.findViewById(R.id.txtLeftAmount);
//            txtPurchasePrice = (TextView) view.findViewById(R.id.txtPurchasePrice);
            txtMemberPrice = (TextView) view.findViewById(R.id.txtMemberPrice);
            txtSellPrice = (TextView) view.findViewById(R.id.txtSellPrice);
            btnChange = (ImageButton) view.findViewById(R.id.btn_change);
            btnImport = (ImageButton) view.findViewById(R.id.btn_import);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        }
    }

    public RecyclerGoodsItemAdapter(MainActivity mainActivity, int resourceId, ArrayList<Goods> objects){
        goods = objects;
        this.resourceId = resourceId;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        Goods g = goods.get(position);
        holder.txtMemberPrice.setText(String.valueOf(g.getMemberPrice()));
//        holder.txtPurchasePrice.setText(String.valueOf(g.getBuyPrice()));
        holder.txtSellPrice.setText(String.valueOf(g.getSellPrice()));
        holder.txtLeftAmount.setText(String.valueOf(g.getLeftAmount()));
        holder.txtName.setText(g.getName());
        holder.txtDescription.setText(g.getDescription());
        holder.btnChange.setTag(g);
        holder.btnChange.setOnClickListener(changeAmountClickListener);
        holder.btnImport.setTag(g);
        holder.btnImport.setOnClickListener(importAmountClickListener);
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    class ChangeAmountClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mainActivity.getSaveNewAmountDialog().showDialog((Goods)v.getTag());
        }
    }

    class ImportAmountClickListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            mainActivity.getImportAmountDialog().showDialog((Goods)v.getTag());
        }
    }
}
