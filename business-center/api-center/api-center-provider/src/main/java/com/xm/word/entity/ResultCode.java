package com.xm.word.entity;

public enum ResultCode {
    OK("200", "成功"),
    PARAM_NOT_MATCH("400", "参数不匹配"),
    UNAUTHORIZED("401", "请先登录"),
    ACCESS_DENIED("403", "权限不足"),
    REQUEST_NOT_FOUND("404", "请求不存在"),
    HTTP_BAD_METHOD("405", "请求方式不支持"),
    SYSTEM_ERROR("500", "请求异常，请稍后再试"),
    PARAM_NOT_NULL("400", "参数不能为空"),
    USER_DISABLED("403", "当前用户已被锁定，请联系管理员解锁"),
    PARAM_DENIED("401", "拒绝访问"),
    USERNAME_PASSWORD_ERROR("5001", "用户名或密码错误"),
    TOKEN_EXPIRED("5002", "token 已过期，请重新登录"),
    TOKEN_PARSE_ERROR("5002", "token 解析失败，请尝试重新登录"),
    TOKEN_OUT_OF_CTRL("5003", "当前用户已在别处登录，请尝试更改密码或重新登录");

    private final String code;
    private final String message;

    public static boolean isOk(String code) {
        return OK.getCode().equals(code);
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ResultCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}