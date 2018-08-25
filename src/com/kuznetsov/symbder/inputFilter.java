package com.kuznetsov.symbder;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

class inputFilter extends DocumentFilter {

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        for (char c : text.toCharArray()) {
            if (!((c >= '0' && c <= '9') || (c == '-') || (c == '.'))) {
                return; //filter out all inputs except 0-9, '-' and '.'
            }
        }
        super.replace(fb, offset, length, text, attrs);
    }
}
