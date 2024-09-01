# -*- coding: utf-8 -*-
"""
Created on Tue Dec 21 15:35:00 2021

@author: konishi
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

numberofclasses = 4

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'vehicle4X' + sep 

Dataset = 'vehicle'

addFontSize = 20

def SummaryOneTrial(Dataset, trial):
    
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'results.csv')
    RuleNumList = list(df_results['NR'])
    TraErrorList = list(df_results['train'])
    TstErrorList = list(df_results['test'])

    return {"ruleNum" : RuleNumList, "TraError" : TraErrorList, "TstError" : TstErrorList}

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(1) + str(1)]

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

        
sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'vehicle4X' + sep 

Dataset = 'vehicle'

def SummaryOneTrialARC(Dataset, trial):
    
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    RuleNumList = list(df_results['NR'])
    TraErrorList = list(df_results['train'])
    TstErrorList = list(df_results['test'])

    return {"ruleNum" : RuleNumList, "TraError" : TraErrorList, "TstError" : TstErrorList}

# make trial number rr = {0,1,2}, cc = {0,1,...9}
#trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrialARC(Dataset, x), trial))

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

"""
sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'mammographic150000' + sep 

Dataset = 'mammographic'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
#trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

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


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'mammographic225000' + sep 

Dataset = 'mammographic'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
#trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

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


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'mammographic300000' + sep 

Dataset = 'mammographic'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
#trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

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
"""
x1=np.ma.masked_where(x1<numberofclasses,x1)
x2=np.ma.masked_where(x2<numberofclasses,x2)
er_tra1=np.ma.masked_where(er_tra1==0, er_tra1)
er_tra2=np.ma.masked_where(er_tra2==0, er_tra2)
"""
x3=np.ma.masked_where(x3<numberofclasses,x3)
x4=np.ma.masked_where(x4<numberofclasses,x4)
x5=np.ma.masked_where(x5<numberofclasses,x5)
"""

alpha = 0.7
alpha_values1 = np.where(er_tra1 == 0, 0, alpha)
alpha_values2 = np.where(er_tra2 == 0, 0, alpha)
#plt.scatter(x3,er_tra3,color=[0,1,1],label="2,500世代",edgecolors="black", alpha= alpha)
#plt.scatter(x4,er_tra4,color=[0,0,1],label="3,750世代",edgecolors="black", alpha= alpha)     
plt.scatter(x2,er_tra2,color=[0,0,1],label="1,250世代",edgecolors="black",marker="D", s=60, alpha= alpha_values2)
plt.scatter(x1,er_tra1,color=[1,0,0],label="0世代",edgecolors="black",marker="o", alpha= alpha_values1)
#plt.scatter(x5,er_tra5,color=[1,0,1],label="5,000世代",edgecolors="black", alpha= alpha)   
plt.xlabel('Number of Rules',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks([numberofclasses,10,30,50,70],fontsize=16)
plt.yticks(np.arange(0.00,0.26,0.05),fontsize=16)
#plt.title('test data',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16)
plt.grid()
plt.ylim(0.00,0.26)
plt.tight_layout()
plt.savefig("vehicle4Xtra11.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()

