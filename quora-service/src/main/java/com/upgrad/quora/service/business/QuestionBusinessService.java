package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, UserAuthEntity userAuthEntity) throws AuthorizationFailedException {


        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        return questionDao.createQuestion(questionEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questionId, UserAuthEntity userAuthEntity) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        UserEntity userEntity = userAuthEntity.getUser();
        Integer userId = questionEntity.getUser().getId();
        String role = userEntity.getRole();

        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        } else if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else if ((role.equals("nonadmin")) || !userEntity.getId().equals(userId)) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return questionDao.deleteQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestion(UserAuthEntity userAuthEntity) throws AuthorizationFailedException {

        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestion();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userId, UserAuthEntity userAuthEntity) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userAuthEntity.getUser();


        if (!userEntity.getUuid().equals(userId)) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        return questionDao.getAllQuestionsByUser(userId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String questionId, UserAuthEntity userAuthEntity , String content) throws AuthorizationFailedException, InvalidQuestionException {

       QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
       /*  Integer authUserId = userAuthEntity.getUser().getId();
        Integer queUserId = questionEntity.getUser().getId();*/
        boolean validuser = compareUser(userAuthEntity, questionEntity);
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        } else if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }else if (validuser==false){
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
                 questionEntity.setContent(content);
        return  questionDao.editQuestion(questionEntity);
    }

    private boolean compareUser(UserAuthEntity authEntity, QuestionEntity queEntity){
        Integer authUserId = authEntity.getUser().getId();
        Integer queUserId = queEntity.getUser().getId();
        if (authUserId == queUserId){
            return true;
        }
        return false;
    }
}

