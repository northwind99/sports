package com.aliasapps.sports.http;

/**
 * Created by evanchen on 16-04-08.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Random;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;


public class MultipartRequest extends Request <JSONObject> {
    private Response.Listener<JSONObject> mListener = null;
    private Response.ErrorListener mEListener;

    private final File mFilePart;
    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;

    public MultipartRequest(String url, File file, Response.ErrorListener eListener,
                        Response.Listener<JSONObject> rListener) {
        super(Method.POST, url, eListener);

        mListener = rListener;
        mEListener = eListener;
        mFilePart = file;
        buildMultipartEntity();
        httpentity = entity.build();
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.build().writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        return bos.toByteArray();
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response){
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    private void buildMultipartEntity() {
        entity.addBinaryBody("file", mFilePart, ContentType.create("image/jpeg"), setFileName());
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    private String setFileName(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int milisecs = c.get(Calendar.MILLISECOND);

        return hour+""+minute+""+seconds+""+milisecs+""+random()+""+random()+".jpg";
    }

    public static char random() {
        Random generator = new Random();
        char x = (char)(generator.nextInt(96) + 32);
        return x;
    }
}
