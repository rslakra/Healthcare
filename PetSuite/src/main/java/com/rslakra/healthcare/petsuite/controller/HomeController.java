package com.rslakra.healthcare.petsuite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class HomeController {

    /**
     *
     */
    @Autowired
    public HomeController() {
    }

    /**
     * @param principal
     * @return
     */
    @GetMapping({"/", "index", "home"})
    public String homePage(Principal principal) {
        return "index";
    }

    /**
     * @return
     */
    @GetMapping({"/about-us"})
    public String aboutUs() {
        return "views/about-us";
    }

    /**
     * @return
     */
    @GetMapping({"/contact-us"})
    public String contactUs() {
        return "views/contact-us";
    }

}
