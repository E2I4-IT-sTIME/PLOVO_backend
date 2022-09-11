package com.kb_hackathon.plovo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class EndRes {

    private String m_name;
    private String date;
    private Float distance;
    private String time;
    private Double weight;
    private List<String> weights; // 6개월치 무게 수치
}