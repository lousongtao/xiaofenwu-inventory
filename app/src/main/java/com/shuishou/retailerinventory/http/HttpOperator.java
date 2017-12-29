package com.shuishou.retailerinventory.http;

import android.util.Log;

import com.shuishou.retailerinventory.InstantValue;
import com.shuishou.retailerinventory.bean.Category1;
import com.shuishou.retailerinventory.bean.Goods;
import com.shuishou.retailerinventory.bean.HttpResult;
import com.shuishou.retailerinventory.bean.UserData;
import com.shuishou.retailerinventory.ui.LoginActivity;
import com.shuishou.retailerinventory.ui.MainActivity;
import com.shuishou.retailerinventory.utils.CommonTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.retailerinventory.R;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/9.
 */

public class HttpOperator {

    private String logTag = "HttpOperation";


    private MainActivity mainActivity;
    private LoginActivity loginActivity;

    private static final int WHAT_VALUE_QUERYGOODS = 1;
    private static final int WHAT_VALUE_LOGIN = 2;

    private Gson gson = new Gson();

    private OnResponseListener responseListener =  new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch (what){
                case WHAT_VALUE_QUERYGOODS :
                    doResponseQueryGoods(response);
                    break;
                case WHAT_VALUE_LOGIN :
                    doResponseLogin(response);
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e("Http failed", "what = "+ what + "\nresponse = "+ response.get());
            MainActivity.LOG.error("Response Listener On Faid. what = "+ what + "\nresponse = "+ response.get());
            String msg = InstantValue.NULLSTRING;
            switch (what){
                case WHAT_VALUE_QUERYGOODS :
                    msg = "Failed to load goods data. Please restart app!";
                    break;
                case WHAT_VALUE_LOGIN :
                    msg = "Failed to login. Please retry!";
                    break;
                default:
            }
            CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", msg);
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private RequestQueue requestQueue = NoHttp.newRequestQueue();

    public HttpOperator(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public HttpOperator(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    private void doResponseQueryGoods(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryGoods: " + response.getException().getMessage() );
            MainActivity.LOG.error("doResponseQueryGoods: " + response.getException().getMessage());
            sendErrorMessageToToast("Http:doResponseQueryGoods: " + response.getException().getMessage());
            return;
        }
        HttpResult<ArrayList<Category1>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
        if (result.success){
            ArrayList<Category1> cs = result.data;
            mainActivity.initData(cs);
        }else {
            Log.e(logTag, "doResponseQueryGoods: get FALSE for query goods");
            MainActivity.LOG.error("doResponseQueryGoods: get FALSE for query goods");
        }
    }

    private void doResponseLogin(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseLogin: " + response.getException().getMessage() );
            MainActivity.LOG.error("doResponseLogin: " + response.getException().getMessage());
            sendErrorMessageToToast("doResponseLogin: " + response.getException().getMessage());
            return;
        }
        JSONObject loginResult = response.get();
        try {
            if ("ok".equals(loginResult.getString("result"))){
                UserData loginUser = new UserData();
                loginUser.setId(loginResult.getInt("userId"));
                loginUser.setName(loginResult.getString("userName"));
                loginActivity.loginSuccess(loginUser);
            } else {
                Log.e(logTag, "doResponseLogin: get FAIL while login" + loginResult.getString("result"));
                MainActivity.LOG.error("doResponseLogin: get FAIL while login"  + loginResult.getString("result"));
                sendErrorMessageToToast("doResponseLogin: get FAIL while login"  + loginResult.getString("result"));
            }
        } catch (JSONException e) {
            Log.e(logTag, "doResponseLogin: parse json: " + e.getMessage() );
            MainActivity.LOG.error("doResponseLogin: parse json:" + e.getMessage());
            sendErrorMessageToToast("doResponseLogin: parse json:" + e.getMessage());
        }
    }

    public void login(String name, String password){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/login");
        request.add("username", name);
        request.add("password", password);
        requestQueue.add(WHAT_VALUE_LOGIN, request, responseListener);
    }

    //load goods
    public void loadData(){
        mainActivity.getProgressDlgHandler().sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA,
                "start loading goods data ..."));
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/goods/querygoods");
        requestQueue.add(WHAT_VALUE_QUERYGOODS, request, responseListener);
    }

    private void onFailedLoadMenu(){
        //TODO: require restart app
    }

    private void sendErrorMessageToToast(String sMsg){
        mainActivity.getToastHandler().sendMessage(CommonTool.buildMessage(MainActivity.TOASTHANDLERWHAT_ERRORMESSAGE,sMsg));
    }

    public void uploadErrorLog(File file, String machineCode){
        int key = 0;// the key of filelist;
        UploadErrorLogListener listener = new UploadErrorLogListener(mainActivity);
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/uploaderrorlog", RequestMethod.POST);
        FileBinary bin1 = new FileBinary(file);
        request.add("logfile", bin1);
        request.add("machineCode", machineCode);
        listener.addFiletoList(key, file.getAbsolutePath());
        requestQueue.add(key, request, listener);
    }

    public HttpResult<Goods> saveAmount(int goodsId, int amount){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/goods/changeamount", RequestMethod.POST);
        request.add("userId", mainActivity.getLoginUser().getId());
        request.add("id", String.valueOf(goodsId));
        request.add("amount", String.valueOf(amount));
        Response<JSONObject> response = NoHttp.startRequestSync(request);

        if (response.getException() != null){
            HttpResult<Goods> result = new HttpResult<>();
            result.result = response.getException().getMessage();
            return result;
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while change goods amount. response.get() is null.");
            MainActivity.LOG.error("Error occur while change goods amount. response.get() is null.");
            HttpResult<Goods> result = new HttpResult<>();
            result.result = "Error occur while change goods amount. response.get() is null";
            return result;
        }
        HttpResult<Goods> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Goods>>(){}.getType());
        return result;
    }

    public HttpResult<Goods> importAmount(int goodsId, int amount){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/goods/import_goods", RequestMethod.POST);
        request.add("userId", mainActivity.getLoginUser().getId());
        request.add("id", String.valueOf(goodsId));
        request.add("amount", String.valueOf(amount));
        Response<JSONObject> response = NoHttp.startRequestSync(request);

        if (response.getException() != null){
            HttpResult<Goods> result = new HttpResult<>();
            result.result = response.getException().getMessage();
            return result;
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while import goods amount. response.get() is null.");
            MainActivity.LOG.error("Error occur while import goods amount. response.get() is null.");
            HttpResult<Goods> result = new HttpResult<>();
            result.result = "Error occur while import goods amount. response.get() is null";
            return result;
        }
        HttpResult<Goods> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Goods>>(){}.getType());
        return result;
    }
}
