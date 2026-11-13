package com.example.ozora.senda.ui;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ozora.senda.R;
import com.google.android.material.button.MaterialButton;

public abstract class RouteScreen extends FieldScreen {
    protected static final int INK = 0xff1d4032;
    protected static final int GREEN = 0xff42644e;
    protected static final int SAGE = 0xffd2dfc9;

    protected LinearLayout column() {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        return column;
    }

    protected TextView label(CharSequence value, int size, boolean bold) {
        TextView label = new TextView(this);
        label.setText(value);
        label.setTextSize(size);
        label.setTextColor(INK);
        if (bold) label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return label;
    }

    protected MaterialButton backButton() {
        MaterialButton button = new MaterialButton(this);
        button.setText("‹");
        button.setTextSize(30);
        button.setTextColor(INK);
        button.setBackgroundTintList(ColorStateList.valueOf(0xffffffff));
        button.setCornerRadius(dp(24));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(0, 0, 0, 0);
        button.setContentDescription(getString(R.string.route_back));
        button.setOnClickListener(view -> finish());
        return button;
    }

    protected void card(View view) {
        view.setBackgroundResource(R.drawable.home_card);
        view.setClipToOutline(true);
        view.setElevation(dp(2));
    }

    protected void addWithTopMargin(LinearLayout parent, View child, int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(margin);
        parent.addView(child, params);
    }
}
