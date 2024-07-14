package com.developerspace.filestoragesdk;

import lombok.Data;

/**
 * 没有提供get方法会导致异常：
 * org.springframework.http.converter.HttpMessageNotWritableException:
 * No converter found for return value of type: class com.developerspace.filestoragesdk.Result
 * @param <T>
 */
@Data
public class Result<T> {
    private T data;
    private int code = 0;
    private String msg;
    public Result(){
    }
    public Result(T data){
        this.data = data;
        this.code = 0;
    }
    public Result(int code,T data){
        this.data = data;
        this.code = code;
    }
    public static  Result error(String msg){
        Result<String> result = new Result<>();
        result.code = 1;
        result.msg = msg;
        return result;
    }
}
