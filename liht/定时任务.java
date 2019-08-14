https://www.cnblogs.com/lazyInsects/p/8075487.html

1.用@Scheduled注解

@Component//泛指组件，把普通pojo实例化到spring容器中。这样项目启动时将自动开始进行定时任务
public class TestScheduled {

	@Scheduled(cron="*/2 * * * * *", zone="Asia/Shanghai")//每两分钟执行一次
    public Integer calculateYesterDay() {
        System.out.println("caculate statistics started at " + DateUtil.getCurrentDateInFormatModel1());
        return 11;
    }
}


2.使用quartz//待补充

3.crontab：使用crontab可以在指定的时间执行一个shell脚本或者一系列Linux命令。
详解：https://www.cnblogs.com/aminxu/p/5993769.html

crontab -l：查看用户的定时任务列表
crontab -e：修改 crontab 文件. 如果文件不存在会自动创建。 
o crontab -r : 删除 crontab 文件。
o crontab -ir : 删除 crontab 文件前提醒用户。

crontab 文件的格式：
{minute} {hour} {day-of-month} {month} {day-of-week} 	{full-path-to-shell-script} 
{0~59}	 {0~23} {1~31}	       {1~12}  {0~7，周日 0/7}   {要执行的脚本位置或直接的语句,如
curl -d "baseId=581be94c098b099a0029f3cd" http://localhost:8080/dbc/api/trans/abase，表示调用本地接口}
每分钟表示用 *或 */1.  每n××则为 */n.

系统cron设定：/etc/crontab
通过 /etc/crontab 文件，可以设定系统定期执行的任务，当然，要想编辑这个文件，得有root权限。
https://www.cnblogs.com/chen-lhx/p/5996781.html
