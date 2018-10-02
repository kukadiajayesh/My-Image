package com.app.photobook.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by shree on 10-01-2017.
 */

public class ResponseJson {

    public boolean status;
    public String message = "", response_json;
    public JSONObject jsonObject, jsonObjectDetails;
    public JSONArray details = null;

}
