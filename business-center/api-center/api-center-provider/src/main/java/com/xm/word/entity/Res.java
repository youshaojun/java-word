package com.xm.word.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Res<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String msg;

	private T data;

	protected static <T> Res<T> of(String code, String msg, T data) {
		return new Res<>(code, msg, data);
	}

	public static <T> Res<T> success(T data) {
		return of(ResultCode.OK.getCode(), ResultCode.OK.getMessage(), data);
	}

	public static <T> Res<T> success() {
		return success(null);
	}

	public static <T> Res<T> failure(ResultCode resp) {
		return failure(resp, resp.getMessage());
	}

	public static <T> Res<T> failure(String msg) {
		return failure(ResultCode.SYSTEM_ERROR, msg);
	}

	public static <T> Res<T> failure(ResultCode resp, String msg) {
		return failure(resp.getCode(), msg, null);
	}

	public static <T> Res<T> failure(ResultCode resp, T data) {
		return failure(resp.getCode(), resp.getMessage(), data);
	}

	public static <T> Res<T> failure(String code, String msg, T data) {
		return of(code, msg, data);
	}

}