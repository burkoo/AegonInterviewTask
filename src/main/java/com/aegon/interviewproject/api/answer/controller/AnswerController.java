package com.aegon.interviewproject.api.answer.controller;

import com.aegon.interviewproject.api.answer.controller.dto.AnswerDTO;
import com.aegon.interviewproject.api.answer.controller.dto.AnswerResultDTO;
import com.aegon.interviewproject.api.answer.controller.mapper.AnswerMapper;
import com.aegon.interviewproject.api.answer.controller.mapper.AnswerResultMapper;
import com.aegon.interviewproject.api.answer.repository.domain.Answer;
import com.aegon.interviewproject.api.answer.service.AnswerService;
import com.aegon.interviewproject.api.survey.repository.domain.Survey;
import com.aegon.interviewproject.api.survey.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AnswerResultMapper answerResultMapper;

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    private Answer add(@RequestBody AnswerDTO answerDTO){
        Answer answer = answerService.save(answerMapper.toEntity(answerDTO));
        // npm operations
        Survey survey = surveyService.findById(answer.getSurvey().getId());
        if(answer.getScore() <= 6){
            survey.setDetractors(survey.getDetractors()+1);
        } else if(answer.getScore() >= 9){
            survey.setPromoters(survey.getPromoters()+1);
        } else {
            survey.setPassives(survey.getPassives()+1);
        }
        double promoters = (double) survey.getPromoters();
        double detractors = (double) survey.getDetractors();
        double passives = (double) survey.getPassives();
        survey.setScore(((promoters-detractors)/(promoters+detractors+passives))*100);
        surveyService.update(survey);
        return answer;
    }

    // using querydsl
    @GetMapping("/list/{topicId}")
    @ResponseStatus(HttpStatus.OK)
    private List<AnswerResultDTO> list(@PathVariable int topicId){
        Survey survey = surveyService.findById(topicId);
        List<Answer> answerList = answerService.findByTopicId(survey.getId());
        List<AnswerResultDTO> result = new ArrayList<>();
        for(Answer answer : answerList){
            result.add(answerResultMapper.toDTO(answer));
        }
        return result;
    }

    @GetMapping("/list2/{topicId}")
    @ResponseStatus(HttpStatus.OK)
    private List<AnswerResultDTO> list2(@PathVariable int topicId){
        Survey survey = surveyService.findById(topicId);
        List<Answer> answerList = answerService.findBySurvey(survey);
        List<AnswerResultDTO> result = new ArrayList<>();
        for(Answer answer : answerList){
            result.add(answerResultMapper.toDTO(answer));
        }
        return result;
    }
}
