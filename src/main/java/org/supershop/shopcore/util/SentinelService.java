package org.supershop.shopcore.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class SentinelService {

    @ResponseBody
    @RequestMapping("hello")
    public String hello() {
        String result;
        try (Entry entry = SphU.entry("HelloResource")) {
            /* Start of flow control */
            result = "Hello Sentinel";
            return result;
            /* End of flow control */
        } catch (BlockException e) {
            // Handles blocked requests.
            log.error(e.toString());
            result = "System busy, please try again later.";
            return result;
        }
    }

    @PostConstruct
    public void seckillsFlow() {
        // Initialize list for flow control rules.
        List<FlowRule> rules = new ArrayList<>();

        // Rule 1: limit QPS of requests for seckill resources.
        FlowRule rule = new FlowRule();
        rule.setResource("seckills");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);

        // Rule 2: hello testing.
        FlowRule rule2 = new FlowRule();
        rule2.setResource("HelloResource");
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setCount(2);

        // Load flow control rules.
        rules.add(rule);
        rules.add(rule2);
        FlowRuleManager.loadRules(rules);
    }
}
