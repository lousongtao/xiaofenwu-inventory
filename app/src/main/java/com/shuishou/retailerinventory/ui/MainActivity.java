package com.shuishou.retailerinventory.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.retailerinventory.InstantValue;
import com.shuishou.retailerinventory.bean.Category1;
import com.shuishou.retailerinventory.bean.Category2;
import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.bean.UserData;
import com.shuishou.retailerinventory.http.HttpOperator;
import com.shuishou.retailerinventory.io.IOOperator;
import com.shuishou.retailerinventory.utils.CommonTool;
import com.shuishou.retailerinventory.R;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-12-06.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainActivity.class.getSimpleName());
    private String TAG_UPLOADERRORLOG = "uploaderrorlog";
    private String TAG_EXITSYSTEM = "exitsystem";
    private String TAG_LOOKFOR = "lookfor";
    private String TAG_SCAN = "scan";
    private final static int REQUESTCODE_SCAN = 1;
    private final static int REQUESTCODE_QUICKSEARCH = 2;
    public final static String INTENTEXTRA_CATEGORYLIST= "CATEGORYLIST";

    private UserData loginUser;
    private ArrayList<Category1> category1s = new ArrayList<>();
    private ArrayList<Goods> goods = new ArrayList<>();
    private HttpOperator httpOperator;
    private RecyclerGoodsItemAdapter goodsAdapter;
    private CategoryTabListView listViewCategorys;

    private android.support.v7.widget.RecyclerView lvGoods;
    private ImageButton btnLookfor;
    private ImageButton btnScan;
    private SaveNewAmountDialog saveNewAmountDialog;
    private ImportAmountDialog importAmountDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginUser = (UserData)getIntent().getExtras().getSerializable(LoginActivity.INTENTEXTRA_LOGINUSER);
        setContentView(R.layout.activity_main);
        listViewCategorys = (CategoryTabListView) findViewById(R.id.categorytab_listview);
        lvGoods = (android.support.v7.widget.RecyclerView)findViewById(R.id.goods_listview);
        btnLookfor = (ImageButton)findViewById(R.id.btnLookfor);
        btnScan = (ImageButton)findViewById(R.id.btnScan);
        TextView tvUploadErrorLog = (TextView)findViewById(R.id.drawermenu_uploaderrorlog);
        TextView tvExit = (TextView)findViewById(R.id.drawermenu_exit);
        //add dividers between RecyclerView items
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);
//        lvGoods.addItemDecoration(dividerItemDecoration);

        tvUploadErrorLog.setTag(TAG_UPLOADERRORLOG);
        tvExit.setTag(TAG_EXITSYSTEM);
        btnLookfor.setTag(TAG_LOOKFOR);
        btnScan.setTag(TAG_SCAN);
        tvUploadErrorLog.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        btnLookfor.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        NoHttp.initialize(this);
        Logger.setDebug(true);
        Logger.setTag("goods:nohttp");

        httpOperator = new HttpOperator(this);

        saveNewAmountDialog = new SaveNewAmountDialog(this);
        importAmountDialog = new ImportAmountDialog(this);

        startProgressDialog("wait", "Loading data...");
        httpOperator.loadData();
    }

    public void initData(ArrayList<Category1> cs){
        this.category1s = cs;

        CategoryTabAdapter categoryTabAdapter = new CategoryTabAdapter(MainActivity.this, R.layout.categorytab_listitem_layout, category1s);
        listViewCategorys.setAdapter(categoryTabAdapter);
        listViewCategorys.post(new Runnable() {
            @Override
            public void run() {
                listViewCategorys.chooseItemByPosition(0);
            }
        });

        goodsAdapter = new RecyclerGoodsItemAdapter(this, R.layout.goods_listitem_layout, goods);
        lvGoods.setLayoutManager(new LinearLayoutManager(this));
        lvGoods.setAdapter(goodsAdapter);
    }

    public Handler getProgressDlgHandler(){
        return progressDlgHandler;
    }

    public Handler getToastHandler(){
        return toastHandler;
    }

    public UserData getLoginUser() {
        return loginUser;
    }

    public ArrayList<Category1> getCategories() {
        return category1s;
    }

    public void startProgressDialog(String title, String message){
        progressDlg = ProgressDialog.show(this, title, message);
    }

    public void stopProgressDialog(){
        progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG));
    }

    public HttpOperator getHttpOperator(){
        return httpOperator;
    }

    public SaveNewAmountDialog getSaveNewAmountDialog() {
        return saveNewAmountDialog;
    }

    public ImportAmountDialog getImportAmountDialog(){
        return importAmountDialog;
    }

    public void showGoodsByCategory2(Category2 c2){
        goods.clear();
        if (c2.getGoods() != null)
            goods.addAll(c2.getGoods());
        goodsAdapter.notifyDataSetChanged();
    }

    public void notifyGoodsPropertyChange(Goods g){
        boolean isFound = false;//firstly loop the goods list, if find, notify to update UI; if not, loop all the catagories to update the data
        for (int i = 0; i< goods.size(); i++){
            if (goods.get(i).getId() == g.getId()){
                goods.get(i).setLeftAmount(g.getLeftAmount());
                goodsAdapter.notifyItemChanged(i);
                isFound = true;
                break;
            }
        }
        if (!isFound){
            for(Category1 c1 : category1s){
                if (c1.getCategory2s() != null){
                    for (Category2 c2 : c1.getCategory2s()){
                        if (c2.getGoods() != null){
                            for (Goods gd : c2.getGoods()){
                                if (gd.getId() == g.getId()) {
                                    gd.setLeftAmount(g.getLeftAmount());
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (TAG_UPLOADERRORLOG.equals(v.getTag())){
            IOOperator.onUploadErrorLog(this);
        } else if (TAG_LOOKFOR.equals(v.getTag())){
            Intent intent = new Intent(MainActivity.this, QuickSearchActivity.class);
            intent.putExtra(INTENTEXTRA_CATEGORYLIST, category1s);
            startActivityForResult(intent, REQUESTCODE_QUICKSEARCH);
        } else if (TAG_EXITSYSTEM.equals(v.getTag())){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm")
                    .setIcon(R.drawable.info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
        } else if (TAG_SCAN.equals(v.getTag())){
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivityForResult(intent, REQUESTCODE_SCAN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUESTCODE_SCAN:
                if (resultCode == RESULT_OK){
                    String code = data.getStringExtra(CameraActivity.INTENTDATA_CODE);
                    Goods goods = null;
                    for(Category1 c1 : category1s){
                        if (c1.getCategory2s() != null){
                            for (Category2 c2 : c1.getCategory2s()){
                                if (c2.getGoods() != null){
                                    for (Goods g : c2.getGoods()){
                                        if (g.getBarcode().equals(code)) {
                                            goods = g;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (goods != null){
                        saveNewAmountDialog.showDialog(goods);
                    } else {
                        getToastHandler().sendMessage(CommonTool.buildMessage(MainActivity.TOASTHANDLERWHAT_ERRORMESSAGE,
                                "Cannot find Goods by name : " + code));
                    }
                } else if (resultCode == RESULT_CANCELED){}
                break;
            case REQUESTCODE_QUICKSEARCH:
                if (resultCode == RESULT_OK){
                    Goods goods = (Goods)data.getSerializableExtra(QuickSearchActivity.INTENTDATA_GOODS);
                    int action = data.getIntExtra(QuickSearchActivity.INTENTDATA_ACTION, 0);
                    if (action == QuickSearchActivity.INTENTDATA_ACTION_CHANGE)
                        saveNewAmountDialog.showDialog(goods);
                    else if (action == QuickSearchActivity.INTENTDATA_ACTION_IMPORT)
                        importAmountDialog.showDialog(goods);
                    else
                        Toast.makeText(this, "Unrecognized Action!", Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED){}
                break;
            default:
        }
    }

    //屏蔽实体按键BACK
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static final int PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA = 3;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DOWNFINISH = 2;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    private ProgressDialog progressDlg;

    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DOWNFINISH){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            }
        }
    };

    public static final int TOASTHANDLERWHAT_ERRORMESSAGE = 0;
    private Handler toastHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOASTHANDLERWHAT_ERRORMESSAGE){
                Toast.makeText(MainActivity.this,msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING, Toast.LENGTH_LONG).show();
            }
        }
    };

}

