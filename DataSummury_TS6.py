# -*- coding: utf-8 -*-
"""
Created on Mon May 29 18:14:08 2023

@author: Takeru
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

plt.rc('font', family='Times New Roman')

sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'haberman1' + sep  

Dataset = 'haberman'

ns = []
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]
evaluation=(list(range(15000,300001,15000)))
evaluation.insert(0,3000)
eva_str=[str(i) for i in evaluation]

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None)
        numberofsolutions = len(df_results.drop_duplicates().index)
        ns.append(numberofsolutions)

ns=np.array(ns)

ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29=np.array_split(ns,30)

ns_=np.vstack([ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29])

ns_final1=np.mean(ns_,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'haberman2' + sep  

Dataset = 'haberman'

ns = []

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None)
        numberofsolutions = len(df_results.drop_duplicates().index)
        ns.append(numberofsolutions)

ns=np.array(ns)

ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29=np.array_split(ns,30)

ns_=np.vstack([ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29])

ns_final2=np.mean(ns_,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'haberman4' + sep  

Dataset = 'haberman'

ns = []

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None)
        numberofsolutions = len(df_results.drop_duplicates().index)
        ns.append(numberofsolutions)

ns=np.array(ns)

ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29=np.array_split(ns,30)

ns_=np.vstack([ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29])

ns_final4=np.mean(ns_,axis=0)


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'haberman6' + sep  

Dataset = 'haberman'

ns = []

for i in trial:
    for j in eva_str:
        df_results = pd.read_csv(folder + Dataset + "_trial" + i + sep + "FUN-" + j + '.csv', header=None)
        numberofsolutions = len(df_results.drop_duplicates().index)
        ns.append(numberofsolutions)

ns=np.array(ns)

ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29=np.array_split(ns,30)

ns_=np.vstack([ns0,ns1,ns2,ns3,ns4,ns5,ns6,ns7,ns8,ns9,ns10,ns11,ns12,ns13,ns14,ns15,ns16,ns17,ns18,ns19,ns20,ns21,ns22,ns23,ns24,ns25,ns26,ns27,ns28,ns29])

ns_final6=np.mean(ns_,axis=0)

evaluation=(list(range(250,5001,250)))
evaluation.insert(0,50)
            
plt.plot(evaluation,ns_final1,color=[1,0,0],lw=2)
plt.plot(evaluation,ns_final2,color=[0,1,0],lw=2)
plt.plot(evaluation,ns_final4,color=[0,0,1],lw=2)
plt.plot(evaluation,ns_final6,color=[1,0,1],lw=2)
#plt.axvline(x=2500,color="k")
plt.xlabel('Number of Generations',fontsize=20)
plt.ylabel('Number of Solutions',fontsize=20)
plt.xticks(np.arange(0,5001,1250),fontsize=16)
plt.yticks(fontsize=16)
#plt.title('誤識別率',fontsize=28,fontname="MS Gothic")
#plt.legend(fontsize=16,loc='upper left',bbox_to_anchor=(1,1),prop={"family":"MS Gothic"})
plt.ylim(0,20)
plt.gca().yaxis.set_major_formatter(plt.FormatStrFormatter('%.2f'))
plt.grid()
plt.tight_layout()
plt.savefig("habermanNS.png",format="png",dpi=400,bbox_inches='tight',pad_inches=0)
plt.show()
