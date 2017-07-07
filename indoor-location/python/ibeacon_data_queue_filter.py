# -*- coding: utf-8 -*-
"""
Created on Fri Jul 22 15:55:54 2016

@author: vincent
"""

import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import leastsq
import math as Math
import collections as Collections

"""
读取文件
"""
def read_file_data(filename):
    #打开文件
    f = open(filename, "r")
    #加载文件
    ret = np.loadtxt(f)
    f.close()
    return ret

    
    
#def get_ave_data2(array)

    

"""
 先高斯滤波
 求平均值,对原始数据求平均值，原始数据中会有噪声,此数组为为二位数组
"""
def get_ave_data(array):
    #ave_val = np.zeros(length)  #平均值清零
    L1 = array.shape[0]
    L2 = array.shape[1]
    ave_val = np.zeros(L1)
    for i in range(L1):
        tmp_sum = 0
        counter = 0
        array[i].sort()
        """
        [u, theta] = guassian_filter(array[i])
        for j in range(L2):
            if realtime_filter_a_val(array[i][j], u, theta):
                tmp_sum += array[i][j]
                counter += 1
                
        ave_val[i] = round(tmp_sum/counter)
        """
        for k in  range(70, len(array[i]) - 20):
            tmp_sum = tmp_sum + array[i][k]
            counter += 1
        ave_val[i] = round(tmp_sum/counter)
    return ave_val

"""
A0是1米时的RSSI信号值，n0是衰减因子,为待拟合因子
x为距离
"""
A0 = -68
def rssi_func(x, p):
    """
    数据拟合所需函数，室内定位的信号模型
    w1, w2为所需拟合的参数
    """
    n0 = p
    return A0 - 10*n0*np.log10(x)
    #n0, a = p
    #return A0 - 10*n0*np.log10(x) + a*x
    
"""
y和拟合函数之间的差, p为拟合参数, p = w1, w2
"""
def residuals(p, y, x):
    return y - rssi_func(x, p)

    
"""
使用室内衰减模型拟合数据，并画出对比图
"""
def plot_fitting_data():
    datas = read_file_data('testbeacon_12_3B_6A_1A_5F_04_major_10_minor_1.txt')
    
    avg_values = get_ave_data(datas)
    plt.figure(figsize=(8,8))
    x = np.arange(1, datas.shape[0] + 1, 1)
    #原始均值数据
    plt.plot(x, avg_values, '-*' ,label="before fitting")
    #网格
    plt.grid()
    plt.xlabel("distance(m)")
    plt.ylabel("RSSI")
    plt.title("Least-square fitting")
    
    p0 = [2]   #初始参数
    plsq = leastsq(residuals, p0, args=(avg_values, x))
    plt.plot(x, rssi_func(x, plsq[0]), "-o", label="after fitting, A0-10*n0*log10(x)")
    plt.legend()
    plt.show()
    
    print ('n0:', plsq[0][0])
   

    
"""
根据RSSI值返回距离值
distance = 10^((rssi-A0)/(-10*n0))
"""
def get_distance_from_rssi(A0, n0, rssi):
    #幂
    pw = (rssi - A0)/(-10*n0)
    return Math.pow(10, pw)
  
############################################################################## 
#####   下面为实时滤波
##############################################################################   
    
#队列大小
FILTER_QUEUE_SIZE = 10
  
"""
高斯滤波
"""
def guassian_filter(array):
    length = len(array)
    u = sum(array)/length
    tmp_sum = 0
    """
    
    """
    for i in range(length):
        tmp_sum += pow((array[i]-u),2)
    ave_pow = 1
    if length - 1 != 0:
        ave_pow = tmp_sum/(length-1)
    #theta = 1
    #print(ave_pow)
    theta = Math.sqrt(ave_pow)
    #print('u:',u, "theta:",theta, 'ave_pow:',ave_pow)
    [u, theta] = [round(u), round(theta)]
    return [u, theta]   
    
"""
对于一个值的滤波、
如果value在范围内则返回True
""" 
def realtime_filter_a_val(value, u, theta):
    if value >= (u - theta) and value <= (u + theta):
        return True
    return False

"""
针对一个数组的值进行滤波

"""
def realtime_filter_array_val(arrays, u, theta):
    ret = []
    length = len(arrays)
    for idx in range(length):
        if realtime_filter_a_val(arrays[idx], u, theta):
            ret.append(arrays[idx])
    return ret
    

"""
求数组中的值加上val的平均值
"""
def averagy_of_array(array, val):
    length = len(array)
    tmp_sum = val
    for idx in range(length):
        tmp_sum += array[idx]
    return round(tmp_sum/(length+1))
    
    
    
"""
开始滤波
"""
def queue_filter(real_data):
    data_length = len(real_data)
    [u, theta] = guassian_filter(real_data)
    idx = 0
    #用于放置结果
    result_data = []
    queue_sum = 0
    """
    队列
    """
    realtime_filter_queue = Collections.deque()
  
    """
    初始化队列,放置FILTER_QUEUE_SIZE个合法数
    """
    filter_len = 0
    while filter_len < FILTER_QUEUE_SIZE and idx < data_length:
        if realtime_filter_a_val(real_data[idx], u, theta):
            realtime_filter_queue.append(real_data[idx])
            queue_sum += real_data[idx]
            result_data.append(real_data[idx])  #
            #result_data.append(averagy_of_array(result_data, real_data[idx]))  #初始化的数据放置入结果中
            filter_len += 1
        idx += 1
        
    
    """
    每次取20个原始值，然后高斯滤波得到u, theta值
    再在这20个值中取得合格值
    """
    start = idx
    end = start + FILTER_QUEUE_SIZE
    while start < data_length:
        [u1, theta1] = guassian_filter(real_data[start:end])
        ret = realtime_filter_array_val(real_data[start:end], u1, theta1)
        ret_len = len(ret)
        for index in range(ret_len):
            left = realtime_filter_queue.popleft()  #取得队列最左边的数，同时
            realtime_filter_queue.append(ret[index])
            queue_sum += ret[index]
            queue_sum = queue_sum - left
            avg = round(queue_sum/FILTER_QUEUE_SIZE)
            result_data.append(avg)
        """
        更新索引值
        """
        start += FILTER_QUEUE_SIZE
        end += FILTER_QUEUE_SIZE
        if end > data_length:
            end = data_length
    
    return result_data   


"""
"""
def plot_distance(result_data):
    distance_datas = []
    length = len(result_data)
    for idx in range(length):
        distance_datas.append(get_distance_from_rssi(-59, 2.066, result_data[idx]))
    x_idx = np.arange(1, length + 1)
    plt.figure(figsize=(8,8))
    plt.plot(x_idx, distance_datas, '-o')
    plt.grid()
    plt.legend()
    plt.show()

    
"""
开始滤波
"""
def start_filter():
    ret_array = read_file_data('test_ibeacon_3_100-02.txt')
    result_data = queue_filter(ret_array)
    #print(result_data)
    plt.figure(figsize=(8,8))
    x_idx = np.arange(1, len(ret_array) + 1)
    plt.plot(x_idx, ret_array, '-b^', label='before queue fitting')
    x_idx = np.arange(1, len(result_data) + 1)
   
   
    plt.plot(x_idx, result_data, '-ro', label='after queue fitting')
    plt.grid()
    plt.xlabel("times")
    plt.ylabel("RSSI")
    plt.legend()
    plt.show()
    
    plot_distance(ret_array)
    plot_distance(result_data)     #画距离
    


"""

""" 
plot_fitting_data()
    
"""
开始滤波
"""
#start_filter()  
    
    
    
    
    
    
    
    
    
    
   