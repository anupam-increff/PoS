package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderSearchForm {
    private String query;
    @NotNull(message = "Start date is required")
    private ZonedDateTime startDate;
    @NotNull(message = "End date is required")
    private ZonedDateTime endDate;
    private int page;
    private int size;
}
