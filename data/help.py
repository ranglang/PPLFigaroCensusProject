import json
from collections import defaultdict

with open('state_data/25data_gender.json') as p:
    data = json.load(p)

labels = set()
for elem in data['data']:
    for l in elem['metadata']:
        labels.add(l)
labels = list(labels)

# o = open('formatted_data.txt','w')
# for elem in q['data']:
#     if 'female' in elem['metadata']:
#         o.write('1 ')
#     else:
#         o.write('0 ')
#     o.write(str(elem['population']))
#     o.write(' ')
#     o.write(str(len(elem['metadata'])-1))
#     o.write(' ')
#     for e in elem['metadata']:
#         if e == 'male' or e=='female':
#             continue
#         o.write('"')
#         o.write(e)
#         o.write('"')
#         o.write(' ')
#     o.write('\n')

# o.close()
# quit()

with open('params/age.json') as p:
    age = json.load(p)
with open('params/gender.json') as p:
    gender = json.load(p)
with open('params/race.json') as p:
    race = json.load(p)
with open('params/group_type.json') as p:
    group_type = json.load(p)
with open('params/people_in_house.json') as p:
    lw = json.load(p)
with open('params/house_type.json') as p:
    house_type = json.load(p)
with open('params/inst_type.json') as p:
    inst_type = json.load(p)
with open('params/non_inst_type.json') as p:
    non_inst_type = json.load(p)
with open('params/household_type.json') as p:
    household_type = json.load(p)

for e in age:
    labels.remove(e)
for e in gender:
    labels.remove(e)
for e in race:
    labels.remove(e)
for e in group_type:
    labels.remove(e)
for e in lw:
    labels.remove(e)
for e in inst_type:
    labels.remove(e)
for e in non_inst_type:
    labels.remove(e)
for e in house_type:
    labels.remove(e)
for e in household_type:
    labels.remove(e)


me = defaultdict(set)

for l1 in labels:
    for l2 in labels:
        if l1==l2:
            continue
        appeartogether = False
        for elem in data['data']:
            if l1 in elem['metadata'] and l2 in elem['metadata']:
                appeartogether = True
        if not appeartogether:
            me[l1].add(l2)
            me[l2].add(l1)

def mutex(l):
    for r in l:
        for e in data['data']:
            metadata = e['metadata']
            if r in metadata:
                for lll in metadata:
                    if lll in l and lll!=r:
                        print r,metadata

def subdatl(labels,ls):
    sub = False
    for l1 in labels:
        sub = sub or subcat(l1, ls)
    return sub

def subcat(label,ls):
    for e in data['data']:
        metadata = e['metadata']
        for l in ls:
            if l in metadata and label not in metadata:
                # return False
                print label,"---", l,"---", metadata
    # return True



