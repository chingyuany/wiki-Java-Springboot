package com.alanyang.wiki.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("Login name already exists"),
    LOGIN_USER_ERROR("Login name not exist or password error"),
    VOTE_REPEAT("You already clicked like button"),
    ;

    private String desc;
//BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);  就會把描述存起來 但描述已經預寫好了
    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
