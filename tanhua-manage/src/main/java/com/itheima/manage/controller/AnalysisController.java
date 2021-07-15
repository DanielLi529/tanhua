package com.itheima.manage.controller;


import com.itheima.manage.service.AnalysisService;
import com.itheima.manage.vo.AnalysisSummaryVo;
import com.itheima.manage.vo.AnalysisUsersVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 概要统计分析
 */
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 概要统计分析
     */
    @GetMapping("/summary")
    public ResponseEntity summary() {
        AnalysisSummaryVo analysisSummaryVo = analysisService.summary();
        return ResponseEntity.ok(analysisSummaryVo);
    }

    /**
    * @Desc: 新增、活跃用户、次日留存率
    * @Param: [sd, ed, type]
    * @return: com.itheima.manage.vo.AnalysisUsersVo
    */
    @GetMapping("/users")
    public AnalysisUsersVo getUsers(@RequestParam(name = "sd") Long sd
            , @RequestParam("ed") Long ed
            , @RequestParam("type") Integer type) {
        return this.analysisService.queryAnalysisUsersVo(sd, ed, type);
    }
}
