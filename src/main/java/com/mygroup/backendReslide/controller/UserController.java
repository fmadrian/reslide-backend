package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.notFound.ContactNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/user/")
@RestController
public class UserController {
    private final UserService userService;
    private final GenericResponseService responseService;

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String username){
        try{
            return new ResponseEntity(userService.search(username), HttpStatus.OK);
        }catch(ContactNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
