package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity , UserAuthEntity userAuthEntity) throws  AuthorizationFailedException {

       if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        return questionDao.createQuestion(questionEntity);
    }


    @Transactional
    public QuestionEntity deleteQuestion(String questionid , UserAuthEntity userAuthEntity) throws AuthorizationFailedException, InvalidQuestionException {

       UserEntity userEntity = userAuthEntity.getUser();
       QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionid);

       if (userAuthEntity.getLogoutAt()!=null){
           throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
       }else if (userEntity.getRole().equals("nonadmin") && !userEntity.getId().equals(questionEntity.getUser())){
           throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question" );
       }else if (questionEntity==null){
           throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
       }
      return questionDao.deleteQuestion(questionEntity);
    }

}
