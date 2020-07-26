package fu.is1304.dv.fptsocial.common;

public class Const {
    public static final int REQUEST_CODE_LOGIN = 1001;
    public static final int REQUEST_CODE_CHOSE_AVA = 1002;
    public static final int REQUEST_CODE_CHOSE_STATUS_IMAGE = 1003;
    public static final int RESULT_CODE_COMPLETE = 101;
    public static final int RESULT_CODE_FAIL = 100;


    public static final String USER_COLLECTION = "users";
    public static final String POST_COLLECTION = "posts";
    public static final String COUNT_COLLECTION = "counts";
    public static final String NOTIFICATION_COLLECTION = "notifications";
    public static final String MESSAGE_COLLECTION = "messages";
    public static final String FRIEND_COLLECTION = "friends";
    public static final String COMMENT_COLLECTION = "comments";
    public static final String POST_LIKE_COLLECTION = "post_like";


    public static final String MODE_CREATE_PROFILE = "CREATE_PROFILE";
    public static final String MODE_UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final int MODE_CREATE_STATUS = 0;
    public static final int MODE_UPDATE_STATUS = 1;

    public static final String VALIDATE_CODE_EMPTY = "REQUIRED_FIELD_EMPTY";
    public static final String VALIDATE_CODE_NOT_MATCH = "NOT_MATCH";
    public static final String VALIDATE_CODE_EMAIL_INCORRECT = "EMAIL_INCORRECT";
    public static final String VALIDATE_CODE_PASS_INCORRECT = "PASSWORD_INCORRECT";
    public static final String VALIDATE_CODE_CORRECT = "CORRECT";

    public static final String LOGIN_INFO_REFERENCE = "LOGIN_INFO";

    public static final int NUMBER_ITEMS_OF_NEW_FEED = 3;
    public static final int NUMBER_ITEMS_OF_NOTIFICATION = 10;


    public static final int NEXT_PAGE_CASE = 1;
    public static final int PREV_PAGE_CASE = -1;
    public static final int RELOAD_PAGE_CASE = 0;

    public static final String MESSAGE_NOTIFICATION_TITLE = "Message";
    public static final String POST_NOTIFICATION_TITLE = "News";

    public static final int POST_NOTIFY_ID = 100001;
    public static final int MESSAGE_NOTIFY_ID = 100001;

    public static final String CHANEL_NAME = "notification_chanel";
    public static final String CHANEL_DESCRIPTION = "chanel for notification of FPTSocial";
    public static final String CHANEL_ID = "notification_chanel";
}
