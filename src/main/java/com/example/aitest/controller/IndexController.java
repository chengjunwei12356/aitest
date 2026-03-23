package com.example.aitest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 */
@Controller
public class IndexController {

    /**
     * 首页（工作台）
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 用户管理
     */
    @GetMapping("/user")
    public String user() {
        return "user/index";
    }

    /**
     * 角色管理
     */
    @GetMapping("/role")
    public String role() {
        return "role/index";
    }

    /**
     * 申请管理（占位）
     */
    @GetMapping("/application")
    public String application() {
        return "placeholder";
    }

    /**
     * 审批管理（占位）
     */
    @GetMapping("/approval")
    public String approval() {
        return "placeholder";
    }

    /**
     * 合同管理（占位）
     */
    @GetMapping("/contract")
    public String contract() {
        return "placeholder";
    }

    /**
     * 放款管理（占位）
     */
    @GetMapping("/loan")
    public String loan() {
        return "placeholder";
    }

    /**
     * 还款管理（占位）
     */
    @GetMapping("/repayment")
    public String repayment() {
        return "placeholder";
    }

    /**
     * 预警管理（占位）
     */
    @GetMapping("/warning")
    public String warning() {
        return "placeholder";
    }

    /**
     * 贷后管理（占位）
     */
    @GetMapping("/postloan")
    public String postloan() {
        return "placeholder";
    }

    /**
     * 流程管理（占位）
     */
    @GetMapping("/workflow")
    public String workflow() {
        return "placeholder";
    }

    /**
     * 报表管理（占位）
     */
    @GetMapping("/report")
    public String report() {
        return "placeholder";
    }
}
