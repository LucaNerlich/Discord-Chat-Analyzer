from __future__ import unicode_literals
import csv
import operator

usercount = {}

with open('allgemein.csv', newline='', encoding="utf8") as csvfile:
    reader = csv.DictReader(csvfile, delimiter = ';')
    for row in reader:
        if row['Author'] in usercount:
            usercount[row['Author']] += 1
        else:
             usercount[row['Author']] = 1

sorted_usercount = sorted(usercount.items(), key=operator.itemgetter(1))
print(sorted_usercount)