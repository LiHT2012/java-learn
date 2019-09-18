# coding=utf-8
from sys import argv

#需要计算的data表传过来，然后怎么处理路径问题？存在服务器里 vs 在ethercalc redis中的能否直接访问？
#字典的路径后边需要改
dictionaryPath = "/home/liht/datas/original/liht_prod_page_four.xlsx"
dataPath = "/home/liht/datas/original/" + argv[1] + (".xlsx")

columns = []
for i in range(2,len(argv)):
    columns.append(int(argv[i])) 

import pandas as pd
#需要安装python-pandas
#sudo apt-get install python-pandas
#sheetname可以不指定
dictionary = pd.read_excel(dictionaryPath)#,sheet_name="sheet1"
data = pd.read_excel(dataPath,sheet_name="sheet1")
#print(dictionaryPath,students.dtypes)
#print(scores.dtypes)

# 思路：将字典表和数据表联合起来，即可查到每个公司对应的数据，关联的列 是 证券简称 列
# tables = students.merge(scores)    # 如果不设置连接的方式，则默认时内连接

# 要求把字典表的所有公司都显示出来，包括那些没有数据的学生:用左连接 how="left"
tables = dictionary.merge(data,how="left",on=u"证券简称")

# 把没有匹配到的NaN 改为 0
tables = dictionary.merge(data,how="left",on=u"证券简称").fillna(0)

#需要安装python-numpy
#sudo apt-get install python-nump
import numpy as np

#values是显示的列，注意如果是数字，则直接写即可，字符串需加""
#若是汉字，则需有u，如下 index=[u"服务类型"]
#显示的列，先读取数据，然后取第3～n列？？
test=pd.pivot_table(tables, index=[u"服务类型"], values=columns, aggfunc=[np.sum],fill_value=0)

#可以不指定sheetname，默认生成的是Sheet1
test.to_excel("/home/liht/datas/result/"+argv[1]+".xlsx")#,sheet_name='sheetaaaaa')
