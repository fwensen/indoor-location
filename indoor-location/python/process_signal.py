# -*- coding: utf-8 -*-
"""
Created on Thu Oct 13 15:41:56 2016

@author: vincent
"""
import numpy as np
import matplotlib.pyplot as plt
import math as Math


"""
平均值
"""
def avg(a):
    l = len(a)
    sum = 0
    for i in range(l):
        sum = sum + a[i]
    return sum/l

def var(a):
    u = avg(a)
    l = len(a)
    s = 0
    for i in range(l):
        s = s + Math.pow(a[i] - u, 2)
    return s/l




filename = "2/testbeacon_19_18_FC_01_E2_C3_major_10002_minor_14.txt";

f = open(filename, "r")


#加载文件
a = np.loadtxt(f)
f.close()
"""
L1为第一维
"""
#L1 = a.shape[0]
L1 = len(a)
x = np.arange(1, L1+1, 1)
y = a
#y2 = a[1]
#print(y)
plt.figure(figsize=(12,12))
plt.plot(x, y, '-r^', label='figure for minor 4')
plt.legend()
#plt.plot(x, y2, '-b*', label='figure for minor 3 another place')
plt.grid()

plt.xlabel("times")
plt.ylabel("RSSI")
plt.legend()
plt.show()
print('minor 14:', var(y))
#print ('minor 14 another:', var(y2))