package com.increff.invoice;

import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.List;

public class InvoiceGenerator {

    public static String generate(OrderData order, List<OrderItemData> items) throws Exception {
        String fo = InvoiceXmlBuilder.build(order, items);

        FopFactory fopFactory = FopFactory.newInstance(new java.io.File(System.getProperty("user.dir")).toURI());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(); // No XSLT needed here

        StreamSource foSource = new StreamSource(new StringReader(fo));
        SAXResult result = new SAXResult(fop.getDefaultHandler());

        transformer.transform(foSource, result);

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}
