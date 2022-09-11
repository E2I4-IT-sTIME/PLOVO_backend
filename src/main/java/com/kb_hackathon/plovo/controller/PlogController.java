package com.kb_hackathon.plovo.controller;

import com.kb_hackathon.plovo.service.PlogService;
import com.kb_hackathon.plovo.domain.Mountain;
import com.kb_hackathon.plovo.dto.EndRes;
import com.kb_hackathon.plovo.dto.GetHomeRes;
import com.kb_hackathon.plovo.dto.GetMountainRes;
import com.kb_hackathon.plovo.dto.GetPlovoMountainRes;
import com.kb_hackathon.plovo.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlogController {

    private final PlogService plogService;
    private final MountainService mountainService;

    @GetMapping("/home")
    public GetHomeRes home(){
        return mountainService.home();
    }

    @GetMapping("/plog/recommend")
    public List<GetMountainRes> recommend(){
        return mountainService.recommend();
    }

    @PostMapping("/plog/search")
    public List<Mountain> search(@RequestParam("mName") String mName){
        return mountainService.search(mName);
    }

    @GetMapping("/plog/start")
    public GetPlovoMountainRes plogStart(@RequestParam("mName") String mName){  // 산 선택해서 산이름 넘어오면
        return mountainService.plogStart(mName);
    }

    // 플로보로부터 무게 받아오기 (ex 아두이노)
    @GetMapping("/weight")
    public void plovoWeight(@RequestParam(value = "userRecord_id") Long userRecord_id, @RequestParam(value = "plovo_id") Long plovo_id, @RequestParam(value = "weight") Double weight) {
        plogService.plovoWeight(userRecord_id, plovo_id, weight);
    }

    // 플로보 완료 api (무게 페이지)
    @GetMapping("/auth/plog/weight")
    public Double plovoWeight(@RequestParam(value = "userRecord_id") Long userRecord_id, @RequestParam(value = "time") String time, Authentication authentication) {
        Double weight = plogService.endWeight(userRecord_id, time);
        return weight;
    }

    // 플로보 위치 확인 api
    @GetMapping("/auth/plog/site")
    public String plovoSite(@RequestParam(value = "plovo_id") Long plovo_id) {
        return plogService.plovoSite(plovo_id);
    }

    // 종료 api
    @GetMapping("/auth/plog/end")
    public EndRes end(@RequestParam(value = "userRecord_id") Long userRecord_id) {
        return plogService.end(userRecord_id);
    }
}