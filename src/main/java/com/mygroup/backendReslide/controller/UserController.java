package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.UpdateUserRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.IncorrectCurrentPasswordException;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;
    private final GenericResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody UserRequest userRequest){
        try {
            this.userService.createUser(userRequest);
            return new ResponseEntity(responseService.buildInformation("User created successfully."), HttpStatus.CREATED);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }
        catch (UsernameExistsException | IndividualCodeExistsException | IndividualTypeNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String username){
        try{
            return new ResponseEntity(userService.search(username), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity updateCurrentUser(@RequestBody UpdateUserRequest userRequest){
        try {
            this.userService.updateCurrentUser(userRequest);
            return new ResponseEntity(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }
        catch (IncorrectCurrentPasswordException | UsernameNotFoundException | UsernameExistsException | IndividualCodeExistsException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{username}")
    public ResponseEntity updateUser(@PathVariable String username, @RequestBody UpdateUserRequest userRequest){
        try{
            this.userService.updateUser(username, userRequest);
            return new ResponseEntity(this.responseService.buildInformation("Updated"), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{username}")
    public ResponseEntity getUser(@PathVariable String username){
        try{
            return new ResponseEntity(this.userService.search(username), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get")
    public ResponseEntity getUserInformation(){
        try{
            return new ResponseEntity(this.userService.getUserInformation(), HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/switchStatus")
    public ResponseEntity switchStatus(@RequestBody UserRequest userRequest){
        try{
            this.userService.switchStatus(userRequest);
            return new ResponseEntity(this.responseService.buildInformation("Status changed"), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }
        catch (UsernameNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/switchRole")
    public ResponseEntity switchRole(@RequestBody UserRequest userRequest){
        try{
            this.userService.switchRole(userRequest);
            return new ResponseEntity(this.responseService.buildInformation("Status changed"), HttpStatus.OK);
        }catch(UserNotAuthorizedException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
