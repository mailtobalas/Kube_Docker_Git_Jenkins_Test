package com.docker.Controller;

import org.springframework.web.bind.annotation.*;

@RestController


public class ctrlClassfile {

   // @GetMapping (value="/pushtokafka")
   @RequestMapping(value = "/pushtokafka", method = RequestMethod.GET)
    public  String PostMessage()
    {

       return "test docker Bala";

    }
}
