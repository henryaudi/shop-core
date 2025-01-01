package org.supershop.shopcore.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supershop.shopcore.service.SeckillOverSellService;

@Controller
public class SeckillOverSellController {

    @Autowired
    private SeckillOverSellService seckillOverSellService;

    /**
     * Provide web response.
     * @return web response body
     */
    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckill(@PathVariable long seckillActivityId) {
        return seckillOverSellService.processSeckill(seckillActivityId);
    }
}
