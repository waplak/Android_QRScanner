package lk.waplak.dakma.auth;

import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("Lecturer/GetAll4Combo")
    Call<List<DkResponse>> getLoadLectName(@Header("Authorization")String authToken);
    @GET("Course/GetAll4Combo")
    Call<List<DkResponse>> getLoadCourseName(@Header("Authorization")String authToken);
    @GET("Center/GetAll4Combo")
    Call<List<DkResponse>> getLoadCenterName(@Header("Authorization")String authToken);
    @GET("FeeType/GetAll4Combo")
    Call<List<DkResponse>> getLoadFeeTypeName(@Header("Authorization")String authToken);
    @GET("CardMark/GetPaymentDetails?")
    Call<ScanResult>getPaymentDetals(@Query("lecturerId")String lectId,
                                            @Query("courseId")String courseId,
                                            @Query("centreId")String centerId,
                                            @Query("studentRegNo")String studentRegNo,
                                            @Query("date")String date,
                                            @Header("Authorization")String authToken);

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> getToken(@Field("username")String username, @Field("password")String password, @Field("grant_type")String grant_type);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("CardMark/Pay")
    Call <JsonElement> postPay(@Header("Authorization")String authToken,@Body ScanResult scanResult);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("CardMark/ChangePaymentDetails")
    Call <JsonElement> postChangePaymentDetails(@Header("Authorization")String authToken, @Body ScanResult scanResult);

}
