package lk.waplak.dakma.scanner.fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lk.waplak.dakma.R;
import lk.waplak.dakma.TestActivity;
import lk.waplak.dakma.scanner.ScannerActivity;
import lk.waplak.dakma.utility.DownloadedDataCenter;

public class ScanQrCodeFragment extends Fragment {
    private Button scn;
    private Spinner spLect,spCourse,spCenter;
    private String selectedLect,selectedCource,selectedCenter;
    //private ImageView selectDate;
    private DatePickerDialog datePickerDialog;
    private int year;
    private int month;
    private int dayOfMonth;
    private Calendar calendar;
    //private EditText date;
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scan_qr_code, container, false);
        scn =       rootView.findViewById(R.id.btnScan);
        spLect =    rootView.findViewById(R.id.spnLect);
        spCourse =  rootView.findViewById(R.id.spnCourse);
        spCenter =  rootView.findViewById(R.id.spnCenter);
        //selectDate = rootView.findViewById(R.id.calBt);
        //date = rootView.findViewById(R.id.date);
        String currentDate = df.format(c);
        //date.setText(currentDate);
        loadSpinners();
        scn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadedDataCenter.getInstance(getActivity()).setLectId(selectedLect);
                DownloadedDataCenter.getInstance(getActivity()).setCourceId(selectedCource);
                DownloadedDataCenter.getInstance(getActivity()).setCenterId(selectedCenter);
                Intent scnQr = new Intent(getActivity(), ScannerActivity.class);
                startActivity(scnQr);
            }
        });
//        selectDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                datePickerDialog = new DatePickerDialog(getActivity(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                                date.setText(day + "/" + (month + 1) + "/" + year);
//                            }
//                        }, year, month, dayOfMonth);
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//                datePickerDialog.show();
//            }
//        });
//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                datePickerDialog = new DatePickerDialog(getActivity(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                                date.setText(day + "/" + (month + 1) + "/" + year);
//                            }
//                        }, year, month, dayOfMonth);
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//                datePickerDialog.show();
//            }
//        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        // getActivity().setTitle("Fragment 1");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public void loadSpinners(){
        final ArrayList<String> lectName =new ArrayList<>();
        final ArrayList<String> lectCode =new ArrayList<>();
        for(int x=0;x<DownloadedDataCenter.getInstance(getActivity()).getLoadLectures().size();++x){
            lectName.add(x,DownloadedDataCenter.getInstance(getActivity()).getLoadLectures().get(x).getName());
            lectCode.add(x,DownloadedDataCenter.getInstance(getActivity()).getLoadLectures().get(x).getId());
        }
        //selectedLect = lectCode.get(0);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                lectName);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLect.setAdapter(dataAdapter1);
        spLect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLect = lectCode.get(position);
                DownloadedDataCenter.getInstance(getActivity()).setLectureName(lectName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLect = lectCode.get(0);
                DownloadedDataCenter.getInstance(getActivity()).setLectureName(lectName.get(0));
            }
        });
        final ArrayList<String> courseName =new ArrayList<>();
        final ArrayList<String> courseCode =new ArrayList<>();
        for(int x=0;x<DownloadedDataCenter.getInstance(getActivity()).getLoadCaurse().size();++x){
            courseName.add(x,DownloadedDataCenter.getInstance(getActivity()).getLoadCaurse().get(x).getName());
            courseCode.add(x,DownloadedDataCenter.getInstance(getActivity()).getLoadCaurse().get(x).getId());
        }
        //selectedCource = courseCode.get(0);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                courseName);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(dataAdapter2);
        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCource = courseCode.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCource = courseCode.get(0);
            }
        });
        final ArrayList<String> centerName =new ArrayList<>();
        final ArrayList<String> centerCode =new ArrayList<>();
        for(int x=0;x<DownloadedDataCenter.getInstance(getActivity()).getLoadCenter().size();++x) {
            centerName.add(x, DownloadedDataCenter.getInstance(getActivity()).getLoadCenter().get(x).getName());
            centerCode.add(x, DownloadedDataCenter.getInstance(getActivity()).getLoadCenter().get(x).getId());
        }
        //selectedCenter = centerCode.get(0);
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                centerName);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCenter.setAdapter(dataAdapter3);
        spCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCenter = centerCode.get(position);
                DownloadedDataCenter.getInstance(getActivity()).setCenterName(centerName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCenter = centerCode.get(0);
                DownloadedDataCenter.getInstance(getActivity()).setCenterName(centerName.get(0));
            }
        });
    }

}
