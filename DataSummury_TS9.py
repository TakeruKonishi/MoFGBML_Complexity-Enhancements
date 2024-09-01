# -*- coding: utf-8 -*-
"""
Created on Fri Feb  2 15:57:49 2024

@author: Takeru
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

numberofclasses = 2

addFontSize = 20

def SummaryOneTrial1(Dataset, trial):
    
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'results.csv')
    RuleNumList = list(df_results['NR'])
    RLList = list(df_results['RL'])
    CoverList = list(df_results['Cover'])
    #RWList = list(df_results['RW'])
    TraErrorList = list(df_results['train'])
    TstErrorList = list(df_results['test'])

    #return {"ruleNum" : RuleNumList, "RL" : RLList , "Cover" : CoverList, "RW" : RWList, "TraError" : TraErrorList, "TstError" : TstErrorList}
    return {"ruleNum" : RuleNumList, "RL" : RLList , "Cover" : CoverList, "TraError" : TraErrorList, "TstError" : TstErrorList}

def SummaryOneTrial2(Dataset, trial):
    
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    RuleNumList = list(df_results['NR'])
    RLList = list(df_results['RL'])
    CoverList = list(df_results['Cover'])
    RWList = list(df_results['RW'])
    TraErrorList = list(df_results['train'])
    ValErrorList = list(df_results['val'])
    TstErrorList = list(df_results['test'])

    return {"ruleNum" : RuleNumList, "RL" : RLList , "Cover" : CoverList, "RW" : RWList, "TraError" : TraErrorList, "ValError" : ValErrorList, "TstError" : TstErrorList}

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'bupa150000' + sep 

Dataset = 'bupa'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial2(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

RL = [results[trial]["RL"] for trial in range(len(results))]

Cover = [results[trial]["Cover"] for trial in range(len(results))]

RW = [results[trial]["RW"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

ValError = [results[trial]["ValError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
RL=np.array(RL)
Cover=np.array(Cover)
RW=np.array(RW)
er_tra=np.array(TraError)
er_val=np.array(ValError)
er_tst=np.array(TstError)

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)

temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax

for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
           temp_rN[j]+=1
           temp_r=0
           temp_tr=0
           temp_val=0
           temp_Cover=0
           temp_RL=0
           temp_RW=0
           for k in range(len(rN[i])):
               if rN[i][k]==j:
                    temp_r+=1
                    temp_tr+=er_tra[i][k]
                    if temp_val==0:
                        temp_val=k
                        temp_Cover=k
                        temp_RL=k
                        temp_RW=k
                    elif temp_val>0 and er_val[i][k]<er_val[i][temp_val]:
                            temp_val=k
                            temp_Cover=k
                            temp_RL=k
                            temp_RW=k
                    elif er_val[i][k]==er_val[i][temp_val] and Cover[i][k]>Cover[i][temp_Cover]:
                            temp_val=k
                            temp_Cover=k
                            temp_RL=k
                            temp_RW=k
                    elif er_val[i][k]==er_val[i][temp_val] and Cover[i][k]==Cover[i][temp_Cover] and RL[i][k]<RL[i][temp_RL]:
                            temp_val=k
                            temp_Cover=k
                            temp_RL=k
                            temp_RW=k
                    elif er_val[i][k]==er_val[i][temp_val] and Cover[i][k]==Cover[i][temp_Cover] and RL[i][k]==RL[i][temp_RL] and RW[i][k]<RW[i][temp_RW]:
                            temp_val=k
                            temp_Cover=k
                            temp_RL=k
                            temp_RW=k
                            
           if temp_r!=0:
               temp_tra[j]+=temp_tr/temp_r
           if temp_val!=0:
               temp_tst[j]+=er_tst[i][temp_val]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]

rN1 = [x *addFontSize for x in temp_rN]
x1 = np.arange(rNmax)
er_tra1=temp_tra
er_tst1=temp_tst



sep = "\\"

folder = 'results旧版3' + sep + 'TS' + sep + 'bupa4X2500' + sep 

Dataset = 'bupa'

# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial1(Dataset, x), trial))

ruleNum = [results[trial]["ruleNum"] for trial in range(len(results))]

RL = [results[trial]["RL"] for trial in range(len(results))]

Cover = [results[trial]["Cover"] for trial in range(len(results))]

#RW = [results[trial]["RW"] for trial in range(len(results))]

TraError = [results[trial]["TraError"] for trial in range(len(results))]

TstError = [results[trial]["TstError"] for trial in range(len(results))]

rN=np.array(ruleNum)
RL=np.array(RL)
Cover=np.array(Cover)
#RW=np.array(RW)
er_tra=np.array(TraError)
er_tst=np.array(TstError)

rNmax=max([max(x) for x in rN])+1

rNmax=round(rNmax)
               
temp_rN=[0]*rNmax
temp_tra=[0]*rNmax
temp_tst=[0]*rNmax
   
for i in range(len(rN)):
    for j in range(rNmax):
        if j in rN[i]:
            temp_rN[j]+=1
            temp_r=0
            temp_tr=0
            temp_Cover=0
            temp_RL=0
            #temp_RW=0
            for k in range(len(rN[i])):
                if rN[i][k]==j:
                    temp_r+=1
                    temp_tr+=er_tra[i][k]
                    if temp_Cover==0:
                        temp_Cover=k
                        temp_RL=k
                        #temp_RW=k
                    elif temp_Cover>0 and Cover[i][k]>Cover[i][temp_Cover]:
                        temp_Cover=k
                        temp_RL=k
                        #temp_RW=k
                    elif Cover[i][k]==Cover[i][temp_Cover] and RL[i][k]<RL[i][temp_RL]:
                        temp_Cover=k
                        temp_RL=k
                        #temp_RW=k
                    #elif Cover[i][k]==Cover[i][temp_Cover] and RL[i][k]==RL[i][temp_RL] and RW[i][k]<RW[i][temp_RW]:
                        #temp_Cover=k
                        #temp_RL=k
                        #temp_RW=k
                        
            if temp_r!=0:
                temp_tra[j]+=temp_tr/temp_r
            if temp_RL!=0:
                temp_tst[j]+=er_tst[i][temp_Cover]

for j in range(rNmax):
    if temp_rN[j]!=0:
        temp_tra[j]=temp_tra[j]/temp_rN[j]
        temp_tst[j]=temp_tst[j]/temp_rN[j]


rN2 = [x *addFontSize for x in temp_rN]
x2 = np.arange(rNmax)
er_tra2=temp_tra
er_tst2=temp_tst



x1=np.ma.masked_where(x1<numberofclasses,x1)
x2=np.ma.masked_where(x2<numberofclasses,x2)
#x3=np.ma.masked_where(x3<numberofclasses,x3)
#x4=np.ma.masked_where(x4<numberofclasses,x4)
#x5=np.ma.masked_where(x5<numberofclasses,x5)

alpha = 0.7
#plt.scatter(x3,er_tra3,color=[0,1,1],s=rN3,edgecolors="black", alpha= alpha)
#plt.scatter(x4,er_tra4,color=[0,0,1],s=rN4,edgecolors="black", alpha= alpha)     
plt.scatter(x2,er_tst2,color=[0,1,0],s=rN2,edgecolors="black", alpha= alpha)
plt.scatter(x1,er_tst1,color=[1,0,0],s=rN1,edgecolors="black", alpha= alpha)
#plt.scatter(x5,er_tra5,color=[1,0,1],s=rN5,edgecolors="black", alpha= alpha)   
plt.xlabel('Number of Rules',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks([numberofclasses,10,20,30],fontsize=16)
plt.yticks(np.arange(0.25,0.46,0.05),fontsize=16)
#plt.title('test data',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16)
plt.grid()
plt.ylim(0.25,0.46)
plt.tight_layout()
plt.savefig("bupatst.pdf")
#plt.savefig("bupatra.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()

