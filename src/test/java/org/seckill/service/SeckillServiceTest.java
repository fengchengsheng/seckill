package org.seckill.service;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:spring/spring-dao.xml",
	"classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() throws Exception {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}", list);	//这里的{}是占位符，list会打印在里面
	}

	@Test
	public void testGetById() throws Exception {
		long id = 1000L;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}", seckill);
	}

	//集成测试代码完整逻辑，注意可重复执行
	@Test
	public void testSeckillLogic() throws Exception {
		long id =1000L;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if (exposer.isExposed()) {
			logger.info("exposer={}", exposer);
			long phone = 15357826530L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
				logger.info("seckillExecution={}", seckillExecution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			} catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		} else {
			//秒杀未开启
			logger.warn("exposer={}", exposer);
		}
	}
	
	//普通秒杀
	@Test
	public void testExecuteSeckill() throws Exception{
		long seckillId = 1000L;
		long phone = 15255114307L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
				logger.info(execution.getStateInfo());
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
			
		} else {
			//秒杀未开启
			logger.warn("exposer={}", exposer);
		}
	}
	//测试五次计时：0.722s,0.503s,0.411s,0.564s,0.465s
	
	//存储过程秒杀
	@Test
	public void testExcuteSeckillProcedure() {
		long seckillId = 1000L;
		long phone = 15355114301L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			String md5 = exposer.getMd5();
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
			logger.info(execution.getStateInfo());
		} else {
			//秒杀未开启
			logger.warn("exposer={}", exposer);
		}
	}
	//测试五次计时：0.641s,0.455s,0.458s,0.439s,0.417s
	//两种方法平均时间差距不大，本机无网络延迟，本测试中gc也可忽略不计，所以优化效果不明显
}
