# -*- coding: utf-8 -*-
"""
Created on Mon Jan  2 09:32:03 2017

@author: wensen
"""

import numpy as np
import matplotlib.pyplot as plt
import math

class StepCount(object):
    
    def __init__(self, avg_file, time_file):
        #三轴数据
        #self.ori_values = list([0,0,0])
        self.value_num = 4
        #存放计算阈值的波峰波谷差值
        self.temp_value = list([0,0,0,0])
        self.temp_count = 0
        #是否上升的标志位
        self.is_direction_up = False
        #持续上升次数
        self.continue_up_count = 0
        #上一点的持续上升的次数，为了记录波峰的上升次数
        self.continue_up_former_count = 0
        #上一点的状态，上升还是下降
        self.last_status = False
        #波峰值
        self.peak_of_wave = 0
        #波谷值
        self.valley_of_wave = 0
        #此次波峰的时间
        self.time_of_this_peak = 0
        #上次波峰的时间
        self.time_of_last_peak = 0

        #当前时间
        self.time_of_now = 0
        #当前传感器的值
        self.gravity_new = 0
        #上次传感器的值
        self.gravity_old = 0

        #动态阈值需要动态的数据，这个值用于这些动态数据的阈值
        self.initial_value = 1.3
        #初始阈值
        self.thread_value = 1.4

        self.TIME_DIFF = 200
        
        self.min_value = 1.0
        self.max_value = 25.6

        self.STEP_COUNT = 0
        #时间
        self.times = []
        self.values = []
        self.time_pos = -1
        self.steps = []
        self.__init_data(avg_file, time_file)
        
    def __load_file(self, filename):
        with open(filename, 'r') as f:
            a = np.loadtxt(f)
        return a
        
    def __init_data(self, avg_file, time_file):
        self.values = self.__load_file(avg_file)
        self.times  = self.__load_file(time_file)
        self.steps  = [0 for x in range(len(self.values))]
      
        
    def start_step(self):
        length = len(self.values)
        for n in range(length):
            self.detector_new_step()
        print('Total step: ', self.STEP_COUNT)
        
    def plot_result(self):
        L1 = len(self.values)
        x = np.arange(1, L1+1, 1)
        y1 = self.values
        y2 = self.steps
        plt.figure(figsize=(50,6))
        plt.plot(x, y1, '-bo', label="origin")
        plt.plot(x, y2, 'r^', label="step detect")
        plt.legend()
        plt.grid(True)
        
        plt.xlabel("times")
        plt.ylabel("m/s^2")
        plt.legend()
        plt.show()
        
    '''
    检测步子，并开始计步
    * 1.传入sersor中的数据
    * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定为1步
    * 3.符合时间差条件，波峰波谷差值大于initial_value，则将该差值纳入阈值的计算中
    '''
    def detector_new_step(self):
        self.time_pos += 1
        value = self.values[self.time_pos]
        
        if self.gravity_old == 0:
            self.gravity_old = value
        else:
            if (self.detector_peak(value, self.gravity_old)):
                
                self.time_of_last_peak = self.time_of_this_peak
                self.time_of_now = self.times[self.time_pos]
                #print('detect a peak')
                if self.time_of_now - self.time_of_last_peak >= self.TIME_DIFF \
                and self.peak_of_wave - self.valley_of_wave >= self.thread_value \
                and self.time_of_now - self.time_of_last_peak <= 2000:
                    
                    self.time_of_this_peak = self.time_of_now
                    self.STEP_COUNT += 1
                    self.steps[self.time_pos-1] = self.values[self.time_pos-1]
                
                if self.time_of_now - self.time_of_last_peak >= 200 \
                and self.peak_of_wave - self.valley_of_wave >= self.initial_value:
                    self.time_of_this_peak = self.time_of_now
                    self.thread_value = self.peak_valley_thread(self.peak_of_wave - self.valley_of_wave)
                
        self.gravity_old = value

            

    '''
    检测波峰
     * 以下四个条件判断为波峰：
     * 1.目前点为下降的趋势：is_direction_up为False
     * 2.之前的点为上升的趋势：last_status为true
     * 3.到波峰为止，持续上升大于等于2次
     * 记录波谷值
     * 4.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * .所以要记录每次的波谷值，为了和下次的波峰做对比
     '''      
    def detector_peak(self, new_value, old_value):
        
        self.last_status = self.is_direction_up
        if new_value >= old_value or math.fabs(new_value-old_value) <= 0.3:
            self.is_direction_up = True
            self.continue_up_count += 1
        else:
             self.continue_up_former_count = self.continue_up_count
             self.continue_up_count = 0
             self.is_direction_up = False
        
        #print('last_status: ', self.last_status, ' is_direction_up:', self.is_direction_up, \
        #      ' continue_up_former_count:', self.continue_up_former_count, ' old_value:', \
        #      old_value)
        if self.is_direction_up == False and self.last_status and \
        (self.continue_up_former_count >= 2 and old_value >= self.min_value and \
        old_value <= self.max_value):
            self.peak_of_wave = old_value
            return True
        elif self.last_status == False and self.is_direction_up:
            self.valley_of_wave = old_value
            return False
        else:
            return False


    '''
    阈值的计算
     * 1.通过波峰波谷的差值计算阈值
     * 2.记录4个值，存入temp_value[]数组中
     * 3.在将数组传入函数average_value中计算阈值
     '''      
    def peak_valley_thread(self, value):
        self.temp_thread = self.thread_value
        if self.temp_count < self.value_num:
            self.temp_value[self.temp_count] = value
            self.temp_count += 1
        else:
            self.temp_thread = self.average_value(self.temp_value, self.value_num)
            for i in range(1, self.value_num):
                self.temp_value[i-1] = self.temp_value[i]
                self.temp_value[self.value_num - 1] = value
        return self.temp_thread

    '''
    梯度化阈值
    1.计算数组的均值
    2.通过均值将阈值梯度化在一个范围里
    '''      
    def average_value(self, values, n):
    
        avg = 0.0
        for i in range(n):
            avg += values[i]
    
        avg = avg/self.value_num    
        if (avg >= 8):
            avg = 4.3
        elif avg >= 7 and avg < 8:
            avg = 3.3
        elif avg >= 4 and avg < 7:
            avg = 2.3
        elif avg >= 3 and avg < 4:
            avg = 2.0
        else:
            avg = 1.7
        return avg


if __name__ == '__main__':
    
    step_counter = StepCount('./sensor_5/after_compu_z.txt', './sensor_5/time.txt')
    print('starting...')
    step_counter.start_step()
    step_counter.plot_result()
    print('ending')

