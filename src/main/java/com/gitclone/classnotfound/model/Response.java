package com.gitclone.classnotfound.model;

public class Response<T>  {
	 private int code = 200;

	    private String message = "Success";

	    private T data;

	    public int getCode() {
	        return code;
	    }

	    public void setCode(int code) {
	        this.code = code;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    public T getData() {
	        return data;
	    }

	    public void setData(T data) {
	        this.data = data;
	    }

	    @Override
	    public String toString() {
	        return "Response{" +
	                "code=" + code +
	                ", message='" + message + '\'' +
	                ", data=" + data +
	                '}';
	    }

}
