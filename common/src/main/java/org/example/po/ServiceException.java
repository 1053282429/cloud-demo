package org.example.po;
import java.text.MessageFormat;


/**
 * @author 10532
 */
public class ServiceException extends RuntimeException {

    /**
     * code
     */
    private Integer code;

    private String zhMsg;

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    private String enMsg;

    public ServiceException() {
    }

    public ServiceException(Status status) {
        super(status.getMsg());
        this.code = status.getCode();
        this.zhMsg = status.getZhMsg();
        this.enMsg = status.getEnMsg();
    }

    public ServiceException(Status status, Object... args) {
        super(status.getMsg());
        this.code = status.getCode();
        this.zhMsg = MessageFormat.format(status.getZhMsg(), args);
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message) {
        super(message);
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getZhMsg() {
        return zhMsg;
    }

    public void setZhMsg(String zhMsg) {
        this.zhMsg = zhMsg;
    }
}
