package com.example.jeylnastoninfer.debug7;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

public class EditMode{

    private final int defaultFontSizeSp = 16;
    private final int defaultFontColor = Color.rgb(0xff, 0xff, 0xff);
    private int fontSizeSp;
    private boolean isUnderline;
    private boolean isStrikeThrough;
    private boolean isBold;
    private boolean isItalic;
    private int fontColor;
    private SpannableString tmpSS = null;
    private int tmpCount = 0;

    public EditMode(){
        this.fontSizeSp = defaultFontSizeSp;
        this.isUnderline = false;
        this.isStrikeThrough = false;
        this.isBold = false;
        this.isItalic = false;
        this.fontColor = defaultFontColor;
    }

    public EditMode(int color, int fontSize, int encryption){
        this.fontColor = color;
        this.fontSizeSp = fontSize;
        this.isUnderline = (encryption & 1) == 1;
        this.isStrikeThrough = ((encryption >> 1) & 1) == 1;
        this.isBold = ((encryption >> 2) & 1) == 1;
        this.isItalic = ((encryption >> 3) & 1) == 1;
    }

    public int getFontSizeSp(){
        return fontSizeSp;
    }

    public void setFontSizeSp(int newFontSizeSp){
        this.fontSizeSp = newFontSizeSp;
    }

    public boolean isUnderline(){
        return this.isUnderline;
    }

    public void setUnderline(boolean value){
        this.isUnderline = value;
    }

    public boolean isStrikeThrough(){
        return this.isStrikeThrough;
    }

    public void setStrikeThrough(boolean value){
        this.isStrikeThrough = value;
    }

    public boolean isBold(){
        return this.isBold;
    }

    public void setBold(boolean value){
        this.isBold = value;
    }

    public boolean isItalic(){
        return this.isItalic;
    }

    public void setItalic(boolean value){
        this.isItalic = value;
    }

    public int getFontColor(){
        return this.fontColor;
    }

    public void setFontColor(int newFontColor){
        this.fontColor = newFontColor;
    }

    public int getEncryption(){
        return (isUnderline ? 1 : 0)  + (isStrikeThrough ? 1 : 0) * (1 << 1) +
                (isBold ? 1 : 0) * (1 << 2) + (isItalic ? 1 : 0) * (1 << 3);
    }

    private void applyForeground(){
        tmpSS.setSpan(new ForegroundColorSpan(fontColor), 0, tmpCount, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyFontSize(){
        tmpSS.setSpan(new AbsoluteSizeSpan(fontSizeSp, true), 0, tmpCount, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyUnderline(){
        if(!isUnderline) return;
        tmpSS.setSpan(new UnderlineSpan(), 0, tmpCount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyStrikeThrough(){
        if(!isStrikeThrough) return;
        tmpSS.setSpan(new StrikethroughSpan(), 0, tmpCount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyBoldItalic(){
        if(!(isBold | isItalic)) return;
        int typeface = 0;
        if(isBold & isItalic) typeface = Typeface.BOLD_ITALIC;
        else typeface = isBold ? Typeface.BOLD : Typeface.ITALIC;
        tmpSS.setSpan(new StyleSpan(typeface), 0, tmpCount, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void applyFormat(SpannableString ss, int count){
        tmpSS = ss;
        tmpCount = count;

        applyForeground();
        applyFontSize();
        applyUnderline();
        applyStrikeThrough();
        applyBoldItalic();
    }

    public static void applyFormat(SpannableString ss, int count, int color, int fontSize, int encryption){
        EditMode tmpEditMode = new EditMode(color, fontSize, encryption);
        tmpEditMode.applyFormat(ss, count);
    }

}