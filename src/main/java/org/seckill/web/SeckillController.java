package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //@Service @Component
@RequestMapping("/seckill") //url:/模块{id}/细分  /seckill/list
public class SeckillController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public String list(Model model){
		//获取列表页
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		//list.jsp + model = ModelAndView
		return "list";	// /WEB-INF/jsp/"list".jsp
	}
	
	@RequestMapping(value = "{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId,Model model){
		if (seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	
	//ajax json
	@RequestMapping(value = "/{seckillId}/exposer",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})//指定contentType和字符集编码
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId)  {
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/{seckillId}/{md5}/execution",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")Long seckillId,
			@PathVariable("md5")String md5,
			@CookieValue(value = "killPhone")Long phone){
		//在校验参数较多的情况下可用spring validation
		if (phone == null) {
			return new SeckillResult<SeckillExecution>(false, "未注册");
		}
		//普通秒杀
//		try {
//			SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
//			return new SeckillResult<SeckillExecution>(true, execution);
//		} catch (RepeatKillException e) {
//			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
//			return new SeckillResult<SeckillExecution>(true, execution);
//		} catch (SeckillCloseException e) {
//			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
//			return new SeckillResult<SeckillExecution>(true, execution);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
//			return new SeckillResult<SeckillExecution>(true, execution);
//		}
		
		//存储过程执行秒杀
		SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
		return new SeckillResult<SeckillExecution>(true, execution);
	}
	
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }
	
}
