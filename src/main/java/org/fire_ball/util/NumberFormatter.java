package org.fire_ball.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    public static final String[] endings = new String[]{
            "K",
            "M",
            "B",
            "T"
    };

    public static String format(double num, int fractionDigits) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.UK);
        formatter.setMaximumFractionDigits(fractionDigits);
        formatter.setMinimumFractionDigits(fractionDigits);

        return formatter.format(num).replace(",", " ");
    }

    public static String format(double num) {
        return format(num, 2);
    }

    public static String format(double num, int prec, int maxWith) {
        int size = String.valueOf(Math.round(num)).length();
        int absSize = String.valueOf(Math.round(Math.abs(num))).length();
        int ending = (absSize - prec - 1) / 3;
        boolean endingNeed = size - prec > 0;
        int resultLength = maxWith - (endingNeed ? endings[ending].length() : 0);
        if (endingNeed) num /= Math.pow(10, (ending + 1) * 3);

        String formattedNumber = String.valueOf(num);

        if (formattedNumber.length() > resultLength)
            formattedNumber = formattedNumber.substring(0, maxWith - (endingNeed ? endings[ending].length() : 0));

        if (formattedNumber.charAt(formattedNumber.length() - 1) == '.')
            formattedNumber = formattedNumber.replace(".", " ");

        return formattedNumber + (endingNeed ? endings[ending] : "");
    }
}

