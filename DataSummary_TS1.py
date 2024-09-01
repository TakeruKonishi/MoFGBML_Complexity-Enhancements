# -*- coding: utf-8 -*-
"""
Created on Tue Dec 17 11:52:00 2021

@author: konishi
"""

import pandas as pd
import statistics


sep = "\\"

folder = 'results' + sep + 'TS' + sep + 'heart150000' + sep 

Dataset = 'heart'

"""
function SummaryOneTrial
this function return results(number of solutions, max rule number, min error rate for train, appropriate error rate for test)
for one trial.
@param Dataset : String
@param trial : String , it is the trial number. 
return dictionary of: 
           number of solutions,
           max the number of rule,
           min error rate for the training data,
           appropriate error rate for the test data
"""
def SummaryOneTrial(Dataset, trial):
    """
    #最終解選択手法1：学習用データで最も良い解における平均値
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    
    numberofsolutions = len(df_results.drop_duplicates(subset=['train', 'NR', 'RL', 'Cover', 'RW']).index)
    
    maxRuleNum = max(df_results.NR)

    TraError = min(df_results.train)
    
    # 最大値を持つ行番号を抽出
    max_ruleNum = df_results[df_results['NR'] == df_results['NR'].max()]
    
    # 各行番号に対応するtestの平均値を計算
    TstError = max_ruleNum['test'].mean()
    """
    """
    #最終解選択手法2：学習用データで最も良い解　→　Covermax →　RLmin →　RWmin
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    
    numberofsolutions = len(df_results.drop_duplicates(subset=['train', 'NR', 'RL', 'Cover', 'RW']).index)
    
    maxRuleNum = max(df_results.NR)

    TraError = min(df_results.train)
    
    # trainの値が最も小さい行を抽出
    min_train = df_results[df_results['train'] == df_results['train'].min()]
    
    # 最もCoverが大きい行を抽出
    selected_row = min_train[min_train['Cover'] == min_train['Cover'].max()]
    
    # Coverが同じ場合はRLが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RL'] == selected_row['RL'].min()]
        
    # RLが同じ場合は平均ルール重みが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RW'] == selected_row['RW'].min()]
        
    target_pop = selected_row['pop'].iloc[0]
    
    TstError = df_results.loc[df_results['pop'] == target_pop, 'test'].values[0]
    """

    #最終回選択手法3：検証用データで最も良い解　→　Covermax →　RLmin →　RWmin
    df_results = pd.read_csv(folder + Dataset + "_trial" + trial + sep + 'resultsARC.csv')
    
    numberofsolutions = len(df_results.drop_duplicates(subset=['train', 'NR', 'RL', 'Cover', 'RW']).index)
    
    maxRuleNum = max(df_results.NR)
    
    TraError = min(df_results.train)
    
    # valの値が最も小さい行を抽出
    min_val = df_results[df_results['val'] == df_results['val'].min()]
    """
    # RLの値が最も小さい行を抽出
    selected_row = min_val[min_val['RL'] == min_val['RL'].min()] 
    # RLが同じ場合はNRが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['NR'] == selected_row['NR'].min()]
    # NRが同じ場合はCoverが最大の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['Cover'] == selected_row['Cover'].max()]
    # Coverが同じ場合は平均ルール重みが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RW'] == selected_row['RW'].min()]
    """
    """
    # 最もCoverが大きい行を抽出
    selected_row = min_val[min_val['Cover'] == min_val['Cover'].max()]

    # Coverが同じ場合はRLが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RL'] == selected_row['RL'].min()]
    # RLが同じ場合はNRが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['NR'] == selected_row['NR'].min()]
    # NRが同じ場合は平均ルール重みが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RW'] == selected_row['RW'].min()]
    """ 
    """       
     # 平均ルール重みの値が最も小さい行を抽出
    selected_row = min_val[min_val['RW'] == min_val['RW'].min()] 
    # 平均ルール重みが同じ場合はCoverが最大の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['Cover'] == selected_row['Cover'].max()]
    # Coverが同じ場合はRLが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['RL'] == selected_row['RL'].min()]
    # RLが同じ場合はNRが最小の行を抽出
    if len(selected_row) > 1:
        selected_row = selected_row[selected_row['NR'] == selected_row['NR'].min()]
    """
    """
    target_pop = selected_row['pop'].iloc[0]

    TstError = df_results.loc[df_results['pop'] == target_pop, 'test'].values[0]
    """
    # 各行番号に対応するtestの平均値を計算
    TstError = min_val['test'].mean()
    
    return {"numberofsolutions" : numberofsolutions, "ruleNum" : maxRuleNum, "TraError" : TraError, "TstError" : TstError}  
    
# make trial number rr = {0,1,2}, cc = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

numberofsolutions = statistics.mean([results[trial]["numberofsolutions"] for trial in range(len(results))])

numberofsolutionsstd = statistics.stdev([results[trial]["numberofsolutions"] for trial in range(len(results))])

ruleNum = statistics.mean([results[trial]["ruleNum"] for trial in range(len(results))])

ruleNumstd = statistics.stdev([results[trial]["ruleNum"] for trial in range(len(results))])

TraError = statistics.mean([results[trial]["TraError"] for trial in range(len(results))])

TraErrorstd = statistics.stdev([results[trial]["TraError"] for trial in range(len(results))])

TstError = statistics.mean([results[trial]["TstError"] for trial in range(len(results))])

TstErrorstd = statistics.stdev([results[trial]["TstError"] for trial in range(len(results))])


# ----print result ----

print("Average the number of solutions : " + str(numberofsolutions))

print("std the number of solutions : " + str(numberofsolutionsstd))

print("Average the number of rule : " + str(ruleNum))

print("std the number of rule : " + str(ruleNumstd))

print("Average error rate for the training data : " + str(TraError))

print("std error rate for the training data : " + str(TraErrorstd))

print("Average error rate for the test data : " + str(TstError))

print("std error rate for the test data : " + str(TstErrorstd))






