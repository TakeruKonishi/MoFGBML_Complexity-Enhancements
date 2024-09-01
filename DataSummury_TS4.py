# -*- coding: utf-8 -*-
"""
Created on Sun Feb 19 15:11:27 2023

@author: Takeru
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

numberofclasses = 2

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'ecoli1' + sep 

Dataset = 'ecoli'

addFontSize = 20

def SummaryOneTrial(Dataset, trial):
    
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'results.csv')
    RuleNumList = list(df_results['NR'])
    TraErrorList = list(df_results['train'])
    TstErrorList = list(df_results['test'])

    return {"ruleNum" : RuleNumList, "TraError" : TraErrorList, "TstError" : TstErrorList}

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

for i in range(len(rN)):
    for j in range(len(rN[i])):
        sum1=0
        sum2=0
        sum3=0
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               sum1+=1
               sum2+=er_tra[i][k]
               sum3+=er_tst[i][k]
        if sum1!=0:
            ave1=sum2/sum1
            ave2=sum3/sum1
    
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               er_tra[i][j]=ave1
               er_tra[i][k]=ave1
               er_tst[i][j]=ave2
               er_tst[i][k]=ave2

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=[0]*rNmax
            temp_tr=[0]*rNmax
            temp_ts=[0]*rNmax
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r[j]+=1
                    temp_tr[j]+=er_tra[i][k]
                    temp_ts[j]+=er_tst[i][k]
            if temp_r[j]!=0:
                temp_tra[j]+=temp_tr[j]/temp_r[j]
                temp_tst[j]+=temp_ts[j]/temp_r[j]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]



rN1 = [x *addFontSize for x in temp_rN]
x1 = np.arange(rNmax)
er_tra1=temp_tra
er_tst1=temp_tst

x1_renew = [numberofclasses, round((rNmax-1+numberofclasses)/2), rNmax-1]

er_tra1_renew = [temp_tra[numberofclasses],temp_tra[round((rNmax-1+numberofclasses)/2)],temp_tra[rNmax-1]]

er_tst1_renew = [temp_tst[numberofclasses],temp_tst[round((rNmax-1+numberofclasses)/2)],temp_tst[rNmax-1]]


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'ecoli2' + sep 

Dataset = 'ecoli'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

for i in range(len(rN)):
    for j in range(len(rN[i])):
        sum1=0
        sum2=0
        sum3=0
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               sum1+=1
               sum2+=er_tra[i][k]
               sum3+=er_tst[i][k]
        if sum1!=0:
            ave1=sum2/sum1
            ave2=sum3/sum1
    
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               er_tra[i][j]=ave1
               er_tra[i][k]=ave1
               er_tst[i][j]=ave2
               er_tst[i][k]=ave2

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=[0]*rNmax
            temp_tr=[0]*rNmax
            temp_ts=[0]*rNmax
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r[j]+=1
                    temp_tr[j]+=er_tra[i][k]
                    temp_ts[j]+=er_tst[i][k]
            if temp_r[j]!=0:
                temp_tra[j]+=temp_tr[j]/temp_r[j]
                temp_tst[j]+=temp_ts[j]/temp_r[j]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]


rN2 = [x * addFontSize for x in temp_rN]
x2 = np.arange(rNmax)
er_tra2=temp_tra
er_tst2=temp_tst

x2_renew = [numberofclasses, round((rNmax-1+numberofclasses)/2), rNmax-1]

er_tra2_renew = [temp_tra[numberofclasses],temp_tra[round((rNmax-1+numberofclasses)/2)],temp_tra[rNmax-1]]

er_tst2_renew = [temp_tst[numberofclasses],temp_tst[round((rNmax-1+numberofclasses)/2)],temp_tst[rNmax-1]]


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'ecoli4' + sep 

Dataset = 'ecoli'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

for i in range(len(rN)):
    for j in range(len(rN[i])):
        sum1=0
        sum2=0
        sum3=0
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               sum1+=1
               sum2+=er_tra[i][k]
               sum3+=er_tst[i][k]
        if sum1!=0:
            ave1=sum2/sum1
            ave2=sum3/sum1
    
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               er_tra[i][j]=ave1
               er_tra[i][k]=ave1
               er_tst[i][j]=ave2
               er_tst[i][k]=ave2

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=[0]*rNmax
            temp_tr=[0]*rNmax
            temp_ts=[0]*rNmax
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r[j]+=1
                    temp_tr[j]+=er_tra[i][k]
                    temp_ts[j]+=er_tst[i][k]
            if temp_r[j]!=0:
                temp_tra[j]+=temp_tr[j]/temp_r[j]
                temp_tst[j]+=temp_ts[j]/temp_r[j]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]


rN3 = [x *addFontSize for x in temp_rN]
x3 = np.arange(rNmax)
er_tra3=temp_tra
er_tst3=temp_tst

x3_renew = [numberofclasses, round((rNmax-1+numberofclasses)/2), rNmax-1]

er_tra3_renew = [temp_tra[numberofclasses],temp_tra[round((rNmax-1+numberofclasses)/2)],temp_tra[rNmax-1]]

er_tst3_renew = [temp_tst[numberofclasses],temp_tst[round((rNmax-1+numberofclasses)/2)],temp_tst[rNmax-1]]


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'ecoli5' + sep 

Dataset = 'ecoli'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

for i in range(len(rN)):
    for j in range(len(rN[i])):
        sum1=0
        sum2=0
        sum3=0
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               sum1+=1
               sum2+=er_tra[i][k]
               sum3+=er_tst[i][k]
        if sum1!=0:
            ave1=sum2/sum1
            ave2=sum3/sum1
    
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
               er_tra[i][j]=ave1
               er_tra[i][k]=ave1
               er_tst[i][j]=ave2
               er_tst[i][k]=ave2

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=[0]*rNmax
            temp_tr=[0]*rNmax
            temp_ts=[0]*rNmax
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r[j]+=1
                    temp_tr[j]+=er_tra[i][k]
                    temp_ts[j]+=er_tst[i][k]
            if temp_r[j]!=0:
                temp_tra[j]+=temp_tr[j]/temp_r[j]
                temp_tst[j]+=temp_ts[j]/temp_r[j]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]

rN4 = [x *addFontSize for x in temp_rN]
x4 = np.arange(rNmax)
er_tra4=temp_tra
er_tst4=temp_tst

x4_renew = [numberofclasses, round((rNmax-1+numberofclasses)/2), rNmax-1]

er_tra4_renew = [temp_tra[numberofclasses],temp_tra[round((rNmax-1+numberofclasses)/2)],temp_tra[rNmax-1]]

er_tst4_renew = [temp_tst[numberofclasses],temp_tst[round((rNmax-1+numberofclasses)/2)],temp_tst[rNmax-1]]


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'ecoli6' + sep 

Dataset = 'ecoli'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

for i in range(len(rN)):
    for j in range(len(rN[i])):
        sum1=0
        sum2=0
        sum3=0
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
                sum1+=1
                sum2+=er_tra[i][k]
                sum3+=er_tst[i][k]
        if sum1!=0:
            ave1=sum2/sum1
            ave2=sum3/sum1
    
        for k in reversed(range(len(rN[i]))):
        
            if rN[i][j]==rN[i][k]:
                er_tra[i][j]=ave1
                er_tra[i][k]=ave1
                er_tst[i][j]=ave2
                er_tst[i][k]=ave2

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=[0]*rNmax
            temp_tr=[0]*rNmax
            temp_ts=[0]*rNmax
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r[j]+=1
                    temp_tr[j]+=er_tra[i][k]
                    temp_ts[j]+=er_tst[i][k]
            if temp_r[j]!=0:
                temp_tra[j]+=temp_tr[j]/temp_r[j]
                temp_tst[j]+=temp_ts[j]/temp_r[j]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]

rN5 = [x *addFontSize for x in temp_rN]
x5 = np.arange(rNmax)
er_tra5=temp_tra
er_tst5=temp_tst

x5_renew = [numberofclasses, round((rNmax-1+numberofclasses)/2), rNmax-1]

er_tra5_renew = [temp_tra[numberofclasses],temp_tra[round((rNmax-1+numberofclasses)/2)],temp_tra[rNmax-1]]

er_tst5_renew = [temp_tst[numberofclasses],temp_tst[round((rNmax-1+numberofclasses)/2)],temp_tst[rNmax-1]]


x1=np.ma.masked_where(x1<numberofclasses,x1)
x2=np.ma.masked_where(x2<numberofclasses,x2)
x3=np.ma.masked_where(x3<numberofclasses,x3)
x4=np.ma.masked_where(x4<numberofclasses,x4)
x5=np.ma.masked_where(x5<numberofclasses,x5)


alpha = 0.7

plt.scatter(x3_renew,er_tra3_renew,color=[0,0,1],s=100,edgecolors="black", alpha= alpha, clip_on=False, zorder=2)
plt.plot(x3_renew, er_tra3_renew,color=[0,0,1], alpha= alpha, zorder=1)
plt.scatter(x4_renew,er_tra4_renew,color=[0,1,1],s=100,edgecolors="black", alpha= alpha, clip_on=False, zorder=2)
plt.plot(x4_renew, er_tra4_renew,color=[0,1,1], alpha= alpha, zorder=1)    
plt.scatter(x2_renew,er_tra2_renew,color=[0,1,0],s=100,edgecolors="black", alpha= alpha, clip_on=False, zorder=2)
plt.plot(x2_renew, er_tra2_renew,color=[0,1,0], alpha= alpha, zorder=1)
plt.scatter(x1_renew,er_tra1_renew,color=[1,0,0],s=100,edgecolors="black", alpha= alpha, clip_on=False, zorder=2)
plt.plot(x1_renew, er_tra1_renew,color=[1,0,0], alpha= alpha, zorder=1)
plt.scatter(x5_renew,er_tra5_renew,color=[1,0,1],s=100,edgecolors="black", alpha= alpha, clip_on=False, zorder=2) 
plt.plot(x5_renew, er_tra5_renew,color=[1,0,1], alpha= alpha, zorder=1)
plt.xlabel('Number of Rules',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks([numberofclasses,10,20,30,40],fontsize=16)
plt.yticks(np.arange(0.05,0.16,0.05),fontsize=16)
#plt.title('test data',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16)
plt.grid()
plt.rcParams['axes.axisbelow'] = True
plt.ylim(0.05,0.16)
plt.tight_layout()
plt.savefig("ecolitra_v2.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()

