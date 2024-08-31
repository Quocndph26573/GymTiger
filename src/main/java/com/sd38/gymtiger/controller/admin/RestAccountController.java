
package com.sd38.gymtiger.controller.admin;

import com.sd38.gymtiger.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tiger/account")
public class RestAccountController {

    @Autowired
    private ProductDetailService productDetailService;

}
