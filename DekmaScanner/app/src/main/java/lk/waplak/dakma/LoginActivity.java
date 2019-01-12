package lk.waplak.dakma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import lk.waplak.dakma.auth.AccessToken;
import lk.waplak.dakma.auth.DkResponse;
import lk.waplak.dakma.auth.JsonPlaceHolderApi;
import lk.waplak.dakma.utility.AndroidUtill;
import lk.waplak.dakma.utility.DownloadedDataCenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp;
    private boolean isClickLogin=false;
    private ProgressDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutEmail.requestFocus();
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btn_login);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        TextView txtt=(TextView)findViewById(R.id.footerMark);
        final SpannableStringBuilder sb = new SpannableStringBuilder("Copyright © 2017 Dekma Institute. All Rights Reserved. | Powered by innosoft");
        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255,255,255));
        // Span to make text bold
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, 68, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // make them also bold
        //sb.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        txtt.setText(sb);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClickLogin) {
                    isClickLogin=true;
                    submitForm();
                }
            }
        });
    }
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        final String email = inputEmail.getText().toString().trim();
        final String pwrd = inputPassword.getText().toString().trim();
        if(AndroidUtill.isNetworkConnected(LoginActivity.this)) {
            DownloadedDataCenter.getInstance(LoginActivity.this).getLoadLectures().clear();
            DownloadedDataCenter.getInstance(LoginActivity.this).getLoadCaurse().clear();
            DownloadedDataCenter.getInstance(LoginActivity.this).getLoadCenter().clear();
            DownloadedDataCenter.getInstance(LoginActivity.this).getLoadFeeType().clear();
            DownloadedDataCenter.getInstance(LoginActivity.this).setTokenType(null);
            DownloadedDataCenter.getInstance(LoginActivity.this).setToken(null);
            dialog = new ProgressDialog(
                    LoginActivity.this,ProgressDialog.THEME_HOLO_DARK);
            this.dialog.setMessage("Authenticating....");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AndroidUtill.BASE_URL_)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<AccessToken> call = jsonPlaceHolderApi.getToken(email,pwrd,"password");
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    if(!response.isSuccessful()){
                        isClickLogin = false;
                        inputLayoutPassword.setError(getString(R.string.err_msg_useraname_password));
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this,"Login Failed, Error Code:"+response.code(),Toast.LENGTH_LONG);
                    }else{
                        DownloadedDataCenter.getInstance(LoginActivity.this).setTokenType(response.body().getToken_type());
                        DownloadedDataCenter.getInstance(LoginActivity.this).setToken(response.body().getAccess_token());
                        dialog.setMessage("Loading Data....");
                        DownloadedDataCenter.getInstance(LoginActivity.this).setUserName(email);
                        loadLectures(response.body().getAccess_token(),response.body().getToken_type());

                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    isClickLogin = false;
                    inputLayoutPassword.setError(getString(R.string.err_msg_useraname_password));
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

        }else{
            Toast.makeText(
                    LoginActivity.this,
                    "No internet connection", Toast.LENGTH_LONG).show();
            isClickLogin=false;
            return;
        }

    }
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            isClickLogin=false;
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            isClickLogin=false;
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return true;
        //!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
            }
        }
    }

    private void loadLectures(final String token, final String tokenType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<DkResponse>> call = jsonPlaceHolderApi.getLoadLectName(tokenType+" "+token);
        call.enqueue(new Callback<List<DkResponse>>() {
            @Override
            public void onResponse(Call<List<DkResponse>> call, Response<List<DkResponse>> response) {
                if(!response.isSuccessful()){
                    isClickLogin = false;
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this,"Login Failed, Error Code:"+response.code(),Toast.LENGTH_LONG);
                }else{
                    List<DkResponse> lectList = response.body();
                    for(DkResponse lect:lectList){
                        if(lect.getName()!=null) {
                            DownloadedDataCenter.getInstance(LoginActivity.this).setLoadLectures(lect);
                        }
                    }
                    loadCaurses(token,tokenType);
                }
            }

            @Override
            public void onFailure(Call<List<DkResponse>> call, Throwable t) {
                isClickLogin = false;
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
    private void loadCaurses(final String token, final String tokenType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<DkResponse>> call = jsonPlaceHolderApi.getLoadCourseName(tokenType+" "+token);
        call.enqueue(new Callback<List<DkResponse>>() {
            @Override
            public void onResponse(Call<List<DkResponse>> call, Response<List<DkResponse>> response) {
                if(!response.isSuccessful()){
                    isClickLogin = false;
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this,"Login Failed, Error Code:"+response.code(),Toast.LENGTH_LONG);
                }else{
                    List<DkResponse> caurse = response.body();
                    for(DkResponse cs:caurse){
                        if(cs.getName()!=null) {
                            DownloadedDataCenter.getInstance(LoginActivity.this).setLoadCaurse(cs);
                        }
                    }
                    loadCenter(token,tokenType);
                }
            }

            @Override
            public void onFailure(Call<List<DkResponse>> call, Throwable t) {
                isClickLogin = false;
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
    private void loadCenter(final String token, final String tokenType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<DkResponse>> call = jsonPlaceHolderApi.getLoadCenterName(tokenType+" "+token);
        call.enqueue(new Callback<List<DkResponse>>() {
            @Override
            public void onResponse(Call<List<DkResponse>> call, Response<List<DkResponse>> response) {
                if(!response.isSuccessful()){
                    isClickLogin = false;
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this,"Login Failed, Error Code:"+response.code(),Toast.LENGTH_LONG);
                }else{
                    List<DkResponse> centers = response.body();
                    for(DkResponse cnt:centers){
                        if(cnt.getName()!=null) {
                            DownloadedDataCenter.getInstance(LoginActivity.this).setLoadCenter(cnt);
                        }
                    }
                    loadFeeType(token,tokenType);
                }
            }

            @Override
            public void onFailure(Call<List<DkResponse>> call, Throwable t) {
                isClickLogin = false;
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
    private void loadFeeType(final String token, final String tokenType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AndroidUtill.COMMON_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<DkResponse>> call = jsonPlaceHolderApi.getLoadFeeTypeName(tokenType+" "+token);
        call.enqueue(new Callback<List<DkResponse>>() {
            @Override
            public void onResponse(Call<List<DkResponse>> call, Response<List<DkResponse>> response) {
                if(!response.isSuccessful()){
                    isClickLogin = false;
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this,"Login Failed, Error Code:"+response.code(),Toast.LENGTH_LONG);
                }else{
                    List<DkResponse> centers = response.body();
                    for(DkResponse cnt:centers){
                        if(cnt.getName()!=null) {
                            DownloadedDataCenter.getInstance(LoginActivity.this).setLoadFeeType(cnt);
                        }
                    }
                    successResult();
                }
            }

            @Override
            public void onFailure(Call<List<DkResponse>> call, Throwable t) {
                isClickLogin = false;
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }
    private void successResult(){
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));

        TextView textV = (TextView) toastLayout.findViewById(R.id.custom_toast_message);
        textV.setText("You are Welcome ");

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();

        Intent intent = new Intent(LoginActivity.this, NevigationActivity.class);
        startActivity(intent);
    }


}

