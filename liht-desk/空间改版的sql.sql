#1.删除空间下创建的条目、目录、文件夹
delete from dbcool.two_dimension where type in (151200,151300,152500);


#2.将151900,151400转移到home下
select * from dbcool.two_dimension where type in (151900,151400);

update dbcool.two_dimension a inner join (SELECT * FROM dbcool.two_dimension where type = 152511) b on a.first_ID = b.first_ID set a.type = a.type + 100001 ,a.first_ID = b.second_ID where a.type in (151900,151400);

#delete from dbcool.two_dimension where type in (151900,151400)
#3.将151901,151401,152501,151201,151301转移到home下

update dbcool.two_dimension a inner join (SELECT * FROM dbcool.two_dimension where type = 152511) b on a.first_ID = b.first_ID set a.type = a.type + 100000 ,a.first_ID = b.second_ID 
where a.type in (151901,151401,152501,151201,151301);

#delete from dbcool.two_dimension where type in (151901,151401,152501,151201,151301);

#4.将151902,151402,152502,151202,151302转移到index下
select * from dbcool.two_dimension where type in (151902,151402,152502,151202,151302);

update dbcool.two_dimension a inner join (SELECT * FROM dbcool.two_dimension where type = 152512) b on a.first_ID = b.first_ID set a.type = a.type + 99999 ,a.first_ID = b.second_ID 
where a.type in (151902,151402,152502,151202,151302);

#delete from dbcool.two_dimension where type in (151902,151402,152502,151202,151302)

#5.152503创建三维关系homeId-homeId-00，删除这组152503,152504

select first_ID,count(*) from dbcool.two_dimension where type = 152503 group by first_ID;

insert into dbcool.three_dimension (three_dimension.type, first_ID, second_ID, third_ID, order_key) select 25252500,b.second_ID, b.second_ID, a.second_ID, a.order_key from dbcool.two_dimension a left join (SELECT * FROM dbcool.two_dimension where type = 152511) b on a.first_ID = b.first_ID where a.type = 152503;

#delete from dbcool.two_dimension where type = 152503

#6.152504,252500应该有相同的数量，均为空间下的子文件夹。根据252500在三维关系中构建信息，最后将这两者都删掉

select * from dbcool.two_dimension where type = 252500;
select * from dbcool.two_dimension where type = 152504;

select 25252500, c.id ,b.second_ID, c.first_ID, c.second_ID, c.order_key from ((select * from dbcool.two_dimension where type = 152504) a inner join (SELECT * FROM dbcool.two_dimension where type = 152511) b on a.first_ID = b.first_ID) inner join (SELECT * FROM dbcool.two_dimension where type = 252500) c on c.second_ID = a.second_ID;

insert into dbcool.three_dimension (three_dimension.type, first_ID, second_ID, third_ID, order_key) select 25252500,b.second_ID, c.first_ID, c.second_ID, c.order_key from ((select * from dbcool.two_dimension where type = 152504) a inner join (SELECT * FROM dbcool.two_dimension where type = 152511) b on a.first_ID = b.first_ID) inner join (SELECT * FROM dbcool.two_dimension where type = 252500) c on c.second_ID = a.second_ID

#delete from dbcool.two_dimension where type in (152504,252500)