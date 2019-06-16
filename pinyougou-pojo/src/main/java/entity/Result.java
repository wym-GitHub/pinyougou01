package entity;

import java.io.Serializable;

public class Result implements Serializable {

    private boolean success;
    private String massage;

    public Result(boolean success, String massage) {
        this.success = success;
        this.massage = massage;
    }

    public Result() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
