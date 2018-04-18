package macrodes.lab.com.quack.sqlitedatabasetools;

import android.provider.BaseColumns;

/**
 * Created by Botchway on 3/17/2018.
 */

public final class QuackContract  {


    public static final class QuackContacts{
        //declare table names for creation
        public static final String USER_ID = "userId";
        public static final String USER_EMAIL = "email";
        public static final String USER_ABOUT_ME = "aboutMe";
        public static final String USER_PHONE_NUMBER = "phoneNumber";
        public static final String USER_PROFILE_IMAGE_URL = "profileImage";
        public static final String USER_PROFILE_DOWNLOADED = "FALSE";
        public static final String USER_LOCAL_PROFILE_IMAGE_NAME = "";
    }
}
