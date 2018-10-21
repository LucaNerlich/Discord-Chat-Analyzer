from __future__ import unicode_literals
import csv
import operator

users = set()
usercount = {}

with open('allgemein.csv', newline='', encoding="utf8") as csvfile:
    reader = csv.DictReader(csvfile, delimiter = ';')
    for row in reader:
        #print(', '.join(row))
        users.add(row['Author'])
        if row['Author'] in usercount:
            usercount[row['Author']] += 1
        else:
             usercount[row['Author']] = 1
        #print(usercount)
        #print(row['Author'])
#print(usercount)


sorted_x = sorted(usercount.items(), key=operator.itemgetter(1))
print(sorted_x)