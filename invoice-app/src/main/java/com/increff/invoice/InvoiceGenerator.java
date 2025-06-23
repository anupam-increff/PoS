package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;

import org.apache.fop.apps.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Base64;
import java.util.List;

public class InvoiceGenerator {
    private static final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

    public static String generate(OrderData order, List<OrderItemData> items) throws Exception {
        File xslt = new File(InvoiceGenerator.class.getResource("/invoice-template.xsl").toURI());

        String xml = InvoiceXmlBuilder.build(order, items);
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, pdfStream);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer(new StreamSource(xslt));

        Source src = new StreamSource(xmlStream);
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);

        return Base64.getEncoder().encodeToString(pdfStream.toByteArray());
    }
}
