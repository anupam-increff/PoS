package com.increff.pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "System Information")
@RestController
public class AboutController {

    @ApiOperation("Get application information")
    @RequestMapping(path = "/api/about", method = RequestMethod.GET)
    public String about() {
        return "POS Application v1.0";
    }
}
