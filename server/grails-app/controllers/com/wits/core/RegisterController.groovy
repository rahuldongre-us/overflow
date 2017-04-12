package com.wits.core

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.wits.sec.*;
import org.springframework.beans.BeanWrapper
import org.springframework.beans.PropertyAccessorFactory


@Secured('permitAll')
class RegisterController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {

         grailsApplication.controllerClasses.each {
             println it
             if(it=='Register'){
               it.getURIs().each {uri ->
                 println  "${it.logicalPropertyName}.${it.getMethodActionName(uri)}"
               }
             }
   }
 
        params.max = Math.min(max ?: 10, 100)
        respond Register.list(params), model:[registerCount: Register.count()]
    }

    def show(Register register) {
        respond register
    }

    def upload() {
        println " ---------------- " + params
    }

     def handleFileUpload(MultipartFile file) {
        println " working --- " + params
        try {
            println " 1 " + file.getBytes()
            println " 2 " + file.getOriginalFilename()  
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }      
    }

    @Transactional
    def save(Register register) {
        if (register == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (register.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond register.errors, view:'create'
            return
        }

        register.save flush:true

        def registerUser = new User(username: register.email, password: register.password).save(flush:true)
        UserRole.create registerUser, Role.findByAuthority("ROLE_USER"), true

        respond register, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Register register) {
        if (register == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (register.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond register.errors, view:'edit'
            return
        }

        register.save flush:true

        respond register, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Register register) {

        if (register == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        register.delete flush:true

        render status: NO_CONTENT
    }
}
