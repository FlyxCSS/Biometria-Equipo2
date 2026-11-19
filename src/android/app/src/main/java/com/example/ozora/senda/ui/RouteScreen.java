package com.example.ozora.senda.ui;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ozora.senda.R;
import com.google.android.material.button.MaterialButton;

public abstract class RouteScreen extends FieldScreen {
    protected static final int INK = 0xff1f3d2f;
    protected static final int MUTED = 0xff5e6861;
    protected static final int GREEN = 0xff42644e;
    protected static final int SAGE = 0xffd2dfc9;
    protected static final int CREAM = 0xfff4f1ea;

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
        if (bold) label.setTypeface(FieldScreen.inter(this, true));
        return label;
    }

    protected MaterialButton backButton() {
        MaterialButton button = new MaterialButton(this);
        button.setText("");
        button.setIconResource(R.drawable.ic_s_back);
        button.setIconTint(ColorStateList.valueOf(INK));
        button.setIconSize(dp(22));
        button.setIconPadding(0);
        button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        button.setBackgroundTintList(ColorStateList.valueOf(0xffffffff));
        button.setCornerRadius(dp(24));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(0, 0, 0, 0);
        button.setElevation(dp(2));
        button.setContentDescription(getString(R.string.route_back));
        button.setOnClickListener(view -> finish());
        return button;
    }

    protected void card(View view) {
        view.setBackgroundResource(R.drawable.s_card);
        view.setClipToOutline(true);
        view.setElevation(dp(1));
    }

    /** Valor grande con la unidad pequeña y gris, p. ej. "72 ppb". */
    protected CharSequence valueWithUnit(String value, String unit) {
        android.text.SpannableStringBuilder text = new android.text.SpannableStringBuilder(value);
        if (unit == null || unit.isEmpty()) return text;
        int from = text.length();
        text.append(" ").append(unit);
        text.setSpan(new android.text.style.RelativeSizeSpan(0.6f), from, text.length(), 0);
        text.setSpan(new android.text.style.StyleSpan(Typeface.NORMAL), from, text.length(), 0);
        text.setSpan(new android.text.style.ForegroundColorSpan(MUTED), from, text.length(), 0);
        return text;
    }

    protected TextView chip(CharSequence value, int tone, int surface, int icon) {
        TextView chip = label(value, 14, true);
        chip.setTextColor(tone);
        chip.setBackgroundResource(R.drawable.s_pill);
        chip.setBackgroundTintList(ColorStateList.valueOf(surface));
        chip.setPadding(dp(12), dp(6), dp(14), dp(6));
        chip.setGravity(android.view.Gravity.CENTER_VERTICAL);
        chip.setSingleLine(true);
        if (icon != 0) {
            chip.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
            chip.setCompoundDrawablePadding(dp(6));
            chip.setCompoundDrawableTintList(ColorStateList.valueOf(tone));
        }
        return chip;
    }

    protected void addWithTopMargin(LinearLayout parent, View child, int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(margin);
        parent.addView(child, params);
    }
}
