package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceXmlBuilder {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String build(OrderData order, List<OrderItemData> items) {
        double subtotal = items.stream().mapToDouble(i -> i.getSellingPrice() * i.getQuantity()).sum();
        double tax = subtotal * 0.10;
        double grandTotal = subtotal + tax;

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");

        xml.append("  <fo:layout-master-set>\n");
        xml.append("    <fo:simple-page-master master-name=\"A4\" page-height=\"29.7cm\" page-width=\"21cm\" margin=\"2cm\">\n");
        xml.append("      <fo:region-body/>\n");
        xml.append("    </fo:simple-page-master>\n");
        xml.append("  </fo:layout-master-set>\n");

        xml.append("  <fo:page-sequence master-reference=\"A4\">\n");
        xml.append("    <fo:flow flow-name=\"xsl-region-body\">\n");

        // Header
        xml.append("      <fo:block font-size=\"16pt\" font-weight=\"bold\" space-after=\"4pt\">Increff</fo:block>\n");
        xml.append("      <fo:block font-size=\"10pt\">3rd Floor, The Hub Unit 1, Sarjapur - Marathahalli Rd, Bellandur, Bengaluru, Karnataka 560103</fo:block>\n");
        xml.append("      <fo:block font-size=\"10pt\" space-after=\"10pt\">sales@increff.com</fo:block>\n");

        // Divider
        xml.append("      <fo:block border-bottom=\"1pt solid #999999\" margin-bottom=\"10pt\"/>\n");

        // Order Info in two columns
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\">\n");
        xml.append("        <fo:table-column column-width=\"50%\"/>\n");
        xml.append("        <fo:table-column column-width=\"50%\"/>\n");
        xml.append("        <fo:table-body>\n");
        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell><fo:block font-weight=\"bold\">Invoice #</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell><fo:block>").append(order.getId()).append("</fo:block></fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell><fo:block font-weight=\"bold\">Date</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell><fo:block>").append(order.getTime().format(DATE_FMT)).append("</fo:block></fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        xml.append("      <fo:block space-before=\"12pt\" font-size=\"12pt\" font-weight=\"bold\">Items</fo:block>\n");

        // Items Table
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" border=\"0.5pt solid #333\" font-size=\"10pt\">\n");
        xml.append("        <fo:table-column column-width=\"35%\"/>\n");
        xml.append("        <fo:table-column column-width=\"20%\"/>\n");
        xml.append("        <fo:table-column column-width=\"15%\"/>\n");
        xml.append("        <fo:table-column column-width=\"10%\"/>\n");
        xml.append("        <fo:table-column column-width=\"20%\"/>\n");

        xml.append("        <fo:table-header background-color=\"#f0f0f0\">\n");
        xml.append("          <fo:table-row font-weight=\"bold\">\n");
        xml.append("            <fo:table-cell border=\"0.5pt solid #999\" padding=\"4pt\"><fo:block>Description</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell border=\"0.5pt solid #999\" padding=\"4pt\"><fo:block>Barcode</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell border=\"0.5pt solid #999\" padding=\"4pt\"><fo:block>Unit Price</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell border=\"0.5pt solid #999\" padding=\"4pt\"><fo:block>Qty</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell border=\"0.5pt solid #999\" padding=\"4pt\"><fo:block>Total</fo:block></fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-header>\n");

        xml.append("        <fo:table-body>\n");
        for (OrderItemData d : items) {
            double total = d.getQuantity() * d.getSellingPrice();
            xml.append("          <fo:table-row>\n");
            xml.append("            <fo:table-cell border=\"0.5pt solid #ccc\" padding=\"4pt\"><fo:block>").append(escape(d.getProductName())).append("</fo:block></fo:table-cell>\n");
            xml.append("            <fo:table-cell border=\"0.5pt solid #ccc\" padding=\"4pt\"><fo:block>").append(escape(d.getBarcode())).append("</fo:block></fo:table-cell>\n");
            xml.append("            <fo:table-cell border=\"0.5pt solid #ccc\" padding=\"4pt\"><fo:block>").append(String.format("%.2f", d.getSellingPrice())).append("</fo:block></fo:table-cell>\n");
            xml.append("            <fo:table-cell border=\"0.5pt solid #ccc\" padding=\"4pt\"><fo:block>").append(d.getQuantity()).append("</fo:block></fo:table-cell>\n");
            xml.append("            <fo:table-cell border=\"0.5pt solid #ccc\" padding=\"4pt\"><fo:block>").append(String.format("%.2f", total)).append("</fo:block></fo:table-cell>\n");
            xml.append("          </fo:table-row>\n");
        }
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        // Totals box
        xml.append("      <fo:block space-before=\"16pt\" font-size=\"12pt\" font-weight=\"bold\">Total Summary</fo:block>\n");
        xml.append("      <fo:table width=\"100%\" table-layout=\"fixed\">\n");
        xml.append("        <fo:table-column column-width=\"70%\"/>\n");
        xml.append("        <fo:table-column column-width=\"30%\"/>\n");
        xml.append("        <fo:table-body>\n");

        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell><fo:block>&#xA0;</fo:block></fo:table-cell>\n");
        xml.append("            <fo:table-cell background-color=\"#f9f9f9\" border=\"0.5pt solid #666\" padding=\"6pt\">\n");
        xml.append("              <fo:block>Subtotal: ").append(String.format("%.2f", subtotal)).append("</fo:block>\n");
        xml.append("              <fo:block>Tax (10%): ").append(String.format("%.2f", tax)).append("</fo:block>\n");
        xml.append("              <fo:block font-weight=\"bold\">Grand Total: ").append(String.format("%.2f", grandTotal)).append("</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");


        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        // Footer
        xml.append("      <fo:block space-before=\"24pt\" font-style=\"italic\" text-align=\"center\" font-size=\"10pt\">Thank you for your business!</fo:block>\n");

        xml.append("    </fo:flow>\n");
        xml.append("  </fo:page-sequence>\n");
        xml.append("</fo:root>\n");

        return xml.toString();
    }

    private static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
