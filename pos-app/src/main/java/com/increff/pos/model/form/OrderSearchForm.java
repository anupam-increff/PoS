package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderSearchForm {
    private String startDate;
    private String endDate;
    private Boolean invoiceGenerated;
    private int page;
    private int size;
}
