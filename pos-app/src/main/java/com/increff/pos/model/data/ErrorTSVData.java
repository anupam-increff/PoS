package com.increff.pos.model.data;

import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorTSVData
{
    private String barcode;
    private String name;
    private String clientName;
    private String mrp;
    private String quantity;
    private String errorMessage;

    public ErrorTSVData(String barcode, String name, String clientName, String mrp, String errorMessage) {
        this.barcode = barcode;
        this.name = name;
        this.clientName = clientName;
        this.mrp = mrp;
        this.errorMessage = errorMessage;
    }

    public ErrorTSVData(String barcode, String quantity, String errorMessage) {
        this.barcode = barcode;
        this.quantity = quantity;
        this.errorMessage = errorMessage;
    }

    public ErrorTSVData(String name, String errorMessage) {
        this.name = name;
        this.errorMessage = errorMessage;
    }

    public static ErrorTSVData fromForm(Object form, String errorMessage) {
        if (form instanceof ProductForm) {
            ProductForm pf = (ProductForm) form;
            return new ErrorTSVData(pf.getBarcode(), pf.getName(), pf.getClientName(), pf.getMrp() != null ? pf.getMrp().toString() : "", errorMessage);
        } else if (form instanceof InventoryForm) {
            InventoryForm inf = (InventoryForm) form;
            return new ErrorTSVData(inf.getBarcode(), inf.getQuantity() != null ? inf.getQuantity().toString() : "", errorMessage);
        } else {
            return new ErrorTSVData("", errorMessage);
        }
    }
} 