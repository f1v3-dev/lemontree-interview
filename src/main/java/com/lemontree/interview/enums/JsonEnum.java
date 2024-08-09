package com.lemontree.interview.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * ENUM 을 JSON 형태로 반환할 때 사용하는 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public interface JsonEnum {

    String getStatus();

    String getDescription();
}
