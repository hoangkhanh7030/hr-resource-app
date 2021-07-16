package com.ces.intern.hr.resourcing.demo.utils;

public class CSVFile {
    public static final String DATE = "yyyy-MM-dd_HH-mm-ss";
    public static final String HEADER_KEY = "Content-Disposition";
    public static final String CONTENT_TYPE = "text/csv";
    public static final String FILE_TYPE = ".csv";
    public static final String HEADER_VALUE = "attachment; filename=project_";
    public static final String[] CSV_HEADER = {"Project ID", "Name", "Client Name", "Color", "Text Color", "Color Pattern", "Activate"};
    public static final String[] NAME_MAPPING = {"id", "name", "clientName", "color", "textColor", "colorPattern", "isActivate"};

    public static final String[] CSV_HEADER_RESOURCE = {"Name", "Avatar", "Team", "Position"};
    public static final String[] NAME_MAPPING_RESOURCE = {"name", "avatar", "teamId", "positionId"};



    public static final String NO_SELECTED_FILE  = "No selected file to upload! Please do the checking";
    public static final String ERROR = "Error: this is not a CSV file!";
    public static final String FAIL_MESSAGE = "FAIL! -> message = ";
    public static final String UPLOAD_FILE = "Upload File Successfully!";
}
