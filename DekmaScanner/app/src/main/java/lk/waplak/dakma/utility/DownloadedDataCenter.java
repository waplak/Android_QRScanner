package lk.waplak.dakma.utility;

import android.content.Context;

import java.util.ArrayList;

import lk.waplak.dakma.auth.DkResponse;


public class DownloadedDataCenter {
    private final Context myContext;
    private static DownloadedDataCenter mdownloadedInstence;
    private ArrayList<DkResponse> loadLectures = new ArrayList<DkResponse>();
    private ArrayList<DkResponse> loadCaurse = new ArrayList<DkResponse>();
    private ArrayList<DkResponse> loadCenter = new ArrayList<DkResponse>();
    private ArrayList<DkResponse> feeType = new ArrayList<DkResponse>();
    private String token;
    private String tokenType;
    private String lectId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public void setLectId(String lectId) {
        this.lectId = lectId;
    }

    public void setCourceId(String courceId) {
        this.courceId = courceId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    private String courceId;

    public String getLectId() {
        return lectId;
    }

    public String getCourceId() {
        return courceId;
    }

    public String getCenterId() {
        return centerId;
    }

    private String centerId;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    private DownloadedDataCenter(Context context) {
        this.myContext = context;
    }

    public static synchronized DownloadedDataCenter getInstance(Context context) {
        try{
            if (mdownloadedInstence == null) {
                mdownloadedInstence = new DownloadedDataCenter(context);
            }
            return mdownloadedInstence;
        } catch (Exception e) {
            throw new Error("Error  ");
        }
    }
    public ArrayList<DkResponse> getLoadLectures() {
        return loadLectures;
    }
    public ArrayList<DkResponse> getLoadCaurse() {
        return loadCaurse;
    }
    public ArrayList<DkResponse> getLoadCenter() {
        return loadCenter;
    }
    public ArrayList<DkResponse> getLoadFeeType() {
        return feeType;
    }

    public void setLoadLectures(DkResponse test) {
        this.loadLectures.add(test);
    }
    public void setLoadCaurse(DkResponse test) {
        this.loadCaurse.add(test);
    }
    public void setLoadCenter(DkResponse test) {
        this.loadCenter.add(test);
    }
    public void setLoadFeeType(DkResponse test) {
        this.feeType.add(test);
    }

}
