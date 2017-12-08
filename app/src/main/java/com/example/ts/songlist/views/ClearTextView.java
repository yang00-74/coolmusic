package com.example.ts.songlist.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.ts.songlist.R;

public class ClearTextView extends LinearLayout implements View.OnClickListener {
    private EditText mEditText;
    private ImageButton mClearButton;

    private AfterTextChangeListener mListener;

    public ClearTextView(Context context) {
        this(context, null);
    }

    public ClearTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.clear_text_view, this, true);

        mEditText = findViewById(R.id.et);
        mClearButton = findViewById(R.id.bt_clear);

        mEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    mClearButton.setVisibility(VISIBLE);
                } else {
                    mClearButton.setVisibility(GONE);
                }

                if (mListener != null) {
                    mListener.afterTextChanged(s);
                }
            }
        });
        mClearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_clear:
                mEditText.getText().clear();
                break;
            default:
                break;
        }
    }

    public interface AfterTextChangeListener {
        void afterTextChanged(Editable s);
    }

    public void setAfterTextChangeListener(AfterTextChangeListener l) {
        mListener = l;
    }

    private abstract class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
