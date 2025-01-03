# -*- coding: utf-8 -*-
"""
Created on Fri Oct  7 01:04:14 2022

@author: kawano
"""

import subprocess
from concurrent.futures import ThreadPoolExecutor


def detection_async(_request):
    
    result = subprocess.run(["Java", "-Xms1g", "-Xmx8g", "-jar",
                             _request["jarFile"],
                             _request["dataset"],
                             _request["algroithmID"],
                             _request["experimentID"],
                             _request["parallelCores"],
                             _request["trainFile"],
                             _request["validationFile"],
                             _request["testFile"],
                             _request["changeStageSetting"]])
    
    return result


def detection_async_parallel(_requests):
    
    results = []
    
    with ThreadPoolExecutor() as executor:
        
        for result in executor.map(detection_async, _requests):
            
            results.append(result)
            
    return results


if __name__ == "__main__":
    
    Dataset = "bupa"
    generation = 150000

    requests = [{"trial" : f"{i}_{j}",
                "dataset" : Dataset, 
                "jarFile" : "target\MoFGBML-23.0.0-SNAPSHOT-TSv0.jar", 
                "algroithmID" : f"TS\{Dataset}{generation}",
                "parallelCores" : "6",
                "experimentID" : f"trial{i}{j}",
                "trainFile" : f"dataset\\{Dataset}\\a{i}_{j}_{Dataset}-10subtra.dat",
                "validationFile" : f"dataset\\{Dataset}\\a{i}_{j}_{Dataset}-10val.dat",
                "testFile" : f"dataset\\{Dataset}\\a{i}_{j}_{Dataset}-10tst.dat",
                "changeStageSetting" : str(generation)} \
                for i in range(3) for j in range(10)]
    
    for result in detection_async_parallel(requests):
        
        print(result)