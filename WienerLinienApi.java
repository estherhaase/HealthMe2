package com.example.android.healthme;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.android.healthme.BuildConfig.API_KEY;

class WienerLinenApi {

    private final static String WL_AUTHORITY = "www.wienerlinien.at";
    private final static String REALTIME_PATH = "ogd_realtime";
    private final static String ROUTING_PATH = "ogd_routing";
    private final static String MONITOR_PATH = "monitor";
    private final static String RBL_PARAM = "rbl";
    private final static String TRAFFIC_INFO_PARAM = "activateTrafficInfo";
    private final static String TRAFFIC_INFO_PATH = "stoerungkurz";
    private final static String KEY_PARAM = "sender";
    private final static String IESLAMP_AUTHORITY = "ieslamp.technikum-wien.at";
    private final static String DB_PATH = "sys_bvu4_17_j";
    private final static String PROJECT_PATH = "Stationfinder";
    private final static String STATION_FILE_PATH = "QueryData.php";
    private final static String HOSPITAL_FILE_PATH = "getHospitals.php";
    private final static String NEARBY_STATIONS_PATH = "coordsDistance.php";
    private final static String XML_TRIP_PATH = "XML_TRIP_REQUEST2";
    private final static String ORIGIN__TYPE_PARAM = "type_origin";
    private final static String ORIGIN__NAME_PARAM = "name_origin";
    private final static String DESTIN_TYPE_PARAM = "type_destination";
    private final static String DESTIN_NAME_PARAM = "name_destination";
    private final static String TYPE_VALUE = "coord";
    private final static String OUTPUT_PARAM = "outputFormat";
    private final static String JSON_VALUE = "JSON";
    private final static String ROUTING_BASE = "http://www.wienerlinien.at/ogd_routing/XML_TRIP_REQUEST2?outputFormat=JSON";





    static String buildWienerLinienMonitorUrl(ArrayList<Integer> rblNum){

        Uri monitorUri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority(WL_AUTHORITY)
                .appendEncodedPath(REALTIME_PATH)
                .appendEncodedPath(MONITOR_PATH)
                .appendQueryParameter(TRAFFIC_INFO_PARAM, TRAFFIC_INFO_PATH)
                .appendQueryParameter(KEY_PARAM, API_KEY);


        for(int i= 0; i < rblNum.size(); i++){
            builder.appendQueryParameter(RBL_PARAM, rblNum.get(i).toString());
        }

        monitorUri = builder.build();


        URL url = null;
        try {
            url = new URL(monitorUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        assert url != null;
        return url.toString();
    }

    static URL buildRoutesRequest(Location myLocation, Location destinationLocation) {

        String originValue = Double.toString(myLocation.getLongitude()) + ":" + Double.toString(myLocation.getLatitude()) + ":WGS84";
        String destValue = Double.toString(destinationLocation.getLongitude()) + ":" + Double.toString(destinationLocation.getLatitude()) + ":WGS84";

        Uri routeUri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority(WL_AUTHORITY)
                .appendEncodedPath(ROUTING_PATH)
                .appendEncodedPath(XML_TRIP_PATH)
                .appendQueryParameter(OUTPUT_PARAM, JSON_VALUE)
                .appendQueryParameter(ORIGIN__TYPE_PARAM, TYPE_VALUE)
                .appendQueryParameter(ORIGIN__NAME_PARAM, originValue)
                .appendQueryParameter(DESTIN_TYPE_PARAM, TYPE_VALUE)
                .appendQueryParameter(DESTIN_NAME_PARAM, destValue);

        routeUri = builder.build();
        String uriString = routeUri.toString();

        URL url = null;
        try {
            url = new URL(uriString);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        assert url != null;
        return url;

    }

    static URL buildStationDatabaseRequestUrl(){

        Uri dbUri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority(IESLAMP_AUTHORITY)
                .appendEncodedPath(DB_PATH)
                .appendEncodedPath(PROJECT_PATH)
                .appendEncodedPath(STATION_FILE_PATH);

        dbUri = builder.build();
        URL url = null;
        try {
            url = new URL(dbUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;

    }

    static URL buildHospitalDatabaseRequestUrl(){

        Uri dbUri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority(IESLAMP_AUTHORITY)
                .appendEncodedPath(DB_PATH)
                .appendEncodedPath(PROJECT_PATH)
                .appendEncodedPath(HOSPITAL_FILE_PATH);

        dbUri = builder.build();
        URL url = null;
        try {
            url = new URL(dbUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;

    }

    static URL buildNearbyStationRequestUrl(){

        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority(IESLAMP_AUTHORITY)
                .appendEncodedPath(DB_PATH)
                .appendEncodedPath(PROJECT_PATH)
                .appendEncodedPath(NEARBY_STATIONS_PATH);

        uri = builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    @Nullable
    static String getHttpResponse(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
