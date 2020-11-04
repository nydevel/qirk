package org.wrkr.clb.common.util.web;

public class FrontURI {

    public static final String ACTIVATE_EMAIL_TOKEN = "/register?code=";
    public static final String RESET_PASSWORD = "/reset-password/";
    public static final String ACCEPT_INVITE = "/accept-email-invite?invite_key=";
    public static final String DECLINE_INVITE = "/decline-email-invite?invite_key=";
    public static final String MANAGE_SUBSCRIPTIONS = "/manage-subscriptions/";
    public static final String GET_TASK = "/project/%s/task/%d";

    public static String generateGetTaskURI(String remoteHost, String projectUiId, Long taskNumber) {
        return remoteHost + String.format(FrontURI.GET_TASK, projectUiId, taskNumber.toString());
    }
}
