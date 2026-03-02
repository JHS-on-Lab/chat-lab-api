package me.son.chatlabapi.global.response;

import lombok.Getter;
import me.son.chatlabapi.global.exception.ErrorCode;

@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;

    private ApiResponse(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getCode(), errorCode.getMessage());
    }

    public static ApiResponse<Void> failure(String errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }
}
