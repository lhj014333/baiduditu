package com.feicuiedu.treasure_20170327.custom;



import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;


import com.feicuiedu.treasure_20170327.R;


/**
 * Created by Administrator on 2017/3/28.
 */

public class AlertDialogFragment extends DialogFragment {


    private static String KEY_TITLE="key_title";
    private static String KEY_MESSAGE="key_message";

    public static  AlertDialogFragment getInastance(String title, String message){
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE,title);
        bundle.putString(KEY_MESSAGE,message);
        alertDialogFragment.setArguments(bundle);
        return alertDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })
                .create();
    }


}
