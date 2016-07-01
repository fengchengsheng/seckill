package org.seckill.service;

import static org.junit.Assert.*;

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

	@Test
	public void testExportSeckillUrl() throws Exception {
		long id =1000L;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		logger.info("exposer={}", exposer);
//		Exposer{exposed=true,md5=37fd7884a075f2a98fd3c605ff8bb04b,seckillId=1000,now=0,start=0,end=0}

	}

	@Test
	public void testExecuteSeckill() throws Exception {
		long id = 1000L;
		long phone = 15357826532L;
		String md5 = "37fd7884a075f2a98fd3c605ff8bb04b";
		try {
			SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
			logger.info("seckillExecution={}", seckillExecution);
		} catch (RepeatKillException e) {
			logger.error(e.getMessage());
		} catch (SeckillCloseException e) {
			logger.error(e.getMessage());
		}
		
		/*
10:54:55.292 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Creating a new SqlSession
10:54:55.310 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.318 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@1a06f52] will be managed by Spring
10:54:55.324 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0; 
10:54:55.354 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2016-07-01 10:54:55.278(Timestamp), 2016-07-01 10:54:55.278(Timestamp)
10:54:55.356 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
10:54:55.356 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.356 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805] from current transaction
10:54:55.356 [main] DEBUG o.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed(seckill_id,user_phone,state) values (?,?,0) 
10:54:55.357 [main] DEBUG o.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 15357826532(Long)
10:54:55.504 [main] DEBUG o.s.d.S.insertSuccessKilled - <==    Updates: 1
10:54:55.515 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.516 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805] from current transaction
10:54:55.517 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==>  Preparing: select sk.seckill_id, sk.user_phone, sk.create_time, sk.state, s.name "seckill.name", s.number "seckill.number", s.start_time "seckill.start_time", s.end_time "seckill.end_time", s.create_time "seckill.create_time" from success_killed sk inner join seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? and sk.user_phone=? 
10:54:55.518 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==> Parameters: 1000(Long), 15357826532(Long)
10:54:55.530 [main] DEBUG o.s.d.S.queryByIdWithSeckill - <==      Total: 1
10:54:55.536 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.537 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.537 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.537 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e01805]
10:54:55.583 [main] INFO  o.seckill.service.SeckillServiceTest - seckillExecution=SeckillExecution{seckillId=1000,state=1,stateInfo=秒杀成功,successKilled=SuccessKilled{seckillId=1000,userPhone=15357826532,state=0,createTime=Fri Jul 01 10:54:55 CST 2016}}

		 */
	}

}
