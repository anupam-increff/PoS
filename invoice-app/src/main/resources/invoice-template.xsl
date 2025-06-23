<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
    <xsl:template match="/invoice">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="14pt" font-weight="bold" space-after="0.5cm">
                        Invoice for Order <xsl:value-of select="orderId"/>
                    </fo:block>
                    <fo:block>Date: <xsl:value-of select="orderTime"/></fo:block>
                    <fo:block space-before="1cm"/>
                    <fo:table table-layout="fixed" width="100%">
                        <fo:table-header>
                            <fo:table-row font-weight="bold">
                                <fo:table-cell><fo:block>Barcode</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block>Name</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block>Qty</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block>Price</fo:block></fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="items/item">
                                <fo:table-row>
                                    <fo:table-cell><fo:block><xsl:value-of select="barcode"/></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block><xsl:value-of select="name"/></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block><xsl:value-of select="quantity"/></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block><xsl:value-of select="price"/></fo:block></fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
