package com.szu.twowayradio.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.szu.twowayradio.R;
import com.szu.twowayradio.utils.PreferenceUtils;

/**
 * Created by lgp on 2014/10/29.
 */
public class EditDialogFragment extends DialogFragment{

    private static String TitleKey = "dialog_title_key";

    private int titleString;

    private Button sureButton,cancelButton;

    private TextView titleTextView;

    private EditText editText01,editText02;

    private ButtonListener listener = null;

    private DialogType dialogType;

    public static EditDialogFragment newInstance(DialogType type)
    {
        EditDialogFragment dialogFragment = new EditDialogFragment();
        Bundle bundle = new Bundle();
        if (type == DialogType.CHANGE_USER)
        {
            bundle.putInt(TitleKey,R.string.change_user);
        }else if (type == DialogType.CHANGE_ADDRESS)
        {
            bundle.putInt(TitleKey,R.string.change_address);
        }
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            titleString = bundle.getInt(TitleKey);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editdialog_fragment,container,false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        titleTextView = (TextView)view.findViewById(R.id.title);
        editText01 = (EditText)view.findViewById(R.id.edit_01);
        editText02 = (EditText)view.findViewById(R.id.edit_02);
        sureButton = (Button)view.findViewById(R.id.sure);
        cancelButton = (Button)view.findViewById(R.id.cancel);
        titleTextView.setText(getActivity().getResources().getString(titleString));
        if (titleString == R.string.change_user)
        {
            editText01.setText(PreferenceUtils.getInstance(getActivity()).getStringParam(PreferenceUtils.USERNAME_KEY));
            editText02.setText(PreferenceUtils.getInstance(getActivity()).getStringParam(PreferenceUtils.PASSWORD_KEY));
            editText02.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            dialogType = DialogType.CHANGE_USER;
        }else
        {
            editText01.setText(PreferenceUtils.getInstance(getActivity()).getStringParam(PreferenceUtils.IP_KEY));
            editText02.setText(PreferenceUtils.getInstance(getActivity()).getStringParam(PreferenceUtils.PORT_KEY));
            dialogType = DialogType.CHANGE_ADDRESS;
        }
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                {
                    listener.onSure(dialogType,editText01.getText().toString(),editText02.getText().toString());
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                {
                    listener.onCancel();
                }
            }
        });
    }

    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    public interface ButtonListener{
        void onSure(DialogType type,String arg1,String arg2);
        void onCancel();
    }

    public enum DialogType{
        //change user dialog type,include user name and password edit
        CHANGE_USER(0x00),
        //change address dialog type,include ip and port edit
        CHANGE_ADDRESS(0x01);

        private int value;
        DialogType(int value) {
            this.value = value;
        }
        public int getIntValue() {
            return value;
        }
    }
}
