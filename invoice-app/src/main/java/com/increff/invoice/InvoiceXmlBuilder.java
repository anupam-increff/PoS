
package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceXmlBuilder {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Brand colors matching frontend
    private static final String PRIMARY_COLOR = "#e8f2ff";
    private static final String SECONDARY_COLOR = "#f0f7ff";
    private static final String ACCENT_COLOR = "#3b82f6";
    private static final String LIGHT_BLUE = "#dbeafe";
    private static final String TEXT_DARK = "#1f2937";
    private static final String TEXT_LIGHT = "#6b7280";
    private static final String LIGHT_BG = "#f8fafc";
    private static final String BORDER_COLOR = "#e5e7eb";
    private static final String WHITE = "#ffffff";

    public static String build(OrderData order, List<OrderItemData> items) {
        double grandTotal = items.stream().mapToDouble(i -> i.getSellingPrice() * i.getQuantity()).sum();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");

        // Page setup
        xml.append("  <fo:layout-master-set>\n");
        xml.append("    <fo:simple-page-master master-name=\"invoice-page\" page-height=\"29.7cm\" page-width=\"21cm\" margin=\"1.5cm\">\n");
        xml.append("      <fo:region-body margin-top=\"0.8cm\" margin-bottom=\"1cm\"/>\n");
        xml.append("      <fo:region-before extent=\"0.8cm\"/>\n");
        xml.append("      <fo:region-after extent=\"1cm\"/>\n");
        xml.append("    </fo:simple-page-master>\n");
        xml.append("  </fo:layout-master-set>\n");

        xml.append("  <fo:page-sequence master-reference=\"invoice-page\">\n");

        // Header (optional)
        xml.append("    <fo:static-content flow-name=\"xsl-region-before\">\n");
        xml.append("      <fo:block text-align=\"right\" font-size=\"8pt\" color=\"").append(TEXT_LIGHT).append("\">Page <fo:page-number/></fo:block>\n");
        xml.append("    </fo:static-content>\n");

        // Footer with page info
        xml.append("    <fo:static-content flow-name=\"xsl-region-after\">\n");
        xml.append("      <fo:block text-align=\"center\" font-size=\"8pt\" font-family=\"Arial, sans-serif\" color=\"").append(TEXT_LIGHT).append("\" border-top=\"0.5pt solid ").append(BORDER_COLOR).append("\" padding-top=\"6pt\">\n");
        xml.append("        Thank you for choosing Increff | Page <fo:page-number/> of <fo:page-number-citation ref-id=\"last-page\"/>\n");
        xml.append("      </fo:block>\n");
        xml.append("    </fo:static-content>\n");

        xml.append("    <fo:flow flow-name=\"xsl-region-body\">\n");

        // Header with logo and invoice title
        xml.append("      <fo:block-container background-color=\"").append(PRIMARY_COLOR).append("\" padding=\"12pt\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:block>\n");
        xml.append("          <fo:table table-layout=\"fixed\" width=\"100%\">\n");
        xml.append("            <fo:table-column column-width=\"15%\"/>\n");
        xml.append("            <fo:table-column column-width=\"55%\"/>\n");
        xml.append("            <fo:table-column column-width=\"30%\"/>\n");
        xml.append("            <fo:table-body>\n");
        xml.append("              <fo:table-row>\n");

        // Logo
        xml.append("                <fo:table-cell display-align=\"center\">\n");
        xml.append("                  <fo:block-container width=\"40pt\" height=\"40pt\">\n");
        xml.append("                    <fo:block>\n");
        xml.append("                      <fo:external-graphic src=\"url('logo.png')\" content-width=\"40pt\" content-height=\"40pt\"/>\n");
        xml.append("                    </fo:block>\n");
        xml.append("                  </fo:block-container>\n");
        xml.append("                </fo:table-cell>\n");

        // Company name
        xml.append("                <fo:table-cell display-align=\"center\" padding-left=\"12pt\">\n");
        xml.append("                  <fo:block font-size=\"16pt\" font-weight=\"bold\" color=\"").append(TEXT_DARK).append("\" font-family=\"Arial, sans-serif\">INCREFF</fo:block>\n");
        xml.append("                  <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\">Incredible Efficiency</fo:block>\n");
        xml.append("                </fo:table-cell>\n");

        // Invoice label (smaller)
        xml.append("                <fo:table-cell text-align=\"right\" display-align=\"center\">\n");
        xml.append("                  <fo:block font-size=\"16pt\" font-weight=\"500\" color=\"").append(ACCENT_COLOR).append("\">INVOICE</fo:block>\n");
        xml.append("                </fo:table-cell>\n");

        xml.append("              </fo:table-row>\n");
        xml.append("            </fo:table-body>\n");
        xml.append("          </fo:table>\n");
        xml.append("        </fo:block>\n");
        xml.append("      </fo:block-container>\n");

        // FROM & Invoice metadata (Invoice ₹ + Date + Time)
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:table-column column-width=\"55%\"/>\n");
        xml.append("        <fo:table-column column-width=\"45%\"/>\n");
        xml.append("        <fo:table-body>\n");
        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:block font-size=\"10pt\" font-weight=\"bold\" color=\"").append(ACCENT_COLOR).append("\">FROM</fo:block>\n");
        xml.append("              <fo:block font-size=\"11pt\" font-weight=\"bold\" color=\"").append(TEXT_DARK).append("\">Increff</fo:block>\n");
        xml.append("              <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\">3rd Floor, The Hub Unit 1, Sarjapur-Marathahalli Rd, Bellandur, Bengaluru, Karnataka 560103</fo:block>\n");
        xml.append("              <fo:block font-size=\"9pt\" color=\"").append(ACCENT_COLOR).append("\">sales@increff.com</fo:block>\n");
        xml.append("            </fo:table-cell>\n");

        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\">Invoice ₹").append(String.format("%06d", order.getId())).append("</fo:block>\n");
        xml.append("              <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\">Date: ").append(order.getTime().format(DATE_FMT)).append("</fo:block>\n");
        xml.append("              <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\">Time: ").append(order.getTime().format(TIME_FMT)).append("</fo:block>\n");
        xml.append("            </fo:table-cell>\n");

        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");
        // Compact Items Section
        xml.append("      <fo:block font-size=\"12pt\" font-weight=\"bold\" color=\"").append(TEXT_DARK).append("\" margin-bottom=\"8pt\" border-bottom=\"1pt solid ").append(ACCENT_COLOR).append("\" padding-bottom=\"4pt\">ITEMS</fo:block>\n");

        // Refined Items Table
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" border=\"1pt solid ").append(BORDER_COLOR).append("\" margin-bottom=\"16pt\">\n");
        xml.append("        <fo:table-column column-width=\"35%\"/>\n");
        xml.append("        <fo:table-column column-width=\"25%\"/>\n");
        xml.append("        <fo:table-column column-width=\"15%\"/>\n");
        xml.append("        <fo:table-column column-width=\"10%\"/>\n");
        xml.append("        <fo:table-column column-width=\"15%\"/>\n");

        // Compact Table Header
        xml.append("        <fo:table-header>\n");
        xml.append("          <fo:table-row background-color=\"").append(LIGHT_BLUE).append("\">\n");
        xml.append("            <fo:table-cell padding=\"8pt 10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\">\n");
        xml.append("              <fo:block font-weight=\"600\" font-size=\"9pt\" color=\"").append(TEXT_DARK).append("\" text-transform=\"uppercase\">Description</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"8pt 10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\">\n");
        xml.append("              <fo:block font-weight=\"600\" font-size=\"9pt\" color=\"").append(TEXT_DARK).append("\" text-transform=\"uppercase\">Barcode</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"8pt 10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" text-align=\"right\">\n");
        xml.append("              <fo:block font-weight=\"600\" font-size=\"9pt\" color=\"").append(TEXT_DARK).append("\" text-transform=\"uppercase\">Price</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"8pt 10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" text-align=\"center\">\n");
        xml.append("              <fo:block font-weight=\"600\" font-size=\"9pt\" color=\"").append(TEXT_DARK).append("\" text-transform=\"uppercase\">Qty</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell padding=\"8pt 10pt\" text-align=\"right\">\n");
        xml.append("              <fo:block font-weight=\"600\" font-size=\"9pt\" color=\"").append(TEXT_DARK).append("\" text-transform=\"uppercase\">Total</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-header>\n");

        // Compact Table Body
        xml.append("        <fo:table-body>\n");
        int rowIndex = 0;
        for (
        OrderItemData item : items) {
            double total = item.getQuantity() * item.getSellingPrice();
            String bgColor = (rowIndex % 2 == 0) ? WHITE : SECONDARY_COLOR;

            xml.append("          <fo:table-row background-color=\"").append(bgColor).append("\">\n");
            xml.append("            <fo:table-cell padding=\"10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" border-top=\"1pt solid ").append(BORDER_COLOR).append("\">\n");
            xml.append("              <fo:block font-size=\"10pt\" font-weight=\"500\" color=\"").append(TEXT_DARK).append("\">").append(escape(item.getProductName())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" border-top=\"1pt solid ").append(BORDER_COLOR).append("\">\n");
            xml.append("              <fo:block font-size=\"9pt\" color=\"").append(TEXT_LIGHT).append("\" font-family=\"Courier, monospace\">").append(escape(item.getBarcode())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" border-top=\"1pt solid ").append(BORDER_COLOR).append("\" text-align=\"right\">\n");
            xml.append("              <fo:block font-size=\"10pt\" color=\"").append(TEXT_DARK).append("\">₹").append(String.format("%.2f", item.getSellingPrice())).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"10pt\" border-right=\"1pt solid ").append(BORDER_COLOR).append("\" border-top=\"1pt solid ").append(BORDER_COLOR).append("\" text-align=\"center\">\n");
            xml.append("              <fo:block font-size=\"10pt\" font-weight=\"600\" color=\"").append(ACCENT_COLOR).append("\">").append(item.getQuantity()).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("            <fo:table-cell padding=\"10pt\" border-top=\"1pt solid ").append(BORDER_COLOR).append("\" text-align=\"right\">\n");
            xml.append("              <fo:block font-size=\"11pt\" font-weight=\"600\" color=\"").append(TEXT_DARK).append("\">₹").append(String.format("%.2f", total)).append("</fo:block>\n");
            xml.append("            </fo:table-cell>\n");
            xml.append("          </fo:table-row>\n");
            rowIndex++;
        }
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

        // Compact Total Section
        xml.append("      <fo:table table-layout=\"fixed\" width=\"100%\" margin-top=\"12pt\">\n");
        xml.append("        <fo:table-column column-width=\"65%\"/>\n");
        xml.append("        <fo:table-column column-width=\"35%\"/>\n");
        xml.append("        <fo:table-body>\n");
        xml.append("          <fo:table-row>\n");
        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:block>&#xA0;</fo:block>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("            <fo:table-cell>\n");
        xml.append("              <fo:block-container background-color=\"").append(ACCENT_COLOR).append("\" padding=\"14pt\" border=\"1pt solid ").append(ACCENT_COLOR).append("\">\n");
        xml.append("                <fo:block>\n");
        xml.append("                  <fo:table table-layout=\"fixed\" width=\"100%\">\n");
        xml.append("                    <fo:table-column column-width=\"60%\"/>\n");
        xml.append("                    <fo:table-column column-width=\"40%\"/>\n");
        xml.append("                    <fo:table-body>\n");
        xml.append("                      <fo:table-row>\n");
        xml.append("                        <fo:table-cell>\n");
        xml.append("                          <fo:block font-size=\"12pt\" font-weight=\"bold\" color=\"white\">TOTAL</fo:block>\n");
        xml.append("                        </fo:table-cell>\n");
        xml.append("                        <fo:table-cell text-align=\"right\">\n");
        xml.append("                          <fo:block font-size=\"16pt\" font-weight=\"bold\" color=\"white\">₹").append(String.format("%.2f", grandTotal)).append("</fo:block>\n");
        xml.append("                        </fo:table-cell>\n");
        xml.append("                      </fo:table-row>\n");
        xml.append("                    </fo:table-body>\n");
        xml.append("                  </fo:table>\n");
        xml.append("                </fo:block>\n");
        xml.append("              </fo:block-container>\n");
        xml.append("            </fo:table-cell>\n");
        xml.append("          </fo:table-row>\n");
        xml.append("        </fo:table-body>\n");
        xml.append("      </fo:table>\n");

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