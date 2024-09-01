# -*- coding: utf-8 -*-
"""
Created on Mon Jun 20 10:55:05 2022

@author: Takeru
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'bupa' + sep  

Dataset = 'bupa'

errorrate=[]
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(3000,300001,3000)))
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None, names=['f0','f1'])
        Error = min(df_results.f0)
        errorrate.append(Error)

errorrate=np.array(errorrate)

er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29=np.array_split(errorrate,30)

er=np.vstack([er0,er1,er2,er3,er4,er5,er6,er7,er8,er9,er10,er11,er12,er13,er14,er15,er16,er17,er18,er19,er20,er21,er22,er23,er24,er25,er26,er27,er28,er29])

evaluation=(list(range(50,5001,50)))

for i in range(30):
    plt.plot(evaluation,er[i],color="r")

plt.axvline(x=1250,color="k")
plt.axvline(x=2500,color="k")
plt.axvline(x=3750,color="k")
plt.xlabel('Number of Generations',fontsize=20)
plt.ylabel('Error Rate',fontsize=20)
plt.xticks(np.arange(0,5001,1250),fontsize=16)
plt.yticks(fontsize=16)
plt.ylim(0,0.40)
plt.gca().yaxis.set_major_formatter(plt.FormatStrFormatter('%.2f'))
plt.grid()
plt.tight_layout()
plt.savefig("bu30.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()



