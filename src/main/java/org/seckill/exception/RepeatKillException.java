package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常)
 * Created By FCS on 2016年6月30日
 *
 */
@SuppressWarnings("serial")
public class RepeatKillException extends SeckillException{

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepeatKillException(String message) {
		super(message);
	}
	
}
