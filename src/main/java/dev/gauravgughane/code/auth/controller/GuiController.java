package dev.gauravgughane.code.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuiController {

    @GetMapping("/")
    public String home() {
        return "redirect:/gui.html";
    }
    
}
