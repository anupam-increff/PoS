package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PaginationUtil;
import com.increff.pos.util.TSVUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto extends AbstractDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public void addProduct(ProductForm form) {
        checkValid(form);
        ProductPojo pojo = ConvertUtil.convert(form, ProductPojo.class);
        productFlow.addProduct(pojo, form.getClientName());
    }

    public PaginatedResponse<ProductData> getAll(Integer page, Integer pageSize) {
        List<ProductPojo> products = productService.getAll(page, pageSize);
        Long totalProducts = productService.countAll();
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(productDataList, page, pageSize, totalProducts);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, Integer page, Integer pageSize) {
        List<ProductPojo> products = productFlow.getProductsByAClient(clientName, page, pageSize);
        Long totalProducts = productFlow.countProductsByAClient(clientName);
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(productDataList, page, pageSize, totalProducts);
    }

    public PaginatedResponse<ProductData> searchByBarcode(String barcode, Integer page, Integer pageSize) {
        List<ProductPojo> products = productService.searchByBarcode(barcode, page, pageSize);
        Long totalProducts = productService.countSearchByBarcode(barcode);
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(productDataList, page, pageSize, totalProducts);
    }

    public ProductData getByBarcode(String barcode) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        return pojoToData(product);
    }

    public void update(Integer id, ProductForm form) {
        ProductPojo pojo = ConvertUtil.convert(form, ProductPojo.class);
        productFlow.updateProduct(id, pojo, form.getClientName());
    }

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        return TSVUploadUtil.processTSVUpload(
                file,
                ProductForm.class,
                form -> {
                    ProductPojo pojo = ConvertUtil.convert(form, ProductPojo.class);
                    productFlow.addProduct(pojo, form.getClientName());
                }
        );
    }

    private ProductData pojoToData(ProductPojo product) {
        ClientPojo client = clientService.getCheckClientById(product.getClientId());
        ProductData data = ConvertUtil.convert(product, ProductData.class);
        data.setClientName(client.getName());
        return data;
    }
}
