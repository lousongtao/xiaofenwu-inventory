package com.shuishou.retailerinventory.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.retailerinventory.bean.Category1;
import com.shuishou.retailerinventory.bean.Category2;
import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.R;

import java.util.ArrayList;

public class QuickSearchActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String INTENTDATA_GOODS = "GOODS";
    private EditText txtSearchCode;
//    private ArrayList<View> resultCellList = new ArrayList<>(8);
    private View searchResultCell;
    private Button btnImport;
    private Button btnChange;
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
        btnChange = searchResultCell.findViewById(R.id.btn_change);
        btnImport = searchResultCell.findViewById(R.id.btn_import);
        btnChange.setOnClickListener(this);


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
                                        || g.getName().equals(txt)){
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
        Goods findResult = findGoods(code);

        if (findResult == null) {
            Toast.makeText(this, "cannot find result", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i = 0; i< results.size(); i++){
            Goods g= results.get(i);
            View v = resultCellList.get(i);
            v.setVisibility(View.VISIBLE);
            Button btn = (Button)v.findViewById(R.id.btn_change);
            btn.setTag(g);
            btn.setOnClickListener(listener);
            TextView txtName = (TextView)v.findViewById(R.id.txt_goodsname);
            TextView txtLeftAmouont = (TextView)v.findViewById(R.id.txt_leftamount);
            TextView txtUnit = (TextView)v.findViewById(R.id.txt_unit);
            txtName.setText(g.getName());
            txtLeftAmouont.setText(String.valueOf(g.getLeftAmount()));
//            TextView tvName = (TextView)v.findViewById(R.id.txtName);
//            ImageButton chooseButton = (ImageButton) v.findViewById(R.id.chooseBtn);
//            chooseButton.setTag(m);
//            tvName.setText(m.getName());
        }
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

        } else if (v == btnImport){

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
