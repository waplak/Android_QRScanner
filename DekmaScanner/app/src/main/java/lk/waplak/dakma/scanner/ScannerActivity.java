package lk.waplak.dakma.scanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.zxing.Result;


import lk.waplak.dakma.R;
import lk.waplak.dakma.auth.JsonPlaceHolderApi;
import lk.waplak.dakma.auth.ScanResult;
import lk.waplak.dakma.utility.AndroidUtill;
import lk.waplak.dakma.utility.DownloadedDataCenter;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.view.ViewGroup.LayoutParams;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;


public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private PopupWindow popupWindow;
    private int selecdedFeeType;
    private ProgressDialog dialog ;
    DecimalFormat df = new DecimalFormat("#,###,###,###.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                //Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
                requestPermission();

            }
        }

    }
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!cameraAccepted){

                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();
        if(popupWindow!=null && popupWindow.isShowing()){
            return;
        }
        String lectid = DownloadedDataCenter.getInstance(ScannerActivity.this).getLectId();
        String courseid = DownloadedDataCenter.getInstance(ScannerActivity.this).getCourceId();
        String centerId =DownloadedDataCenter.getInstance(ScannerActivity.this).getCenterId();
        String token =DownloadedDataCenter.getInstance(ScannerActivity.this).getToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<ScanResult> call = jsonPlaceHolderApi.getPaymentDetals(
                lectid,
                courseid,
                centerId,
                result,
                "",
                "Bearer "+token
        );
        call.enqueue(new Callback<ScanResult>() {
            @Override
            public void onResponse(Call<ScanResult> call, Response<ScanResult> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(ScannerActivity.this,"Error Code:"+response.code(),Toast.LENGTH_LONG);
                }else{
                    ScanResult scanResult = response.body();
                    if(scanResult==null){
                        mScannerView.resumeCameraPreview(ScannerActivity.this);
                        showWarning();
                    }else {
                        if (scanResult.isHasPaymentDone()) {
                            //showPaidScannerPopup(scanResult);
                            showPaidScannerPopup(scanResult);
                        } else {
                            showNonPaidScannerPopup(scanResult);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanResult> call, Throwable t) {

            }
        });
    }
    private void showPaidScannerPopup(ScanResult scanResult){

        LayoutInflater layoutInflater = (LayoutInflater) ScannerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_layout,null);
        Spinner spFeeType =    customView.findViewById(R.id.feeType);
        ImageView img = customView.findViewById(R.id.dlg_one_button_iv_icon);
        //img.setImageResource(R.drawable.ic_close);
        img.setImageResource(R.drawable.ic_checked);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        int dialogWindowWidth = (int) (displayWidth * 0.85f);
        popupWindow = new PopupWindow(customView,dialogWindowWidth, LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mScannerView, Gravity.CENTER, 0, 0);
        Button fr= customView.findViewById(R.id.dlg_one_button_btn_ok);
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mScannerView.resumeCameraPreview(ScannerActivity.this);

            }
        });
        new Handler().postDelayed(new Runnable(){
            public void run() {
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    mScannerView.resumeCameraPreview(ScannerActivity.this);
                }

            }
        }, 2 *1000);
    }
    private void showNonPaidScannerPopup(final ScanResult scanResult){

        LayoutInflater layoutInflater = (LayoutInflater) ScannerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_not_pay,null);
        Spinner spFeeType =    customView.findViewById(R.id.feeType);
        TextView stdId =customView.findViewById(R.id.studentId);
        TextView stdName = customView.findViewById(R.id.studentName);
        TextView feeAmount = customView.findViewById(R.id.fee_amount);
        Button edit =customView.findViewById(R.id.edit);
        Button pay =customView.findViewById(R.id.pay);
        stdId.setText(scanResult.getStudentRegNo()!=null?scanResult.getStudentRegNo():"UNKNOWN");
        stdName.setText(scanResult.getStudentName()!=null?scanResult.getStudentName():"UNKNOWN");
        feeAmount.setText("Rs. "+df.format(scanResult.getFee()));
        int feeType =scanResult.getFeeTypeId();
        final ArrayList<String> feeTypeName =new ArrayList<>();
        final ArrayList<String> feeTypeCode =new ArrayList<>();
        for(int x=0;x<DownloadedDataCenter.getInstance(ScannerActivity.this).getLoadFeeType().size();++x) {
            feeTypeName.add(x, DownloadedDataCenter.getInstance(ScannerActivity.this).getLoadFeeType().get(x).getName());
            feeTypeCode.add(x, DownloadedDataCenter.getInstance(ScannerActivity.this).getLoadFeeType().get(x).getId());
        }
        //selecdedFeeType = feeTypeCode.get(0);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(
                ScannerActivity.this, android.R.layout.simple_spinner_item,
                feeTypeName);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFeeType.setAdapter(dataAdapter1);
        int spinnerPosition = dataAdapter1.getPosition(feeTypeName.get(feeTypeCode.indexOf(Integer.toString(feeType))));
        spFeeType.setSelection(spinnerPosition);

        spFeeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecdedFeeType = Integer.parseInt(feeTypeCode.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selecdedFeeType = Integer.parseInt(feeTypeCode.get(0));
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        int dialogWindowWidth = (int) (displayWidth * 0.85f);
        popupWindow = new PopupWindow(customView,dialogWindowWidth, LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mScannerView, Gravity.CENTER, 0, 0);
        Button fr= customView.findViewById(R.id.close);
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFeeType(scanResult);
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPayment(scanResult);
            }
        });
    }
    private void showWarning(){

        LayoutInflater layoutInflater = (LayoutInflater) ScannerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_not_found,null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        int dialogWindowWidth = (int) (displayWidth * 0.85f);
        popupWindow = new PopupWindow(customView,dialogWindowWidth, LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mScannerView, Gravity.CENTER, 0, 0);
        Button fr= customView.findViewById(R.id.dlg_one_button_btn_ok);
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    popupWindow.dismiss();
                    mScannerView.resumeCameraPreview(ScannerActivity.this);

            }
        });
        new Handler().postDelayed(new Runnable(){
            public void run() {
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    mScannerView.resumeCameraPreview(ScannerActivity.this);
                }

            }
        }, 2 *1000);
    }

    private void changeFeeType(ScanResult scanResult){
        String token =DownloadedDataCenter.getInstance(ScannerActivity.this).getToken();
        scanResult.setFeeTypeId(selecdedFeeType);
        dialog = new ProgressDialog(
                ScannerActivity.this,ProgressDialog.THEME_HOLO_DARK);
        this.dialog.setMessage("Changing Fee Type....");
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonElement> call = jsonPlaceHolderApi.postChangePaymentDetails("Bearer "+token,scanResult);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
    private void doPayment(ScanResult scanResult){
        scanResult.setHasPaymentDone(true);
        String token =DownloadedDataCenter.getInstance(ScannerActivity.this).getToken();
        dialog = new ProgressDialog(
                ScannerActivity.this,ProgressDialog.THEME_HOLO_DARK);
        this.dialog.setMessage("Payment Processing....");
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonElement> call = jsonPlaceHolderApi.postPay("Bearer "+token,scanResult);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                        popupWindow.dismiss();
                        mScannerView.resumeCameraPreview(ScannerActivity.this);
                    }
                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
}
