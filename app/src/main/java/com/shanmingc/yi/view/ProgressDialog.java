package com.shanmingc.yi.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.shanmingc.yi.R;

public class ProgressDialog extends AlertDialog {

    private Context context;

    private ProgressDialog(Context context, int themeId) {
        super(context);
        this.context = context;
    }

    public static class Builder {

        private ProgressDialog mDialog;
        private View mLayout;

        public Builder(Context context) {
            mDialog = new ProgressDialog(context, 0);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout = inflater.inflate(R.layout.progress_dialog, null, false);
        }

        public ProgressDialog build() {
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }
    }
}
