package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.ConvertUtil;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClientDto extends BaseDto {

    @Autowired
    private ClientService clientService;

    @Autowired
    private Validator validator;

    public void add(ClientForm form) {
        validateClientForm(form);
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.addClient(pojo);
    }

    public PaginatedResponse<ClientData> getAll(int page, int pageSize) {
        List<ClientPojo> pojos = clientService.getAllClients(page, pageSize);
        long totalItems = clientService.countAll();
        return createPaginatedResponse(pojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList()), page, pageSize, totalItems);
    }

    public PaginatedResponse<ClientData> searchClients(String query, int page, int pageSize) {
        List<ClientPojo> pojos = clientService.searchClients(query, page, pageSize);
        long totalItems = clientService.countByQuery(query);
        return createPaginatedResponse(pojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList()), page, pageSize, totalItems);
    }

    public void update(int id, ClientForm form) {
        validateClientForm(form);
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.update(id, pojo);
    }

    private void validateClientForm(ClientForm form) {
        // First check basic null/empty conditions
        if (form == null) {
            throw new ApiException("Client form cannot be null");
        }
        
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new ApiException("Client name cannot be blank");
        }
        
        // Then run Bean Validation
        Set<ConstraintViolation<ClientForm>> violations = validator.validate(form);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<ClientForm> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new ApiException("Validation failed: " + sb.toString());
        }
    }
}

