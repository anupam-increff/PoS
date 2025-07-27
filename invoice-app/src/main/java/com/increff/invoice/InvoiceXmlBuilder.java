package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceXmlBuilder {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static String build(OrderData order, List<OrderItemData> items) {
        double grandTotal = items.stream().mapToDouble(i -> i.getSellingPrice() * i.getQuantity()).sum();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");

        // Page setup
        xml.append("  <fo:layout-master-set>\n");
        xml.append("    <fo:simple-page-master master-name=\"invoice-page\" page-height=\"29.7cm\" page-width=\"21cm\" margin=\"2cm\">\n");
        xml.append("      <fo:region-body margin-top=\"1cm\" margin-bottom=\"1.5cm\"/>\n");
        xml.append("      <fo:region-before extent=\"1cm\"/>\n");
        xml.append("      <fo:region-after extent=\"1.5cm\"/>\n");
        xml.append("    </fo:simple-page-master>\n");
        xml.append("  </fo:layout-master-set>\n");

        xml.append("  <fo:page-sequence master-reference=\"invoice-page\">\n");

        // Footer
        xml.append("    <fo:static-content flow-name=\"xsl-region-after\">\n");
        xml.append("      <fo:block text-align=\"center\" font-size=\"7pt\" font-family=\"Arial, sans-serif\" color=\"#666666\" border-top=\"0.5pt solid #000000\" padding-top=\"6pt\" margin-top=\"12pt\">\n");
        xml.append("        Thank you for your business\n");
        xml.append("      </fo:block>\n");
        xml.append("      <fo:block text-align=\"center\" font-size=\"6pt\" color=\"#666666\" margin-top=\"3pt\">\n");
        xml.append("        Page <fo:page-number/> of <fo:page-number-citation ref-id=\"last-page\"/>\n");
        xml.append("      </fo:block>\n");
        xml.append("    </fo:static-content>\n");

        xml.append("    <fo:flow flow-name=\"xsl-region-body\">\n");

        // Company Header - Simple and Professional
        xml.append("      <fo:block text-align=\"center\" border-bottom=\"2pt solid #000000\" padding-bottom=\"10pt\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:block font-size=\"20pt\" font-weight=\"bold\" font-family=\"Arial, sans-serif\" margin-bottom=\"3pt\">INCREFF</fo:block>\n");
        xml.append("        <fo:block font-size=\"9pt\" color=\"#666666\">3rd Floor, The Hub Unit 1, Sarjapur-Marathahalli Rd, Bellandur</fo:block>\n");
        xml.append("        <fo:block font-size=\"9pt\" color=\"#666666\">Bengaluru, Karnataka 560103</fo:block>\n");
        xml.append("        <fo:block font-size=\"9pt\" color=\"#666666\" margin-top=\"2pt\">Email: sales@increff.com</fo:block>\n");
        xml.append("      </fo:block>\n");

        // Invoice Title and Details
        xml.append("      <fo:block text-align=\"center\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:block font-size=\"16pt\" font-weight=\"bold\" margin-bottom=\"6pt\">INVOICE</fo:block>\n");
        xml.append("        <fo:block font-size=\"10pt\" margin-bottom=\"2pt\">Order Number: ").append(String.format("%06d", order.getId())).append("</fo:block>\n");
        xml.append("        <fo:block font-size=\"10pt\" margin-bottom=\"2pt\">Date: ").append(order.getTime().format(DATE_FMT)).append("</fo:block>\n");
        xml.append("        <fo:block font-size=\"10pt\">Time: ").append(order.getTime().format(TIME_FMT)).append("</fo:block>\n");
        xml.append("      </fo:block>\n");

        // Items Table
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" border=\"1pt solid #000000\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:table-column column-width=\"40%\"/>\n");
        xml.append("        <fo:table-column column-width=\"25%\"/>\n");
        xml.append("        <fo:table-column column-width=\"12%\"/>\n");
        xml.append("        <fo:table-column column-width=\"8%\"/>\n");
        xml.append("        <fo:table-column column-width=\"15%\"/>\n");

        // Table Header
        xml.append("        <fo:table-header>\n");
        xml.append("          <fo:table-row background-color=\"#f5f5f5\" border-bottom=\"1pt solid #000000\">\n");
        xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
        xml.append("              <fo:block font-weight=\"bold\" font-size=\"9pt\" text-align=\"center\">DESCRIPTION</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
        xml.append("              <fo:block font-weight=\"bold\" font-size=\"9pt\" text-align=\"center\">BARCODE</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
        xml.append("              <fo:block font-weight=\"bold\" font-size=\"9pt\" text-align=\"center\">PRICE</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
        xml.append("              <fo:block font-weight=\"bold\" font-size=\"9pt\" text-align=\"center\">QTY</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"6pt\">\n");
        xml.append("              <fo:block font-weight=\"bold\" font-size=\"9pt\" text-align=\"center\">TOTAL</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-header>\n");

        // Table Body
        xml.append("        <fo:table-body>\n");
        for (OrderItemData item : items) {
            double total = item.getQuantity() * item.getSellingPrice();

            xml.append("          <fo:table-row border-bottom=\"0.5pt solid #cccccc\">\n");
            xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
            xml.append("              <fo:block font-size=\"8pt\" text-align=\"left\">").append(escape(item.getProductName())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
            xml.append("              <fo:block font-size=\"8pt\" text-align=\"center\" font-family=\"Courier, monospace\">").append(escape(item.getBarcode())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
            xml.append("              <fo:block font-size=\"8pt\" text-align=\"right\">Rs ").append(String.format("%.2f", item.getSellingPrice())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"6pt\" border-right=\"0.5pt solid #cccccc\">\n");
            xml.append("              <fo:block font-size=\"8pt\" text-align=\"center\">").append(item.getQuantity()).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"6pt\">\n");
            xml.append("              <fo:block font-size=\"8pt\" text-align=\"right\">Rs ").append(String.format("%.2f", total)).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("          </fo:table-row>\n");
        }
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        // Total Section - Clean and Simple, aligned to right
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" margin-top=\"12pt\">\n");
        xml.append("        <fo:table-column column-width=\"65%\"/>\n");
        xml.append("        <fo:table-column column-width=\"35%\"/>\n");
        xml.append("        <fo:table-body>\n");
        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:block>&#xA0;</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:table table-layout=\"fixed\" width=\"100%\" border=\"1pt solid #000000\">\n");
        xml.append("                <fo:table-column column-width=\"60%\"/>\n");
        xml.append("                <fo:table-column column-width=\"40%\"/>\n");
        xml.append("                <fo:table-body>\n");
        xml.append("                  <fo:table-row background-color=\"#f5f5f5\">\n");
        xml.append("                    <fo:table-cell padding=\"8pt\" border-right=\"0.5pt solid #000000\">\n");
        xml.append("                      <fo:block font-size=\"10pt\" font-weight=\"bold\" text-align=\"center\">TOTAL</fo:block>\n");
        xml.append("                    </fo:table-cell>\n");
        xml.append("                    <fo:table-cell padding=\"8pt\">\n");
        xml.append("                      <fo:block font-size=\"10pt\" font-weight=\"bold\" text-align=\"right\">Rs ").append(String.format("%.2f", grandTotal)).append("</fo:block>\n");
        xml.append("                    </fo:table-cell>\n");
        xml.append("                  </fo:table-row>\n");
        xml.append("                </fo:table-body>\n");
        xml.append("              </fo:table>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        // Add invisible block for page numbering reference
        xml.append("      <fo:block id=\"last-page\"></fo:block>\n");

        xml.append("    </fo:flow>\n");
        xml.append("  </fo:page-sequence>\n");
        xml.append("</fo:root>\n");

        return xml.toString();
    }

    private static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}