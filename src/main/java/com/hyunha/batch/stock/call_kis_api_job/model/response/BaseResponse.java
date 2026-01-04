package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseResponse {


    /**
     * 성공/실패 여부 (String, 1)
     * - 보통 "0" 이 성공, 그 외는 실패(실제 사용 중인 API 스펙에 맞춰 판단)
     */
    @JsonProperty("rt_cd")
    private String resultCode;

    /**
     * 응답 코드 (String, 8)
     */
    @JsonProperty("msg_cd")
    private String messageCode;

    /**
     * 응답 메시지 (String, 80)
     */
    @JsonProperty("msg1")
    private String message;

}
