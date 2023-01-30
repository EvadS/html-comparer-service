package com.se.sample.config;

public class ProjectConstants {

    public final static String OLD_DIFFERENCE_FORMAT = "<span style=\"color:green;\">%s</span>";
    public final static String NEW_DIFFERENCE_FORMAT = "<span style=\"color:green;\">%s</span>";

    public static final String PATH_PREFIX = "/api/v1";
    public static final String COMPARISON_API_PATH = "/comparison";
    public static final String UPLOAD_API_PATH = "/upload";

    public static final String HTML_DOWNLOAD_API_PATH = "/download";
    public static final String HTML_DOWNLOAD_FILE_BY_NAME_API_PATH =  "/{file-name}";
    public static final String HTML_DOWNLOAD_OLD_API_PATH = "/{operation-id}/old";
    public static final String URL_SEPARATOR = "/";
    public static final String OPERATION_ID_PARAM = "operation-id";
    public static final String OPERATION_STATUS_API = "/status";


    private ProjectConstants() {
    }
}