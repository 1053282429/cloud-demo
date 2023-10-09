package org.example.po;


import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * status enum
 *
 * @author kouwenhao
 */
public enum Status {

    SUCCESS(0, "success", "成功"),

    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal Server Error", "服务端异常"),

    SERVER_INSTANCE_REGISTER_FAILED(110000, "service instance register failed", "实例注册失败"),
    SERVER_INSTANCE_IS_NOT_EXIST(110001, "service instance is not exist", "实例不存在"),
    SERVER_INSTANCE_KEEPALIVE_FAILED(110002, "service instance keepalive failed", "实例保活失败"),

    ;

    private final int code;
    private final String enMsg;
    private final String zhMsg;

    Status(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

    public String getZhMsg() {
        return this.zhMsg;
    }

    public String getEnMsg() {
        return this.enMsg;
    }
}
