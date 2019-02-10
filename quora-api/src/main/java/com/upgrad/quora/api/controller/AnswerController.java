package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = null;
        UserEntity userEntity = userBusinessService.getUserFromAuth(authorization);
        QuestionEntity questionEntity = questionBusinessService.validateQuestion(questionId);

        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUser(userEntity);

        AnswerEntity createdAnswer = answerService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).
                    status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId,
                                                            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserEntity userEntity = userBusinessService.getUserFromAuth(authorization);
        AnswerEntity answerEntity = answerService.getAnswerFromId(answerId);
        AnswerEntity checkedAnswer = answerService.checkAnswerBelongToUser(userEntity,answerEntity);

        checkedAnswer.setAnswer(answerEditRequest.getContent());
        AnswerEntity updatedAnswer = answerService.updateAnswer(checkedAnswer);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswer.getUuid()).status("ANSWER EDITED");


        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId,
                                                            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserEntity userEntity = userBusinessService.getUserFromAuth(authorization);
        AnswerEntity answerEntity = answerService.getAnswerFromId(answerId);
        AnswerEntity checkedAnswer;

        if(userEntity.getRole().equalsIgnoreCase("admin")) {
            checkedAnswer = answerEntity;
        }
        else {
            checkedAnswer = answerService.checkAnswerBelongToUser(userEntity, answerEntity);
        }
        AnswerEntity deletedAnswer = answerService.deleteAnswer(checkedAnswer);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswer.getUuid())
                .status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("qusetionId") final String questionId,
                                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity = userBusinessService.getUserFromAuth(authorization);
        QuestionEntity questionEntity = questionBusinessService.validateQuestion(questionId);

        ArrayList<AnswerDetailsResponse> list = null;
        ArrayList<AnswerEntity> rawList = (ArrayList)answerService.getAllAnswers(questionId);

        for(AnswerEntity answer : rawList)
        {
            AnswerDetailsResponse detailsResponse = new AnswerDetailsResponse();
            detailsResponse.setId(answer.getUuid());
            detailsResponse.setAnswerContent(answer.getAnswer());
            detailsResponse.setQuestionContent(questionEntity.getContent());
            list.add(detailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(list,HttpStatus.OK);

    }

}
