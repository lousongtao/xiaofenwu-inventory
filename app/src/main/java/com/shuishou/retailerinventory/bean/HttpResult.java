package com.shuishou.retailerinventory.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/22.
 */

public class HttpResult<T> {
    public String result;
    public boolean success;
    @SerializedName(value = "data")
    public T data;

}
