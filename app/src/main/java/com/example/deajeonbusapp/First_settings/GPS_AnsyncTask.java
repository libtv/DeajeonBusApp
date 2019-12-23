package com.example.deajeonbusapp.First_settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class GPS_AnsyncTask extends AsyncTask<Integer , Integer , Integer> {
    //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
    int value;
    Context context;
    ProgressDialog progressDialog = null;

    public GPS_AnsyncTask(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        value = 0;
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("\t세팅 로딩중...");
        //show dialog
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("GPS Setting").setMessage("GPS를 세팅합니다.\n 현재 위치값을 통해 주변 정류장을 검색하는데 기본 세팅값은 5개이며\n 세팅을 통해 바꾸실 수 있습니다.");

        dlg.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                progressDialog.show();
            }
        });

        dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                return;
            }
        });
        dlg.show();
    }

    protected Integer doInBackground(Integer ... values) {
        while (isCancelled() == false) {
            value++;
            if (value >= 100) {
                break;
            } else {
                publishProgress(value);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }

        return value;
    }

    protected void onProgressUpdate(Integer ... values) {
        progressDialog.setProgress(values[0].intValue());
        progressDialog.setMessage("현재 진행 값 : " + values[0].toString());
    }


    //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
    protected void onPostExecute(Integer result) {
        progressDialog.setProgress(0);
        progressDialog.dismiss();
        progressDialog.setMessage("완료되었습니다");
    }

    //Task가 취소되었을때 호출
    protected void onCancelled() {
        progressDialog.setProgress(0);
        progressDialog.setMessage("취소되었습니다");
    }
}