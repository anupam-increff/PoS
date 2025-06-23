package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;


import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceXmlBuilder {
    public static String build(OrderData order, List<OrderItemData> items) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<invoice>");
        xml.append("<orderId>").append(order.getId()).append("</orderId>");
        xml.append("<orderTime>")
                .append(order.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .append("</orderTime>");

        xml.append("<items>");
        for (OrderItemData item : items) {
            xml.append("<item>");
            xml.append("<barcode>").append(item.getBarcode()).append("</barcode>");
            xml.append("<name>").append(item.getProductName()).append("</name>");
            xml.append("<quantity>").append(item.getQuantity()).append("</quantity>");
            xml.append("<price>").append(item.getSellingPrice()).append("</price>");
            xml.append("</item>");
        }
        xml.append("</items>");
        xml.append("</invoice>");
        return xml.toString();
    }
}
