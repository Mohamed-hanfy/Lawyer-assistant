package com.mohamed.lawyer.logs;

import org.springframework.stereotype.Component;

@Component
public class LogsMapper {

    public Logs ToLogs(LogsRequest request){
        return Logs.builder()
                .message(request.message())
                .build();
    }

    public LogsResponse toResponse(Logs logs){
        return LogsResponse.builder()
                .id(logs.getId())
                .date(logs.getDate())
                .message(logs.getMessage())
                .build();
    }
}
