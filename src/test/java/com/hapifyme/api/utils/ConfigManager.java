package com.hapifyme.api.utils;

public final class ConfigManager {

    private ConfigManager() {}

    public static final String BASE_URL = "https://test.hapifyme.com/api";

    public static final String REGISTER = BASE_URL + "/user/register.php";
    public static final String CONFIRM_STATUS = BASE_URL + "/user/retrieve_token.php?username_or_email=";
    public static final String CONFIRM_EMAIL = BASE_URL + "/user/confirm_email.php?token=";
    public static final String LOGIN = BASE_URL + "/user/login.php";
    public static final String GET_PROFILE = BASE_URL + "/user/get_profile.php?user_id=";
    public static final String UPDATE_PROFILE = BASE_URL + "/user/update_profile.php";
    public static final String DELETE_PROFILE = BASE_URL + "/user/delete_profile.php";

}
