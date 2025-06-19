package com.increff.pos.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class OrderData {
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime time;
    private String invoicePath;
}