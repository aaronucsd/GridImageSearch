package com.codepath.gridimagesearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by long on 3/1/15.
 */
public class ImageResult implements Serializable {
    private static final long serialVersionUID = -2893089570992474768L;

    public String fullUrl;
    public String thumbUrl;
    public String title;
    public int height;
    public int width;

    // new ImageResult(..raw item json)
    public ImageResult(JSONObject json){
        try{
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    // ImageResult.fromJSONArray([...,...]) => populate into the ImageResult model into ArrayList
    public static ArrayList<ImageResult> fromJSONArray(JSONArray array){
        ArrayList<ImageResult>  results = new ArrayList<ImageResult>();
        for(int i=0; i< array.length(); i++){
            try{
                results.add(new ImageResult(array.getJSONObject(i)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return results;
    }

}
