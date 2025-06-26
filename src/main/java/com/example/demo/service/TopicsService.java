package com.example.demo.service;

import com.example.demo.dto.TopicsDTO;
import com.example.demo.dto.WeatherCityDTO;
import com.example.demo.dto.extended.ExtendedTopicsDTO;
import com.example.demo.filter.TopicsFilter;
import com.example.demo.model.Topics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public interface TopicsService {
    Page<TopicsDTO> findAll(TopicsFilter filter, Pageable pageable);

    TopicsDTO saveTopics(TopicsDTO topics);

    void deleteTopics(Integer id);

    TopicsDTO updateTopics(Integer id, TopicsDTO topics);

    TopicsDTO patchTopics(TopicsFilter topicsFilter, Integer id);

    TopicsDTO findById(Integer id);

    ExtendedTopicsDTO findByIdExtended(Integer id);

    ResponseEntity<?> getWeatherInCity(String city);
}
