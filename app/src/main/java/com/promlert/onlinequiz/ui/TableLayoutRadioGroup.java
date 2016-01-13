package com.promlert.onlinequiz.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

public class TableLayoutRadioGroup extends TableLayout implements OnClickListener {

    private static final String TAG = TableLayoutRadioGroup.class.getSimpleName();

    private ArrayList<RadioButton> mRadioButtonList = new ArrayList<>();

    /**
     * @param context
     */
    public TableLayoutRadioGroup(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    public TableLayoutRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onClick(View v) {
        for (RadioButton r : mRadioButtonList) {
            r.setChecked(false);
        }

        final RadioButton rb = (RadioButton) v;
        rb.setChecked(true);
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow) child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        for (int i = 0; i < tr.getChildCount(); i++) {
            final View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {
                v.setOnClickListener(this);
                mRadioButtonList.add((RadioButton) v);
            }
        }
    }

    public int getCheckedRadioButtonId() {
        for (RadioButton r : mRadioButtonList) {
            if (r.isChecked()) {
                return r.getId();
            }
        }
        return -1;
    }
}