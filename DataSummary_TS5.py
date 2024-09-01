# -*- coding: utf-8 -*-
"""
Created on Thu Jul 20 17:33:47 2023

@author: konishi
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

numberofclasses = 2

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'bupa4v2_60000x' + sep 

Dataset = 'bupa'

addFontSize = 20

def SummaryOneTrial(Dataset, trial):

    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    
    # NRを重複なしで小さいものから順に格納するリストを作成
    unique_nrs = sorted(df_results['NR'].unique())
    
    # 各NRに対して最小のtestを格納したリストを作成
    min_tests = []
    for nr in unique_nrs:
        min_test = df_results[df_results['NR'] == nr]['test'].min()
        min_tests.append(min_test)
        
    # 各NRに対して最大のtestを格納したリストを作成
    max_tests = []
    for nr in unique_nrs:
        max_test = df_results[df_results['NR'] == nr]['test'].max()
        max_tests.append(max_test)

    return {"ruleNum" : unique_nrs, "min_tests" : min_tests, "max_tests" : max_tests}

trial = [str(0) + str(0)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

min_tests = [results[trial]["min_tests"] for trial in range(len(results))]

max_tests = [results[trial]["max_tests"] for trial in range(len(results))]

rN1=np.array(ruleNum)
er_min1=np.array(min_tests)
er_max1=np.array(max_tests)

rN1=np.ma.masked_where(rN1<numberofclasses,rN1)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'bupa4v1_60000x' + sep 

Dataset = 'bupa'

addFontSize = 20

trial = [str(0) + str(0)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

min_tests = [results[trial]["min_tests"] for trial in range(len(results))]

max_tests = [results[trial]["max_tests"] for trial in range(len(results))]

rN2=np.array(ruleNum)
er_min2=np.array(min_tests)
er_max2=np.array(max_tests)

rN2=np.ma.masked_where(rN2<numberofclasses,rN2)


plt.plot(rN2.T, er_min2.T, linewidth=1, color=[1, 0, 0],marker="o", markersize=10)
plt.plot(rN2.T, er_max2.T, linewidth=1, color=[1, 0, 0],marker="o", markersize=10)
for min_test, max_test in zip(er_min2, er_max2):
    plt.fill_between(rN2[0], min_test, max_test, color=[1, 0, 0], alpha=0.5)
plt.plot(rN1.T, er_min1.T, linewidth=1, color=[0, 0, 1],marker="o")
plt.plot(rN1.T, er_max1.T, linewidth=1, color=[0, 0, 1],marker="o")
for min_test, max_test in zip(er_min1, er_max1):
    plt.fill_between(rN1[0], min_test, max_test, color=[0, 0, 1], alpha=0.3)
#plt.scatter(rN1,er_min1,color=[0,0,1],s=100,edgecolors="black", alpha= alpha)
#plt.scatter(rN1,er_max1,color=[1,0,0],s=100,edgecolors="black", alpha= alpha)
plt.xlabel('Number of Rules',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks([numberofclasses,5,10,15,20,24],fontsize=16)
plt.yticks(np.arange(0.35,0.51,0.05),fontsize=16)
#plt.title('test data',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16)
plt.grid()
plt.ylim(0.35,0.51)
plt.tight_layout()
plt.savefig("bupatstselected60000.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()

