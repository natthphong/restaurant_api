package com.prior.restaurant.exception;

import com.prior.restaurant.models.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
@Slf4j
public class ErrorAdviser {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseModel> handleBaseException(BaseException e){
        ResponseModel<Void> responseModel = new ResponseModel<>();
        responseModel.setStatus(417);
        responseModel.setDescription(e.getMessage());
        responseModel.setTimestamp(LocalDate.now());
        return new ResponseEntity<>(responseModel, HttpStatus.EXPECTATION_FAILED);
    }
}
