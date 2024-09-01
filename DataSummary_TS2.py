# -*- coding: utf-8 -*-
"""
Created on Tue Dec 20 11:37:30 2021

@author: konishi
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart0' + sep  

Dataset = 'heart'

errorrate=[]
ruleNum=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        maxRuleNum = max(df_results.f1)
        errorrate.append(Error)
        ruleNum.append(maxRuleNum)

errorrate=np.array(errorrate)
ruleNum=np.array(ruleNum)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)
rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29=np.array_split(ruleNum,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])
rN=np.vstack([rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29])

er_final=np.mean(er,axis=0)
rN_final=np.mean(rN,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart75000' + sep  

Dataset = 'heart'

errorrate=[]
ruleNum=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        maxRuleNum = max(df_results.f1)
        errorrate.append(Error)
        ruleNum.append(maxRuleNum)

errorrate=np.array(errorrate)
ruleNum=np.array(ruleNum)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)
rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29=np.array_split(ruleNum,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])
rN=np.vstack([rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29])

er_final2=np.mean(er,axis=0)
rN_final2=np.mean(rN,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart150000' + sep  

Dataset = 'heart'

errorrate=[]
ruleNum=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        maxRuleNum = max(df_results.f1)
        errorrate.append(Error)
        ruleNum.append(maxRuleNum)

errorrate=np.array(errorrate)
ruleNum=np.array(ruleNum)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)
rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29=np.array_split(ruleNum,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])
rN=np.vstack([rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29])

er_final3=np.mean(er,axis=0)
rN_final3=np.mean(rN,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart225000' + sep  

Dataset = 'heart'

errorrate=[]
ruleNum=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        maxRuleNum = max(df_results.f1)
        errorrate.append(Error)
        ruleNum.append(maxRuleNum)

errorrate=np.array(errorrate)
ruleNum=np.array(ruleNum)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)
rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29=np.array_split(ruleNum,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])
rN=np.vstack([rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29])

er_final4=np.mean(er,axis=0)
rN_final4=np.mean(rN,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart300000' + sep  

Dataset = 'heart'

errorrate=[]
ruleNum=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        maxRuleNum = max(df_results.f1)
        errorrate.append(Error)
        ruleNum.append(maxRuleNum)

errorrate=np.array(errorrate)
ruleNum=np.array(ruleNum)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)
rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29=np.array_split(ruleNum,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])
rN=np.vstack([rN0,rN1,rN2,rN3,rN4,rN5,rN6,rN7,rN8,rN9,rN10,rN11,rN12,rN13,rN14,rN15,rN16,rN17,rN18,rN19,rN20,rN21,rN22,rN23,rN24,rN25,rN26,rN27,rN28,rN29])

er_final5=np.mean(er,axis=0)
rN_final5=np.mean(rN,axis=0)

evaluation=(list(range(50,5001,50)))
            
plt.plot(evaluation,er_final,color=[1,0,0],label="0世代",lw=2)
plt.plot(evaluation,er_final2,color=[0,1,0],label="1,250世代",lw=2)
plt.plot(evaluation,er_final3,color=[0,1,1],label="2,500世代",lw=2)
plt.plot(evaluation,er_final4,color=[0,0,1],label="3,750世代",lw=2)
plt.plot(evaluation,er_final5,color=[1,0,1],label="5,000世代",lw=2)
plt.axvline(x=1250,color="k")
plt.axvline(x=2500,color="k")
plt.axvline(x=3750,color="k")
plt.xlabel('Number of Generations',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks(np.arange(0,5001,1250),fontsize=16)
plt.yticks(fontsize=16)
#plt.title('誤識別率',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16,loc='upper left',bbox_to_anchor=(1,1),prop={"family":"MS Gothic"})
plt.ylim(0.02,0.12)
plt.gca().yaxis.set_major_formatter(plt.FormatStrFormatter('%.2f'))
plt.grid()
plt.tight_layout()
plt.savefig("heartE.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()

plt.plot(evaluation,rN_final,color=[1,0,0],label="0世代",lw=2)
plt.plot(evaluation,rN_final2,color=[0,1,0],label="1,250世代",lw=2)
plt.plot(evaluation,rN_final3,color=[0,1,1],label="2,500世代",lw=2)
plt.plot(evaluation,rN_final4,color=[0,0,1],label="3,750世代",lw=2)
plt.plot(evaluation,rN_final5,color=[1,0,1],label="5,000世代",lw=2)
plt.axvline(x=1250,color="k")
plt.axvline(x=2500,color="k")
plt.axvline(x=3750,color="k")
plt.xlabel('Number of Generations',fontsize=20)
plt.ylabel('Number of Rules',fontsize=20)
plt.xticks(np.arange(0,5001,1250),fontsize=16)
plt.yticks(fontsize=16)
#plt.title('ルール数',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16,loc='upper left',bbox_to_anchor=(1,1),prop={"family":"MS Gothic"})
plt.ylim(0,30)
plt.grid()
plt.tight_layout()
plt.savefig("heartN.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()




 
