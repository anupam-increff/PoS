package com.increff.pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
@RestController
@RequestMapping("/api/about")
@Api(tags = "About API")
public class AboutController {

    @ApiOperation(value = "Returns basic application information")
    @GetMapping
    public AboutAppData getDetails() {
        AboutAppData data = new AboutAppData();
        data.setName("Increff POS");
        data.setVersion("1.0.0");
        return data;
    }
    @Getter @Setter
    public static class AboutAppData {
        private String name;
        private String version;
    }
}
