package com.payo.assignment.utils;

import android.util.Log;

import com.payo.assignment.data.TransactionType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = Utils.class.getName();

    private static final String[] SUSPICIOUS_WORDS = {"can", "could", "will", "enter", "would", "won",
            "win", "click", "link", "congrat", "eligible"};
    private static final String[] DEBIT_INDICATORS = {"debited", "send", "sent", "paid", "deducted"};
    private static final String[] CREDIT_INDICATORS = {"credited", "received", "receive", "added"};

    private static final String ACCOUNT_REGEX = "(?:account|a/c|acct)";
    private static final String CREDIT_DEBIT_REGEX = "(?:credit|debit|received|sent)";
    private static final String AMOUNT_REGEX_1 = "(?:inr|rs)+[\\\\.]?[\\\\s]*[0-9+[\\\\,]?[0-9]+]+[\\\\.]*[0-9]+";
    private static final String AMOUNT_REGEX_2 = "[0-9+[\\\\,]*+[0-9]*]*[\\\\.]*[0-9]+[\\\\s]*(?:inr|rs)+";

    public static boolean isValidMessage(String body, String sender) {
        return containsRegex(body, ACCOUNT_REGEX) && containsRegex(body, CREDIT_DEBIT_REGEX) &&
                (containsRegex(body, AMOUNT_REGEX_1) || containsRegex(body, AMOUNT_REGEX_2)) &&
                !containSuspiciousWords(body);
    }

    public static boolean equalsRegex(String body, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(body).matches();
    }

    public static boolean containsRegex(String body, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(body).find();
    }

    public static boolean containSuspiciousWords(String body) {
        boolean contains = false;
        body = body.toLowerCase();
        for (String word : SUSPICIOUS_WORDS) {
            contains = contains || body.contains(word);
        }
        return contains;
    }

    public static boolean isCredit(String body) {
        boolean result = false;
        body = body.toLowerCase();
        for (String word : CREDIT_INDICATORS) {
            result = result || body.contains(word);
        }
        return result;
    }

    public static boolean isDebit(String body) {
        boolean result = false;
        body = body.toLowerCase();
        for (String word : DEBIT_INDICATORS) {
            result = result || body.contains(word);
        }
        return result;
    }

    public static double getAmount(String body) {
        double amt = 0;
        body = body.toLowerCase();
        Pattern pattern1 = Pattern.compile(AMOUNT_REGEX_1, Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(body);
        Pattern pattern2 = Pattern.compile(AMOUNT_REGEX_2, Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(body);
        String amount = "";

        if (matcher1.find()) {
            amount = matcher1.group().replaceAll("inr", "");
        } else if (matcher2.find()) {
            amount = matcher2.group().replaceAll("inr", "");
        }
        amount = amount.replaceAll("rs", "");
        amount = amount.replaceAll("inr", "");
        amount = amount.replaceAll(" ", "");
        amount = amount.replaceAll(",", "");

        while (amount.charAt(0) == '.')
            amount = amount.substring(1);
        while (amount.charAt(amount.length() - 1) == '.')
            amount = amount.substring(0, amount.length() - 1);
        try {
            amt = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            amt = 0;
            Log.e(TAG, e.toString());
        }

        return amt;
    }

    public static String getMessageType(String body) {
        if (isCredit(body) && !isDebit(body)) {
            return TransactionType.CREDIT.name();
        } else if (isDebit(body) && !isCredit(body)) {
            return TransactionType.DEBIT.name();
        } else {
            return TransactionType.UNDEFINED.name();
        }
    }
}
