package com.shuishou.retailerinventory.ui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.retailerinventory.bean.Category1;
import com.shuishou.retailerinventory.bean.Category2;
import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.R;

import java.util.ArrayList;

public class QuickSearchActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String INTENTDATA_GOODS = "GOODS";
    public static final String INTENTDATA_ACTION = "ACTION";
    public static final int INTENTDATA_ACTION_CHANGE = 1;
    public static final int INTENTDATA_ACTION_IMPORT = 2;
    private EditText txtSearchCode;
//    private ArrayList<View> resultCellList = new ArrayList<>(8);
    private View searchResultCell;
    private ImageButton btnChange;
    private ImageButton btnImport;
    private TextView txtGoodsName;
    private TextView txtLeftAmount;
    private TextView txtPurchasePrice;
    private TextView txtSellPrice;
    private TextView txtMemberPrice;
    private AlertDialog dlg;

    private QuickSearchActivity.ChooseGoodsListener listener = new QuickSearchActivity.ChooseGoodsListener();
    private ArrayList<Goods> allGoods;
    private ArrayList<Category1> category1s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quicksearch_activity_layout);
        category1s = (ArrayList<Category1>) getIntent().getExtras().getSerializable(MainActivity.INTENTEXTRA_CATEGORYLIST);
        txtSearchCode = (EditText) findViewById(R.id.txtSearchCode);
        searchResultCell = findViewById(R.id.quicksearchresultcell);
        btnChange = (ImageButton) searchResultCell.findViewById(R.id.btn_change);
        btnImport = (ImageButton) searchResultCell.findViewById(R.id.btn_import);
        txtGoodsName = (TextView) searchResultCell.findViewById(R.id.txtGoodsName);
        txtLeftAmount = (TextView) searchResultCell.findViewById(R.id.txtLeftAmount);
//        txtPurchasePrice = (TextView) searchResultCell.findViewById(R.id.txtPurchasePrice);
        txtSellPrice = (TextView) searchResultCell.findViewById(R.id.txtSellPrice);
        txtMemberPrice = (TextView) searchResultCell.findViewById(R.id.txtMemberPrice);

        btnChange.setOnClickListener(this);
        btnImport.setOnClickListener(this);

        txtSearchCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                refreshResult();
                return true;
            }
        });
    }

    private Goods findGoods(String txt){
        if (category1s != null){
            for (Category1 c1 : category1s){
                if (c1.getCategory2s() != null){
                    for(Category2 c2 : c1.getCategory2s()){
                        if (c2.getGoods() != null){
                            for(Goods g : c2.getGoods()){
                                if (g.getBarcode().equals(txt)
                                        || g.getName().toLowerCase().equals(txt.toLowerCase())){
                                    return g;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    private void refreshResult(){
        String code = txtSearchCode.getText().toString();
        Goods findGoods = findGoods(code);

        if (findGoods == null) {
            Toast.makeText(this, "cannot find result", Toast.LENGTH_LONG).show();
            return;
        }
        btnChange.setTag(findGoods);
        btnImport.setTag(findGoods);
        txtGoodsName.setText(findGoods.getName());
        txtLeftAmount.setText(String.valueOf(findGoods.getLeftAmount()));
        txtPurchasePrice.setText(String.valueOf(findGoods.getBuyPrice()));
        txtSellPrice.setText(String.valueOf(findGoods.getSellPrice()));
        txtMemberPrice.setText(String.valueOf(findGoods.getMemberPrice()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnChange){
            Goods g = (Goods)v.getTag();
            if (g == null) return;
            Intent intent = new Intent();
            intent.putExtra(INTENTDATA_GOODS, g);
            intent.putExtra(INTENTDATA_ACTION, INTENTDATA_ACTION_CHANGE);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v == btnImport){
            Goods g = (Goods)v.getTag();
            if (g == null) return;
            Intent intent = new Intent();
            intent.putExtra(INTENTDATA_GOODS, g);
            intent.putExtra(INTENTDATA_ACTION, INTENTDATA_ACTION_IMPORT);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    class ChooseGoodsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag().getClass().getName().equals(Goods.class.getName())){
                Goods g = (Goods)v.getTag();
                Intent intent = new Intent();
                intent.putExtra(INTENTDATA_GOODS, g);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
