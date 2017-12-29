package com.shuishou.retailerinventory.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.R;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/12/25.
 */

public class RecyclerGoodsItemAdapter extends RecyclerView.Adapter<RecyclerGoodsItemAdapter.ViewHolder> {
    private ChangeAmountClickListener clickListener = new ChangeAmountClickListener();
    private final int resourceId;
    private final ArrayList<Goods> goods;
    private final MainActivity mainActivity;
    static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView txtName;
        final TextView txtLeftAmount;
        final TextView txtUnit;
        final Button btnChange;
        public ViewHolder(View view){
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_goodsname);
            txtLeftAmount = (TextView) view.findViewById(R.id.txt_leftamount);
            txtUnit = (TextView) view.findViewById(R.id.txt_unit);
            btnChange = (Button) view.findViewById(R.id.btn_change);
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
        holder.txtUnit.setText(g.getName());
        holder.txtLeftAmount.setText(String.valueOf(g.getLeftAmount()));
        holder.txtName.setText(g.getName());
        holder.btnChange.setTag(g);
        holder.btnChange.setOnClickListener(clickListener);
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
}
