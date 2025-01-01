package org.supershop.shopcore.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supershop.shopcore.service.SeckillActivityService;
import org.supershop.shopcore.service.SeckillOverSellService;

import javax.annotation.Resource;

@Controller
public class SeckillOverSellController {

//    @Resource
//    private SeckillOverSellService seckillOverSellService;

    @Resource
    private SeckillActivityService seckillActivityService;

    /* METHOD DEPRECATED */
//    /**
//     * Provide web response.
//     * @return web response body
//     */
//    @ResponseBody
//    @RequestMapping("/seckill/{seckillActivityId}")
//    public String seckill(@PathVariable long seckillActivityId) {
//        return seckillOverSellService.processSeckill(seckillActivityId);
//    }

    /**
     * Process seckill request using Lua program.
     * @return
     */
    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckillCommodity(@PathVariable long seckillActivityId) {
        boolean stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
        return stockValidateResult ? "Success! Order has been created." : "Item is out of stock!";
    }
}
