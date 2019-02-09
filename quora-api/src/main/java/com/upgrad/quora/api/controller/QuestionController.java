package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserAuthBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private UserAuthBusinessService userAuthBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException {

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);

        final ZonedDateTime now = ZonedDateTime.now();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUser(userAuthEntity.getUser());
        questionEntity.setDate(now);

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity , userAuthEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE , path = "/question/delete/{questionId}" ,  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionid") final String questionid )
         throws AuthorizationFailedException , InvalidQuestionException {

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);


        QuestionEntity deletedQuestion = questionBusinessService.deleteQuestion(questionid, userAuthEntity);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);

    }

}
