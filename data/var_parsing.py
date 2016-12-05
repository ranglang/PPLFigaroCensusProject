import xml.etree.ElementTree as ET
from collections import defaultdict
from state_numbers import states
from valid_concepts import valid_concepts
from pprint import pprint
import json
import requests

idstring = '{http://www.w3.org/XML/1998/namespace}id'
api_string = 'http://api.census.gov/data/2010/sf1?key=4ff1f564caa405278d2226ced2660df8d74007f2&for=state:*'

tree = ET.parse('output.xml')
root = tree.getroot()

variables = [] # all variables
plain_vars = []
concepts = set() # set of concepts
concept_labels = set()
p = set() # set of labels
all_labels = set()
vars_by_concept = defaultdict(list)
labels_by_concept = defaultdict(set)

#-------START CONSTRUCTING JSON-------#
data = []
for state in states:
    sdict = {}
    sdict["state"] = state[0]
    sdict["state_id"] = state[1]
    sdict["data"] = []
    data.append(sdict)


#-------FIGURING OUT THE VARIABLES-------#
class Variable:
    def __init__(self):
        self.id = ''
        self.labels = []
        self.concept = ''

    def __repr__(self):
        return str(self.id) + "; " + str(self.labels) + "; " + str(self.concept)

class VarNoConcept:
    def __init__(self):
        self.id = ''
        self.labels = []

    def __repr__(self):
        return str(self.id) + "; " + str(self.labels)

for child in root._children[0]._children:
    if 'concept' not in child.attrib:
        continue
    elif child.attrib[idstring][:1]!= "P" or child.attrib[idstring][:1] == "H" \
    or child.attrib[idstring][1:3]== "CT" or child.attrib[idstring][1:3]== "CO": # only using P tables (to block level)
        continue
    elif "." not in child.attrib['concept']:
        continue
        
    c = child.attrib['concept']
    if (c[:c.index('.')+1]) not in valid_concepts:
        root._children[0]._children.remove(child)
        continue

    p.add(child.attrib['label'])

    new_var = Variable()
    new_var.id = child.attrib[idstring]
    labels = child.attrib['label'].split("!!")
    for l in labels:
        if l[len(l)-2:]==": ":
            l = l[:len(l)-2]
        if l[len(l)-1:]==":" or l[len(l)-1:]==" ":
            l = l[:len(l)-1]
        if l[:1] == ' ':
            l = l[1:]
        new_var.labels.append(l)
        all_labels.add(l.lower())
        labels_by_concept[(c[:c.index('.')+1])].add(l)
    concepts.add(c)
    new_var.concept = (c[:c.index('.')])
    new_var.labels.append(c[(c.index('.')+2):])
    variables.append(new_var)

    varnoc = VarNoConcept()
    varnoc.id = child.attrib[idstring]
    varnoc.labels = new_var.labels
    vars_by_concept[c[:c.index('.')]].append(varnoc)

    plain_vars.append(child.attrib[idstring])

    c = (c[c.index('.')+2:]).lower()
    if "(" in c:
        l = c[c.index('(')+1:c.index(')')]
        concept_labels.add(l)

variables.sort(key=lambda x: x.id)
p = list(p)
# l = list(all_labels)

vlbc = {}

for key,value in vars_by_concept.iteritems():
    vlbc[key] = list(value)

with open('labels_by_concept.json', 'w') as outfile:
    json.dump(vlbc,outfile)

# family = set()
# race = set()
# age = set()
# house = set()
# gender = set()

# race_words = ["white" ,"black", "african american", "american indian", "alaska native","native hawaiian", "pacific islander","race","hispanic","latino","asian"]
# family_words = ["mother","father","grand","parent","child","brother","sister","spouse","son","daughter"]

# for x in l:
#     for rw in race_words:
#         if rw in x:
#             race.add(x)
#     for fw in family_words:
#         if fw in x:
#             family.add(x)
#     if 'year' in x:
#         age.add(x)
#     if 'hous' in x or "facilit" in x or "quarter" in x:
#         house.add(x)
#     if 'famil' in x:
#         family.add(x)
#     if 'male' in x or 'female' in x:
#         gender.add(x)

# for x in family:
#     if x in l:
#         l.remove(x)

# for x in race:
#     if x in l:
#         l.remove(x)

# for x in age:
#     if x in l:
#         l.remove(x)

# for x in house:
#     if x in l:
#         l.remove(x)

# for x in gender:
#     if x in l:
#         l.remove(x)

"""
#-------ACTUALLY GET THE DATA: MESSY EDITION-------#
json_acc = []
for group in vars_divided:
    get_string = api_string + "&get="

    for var in group:
        get_string += var + ","
    get_string = get_string[:-1]

    print get_string

    ret_data = requests.get(get_string)
    json_acc.append(ret_data.json())

with open('messydata.json', 'w') as outfile:
    json.dump(json_acc,outfile)
"""

"""
#-------ACTUALLY GET THE DATA-------#
# make the calls for every state
for sdict in data:
    if sdict["state_id"] < "51":
        continue

    api_string = 'http://api.census.gov/data/2010/sf1?key=4ff1f564caa405278d2226ced2660df8d74007f2&for=state:'
    get_string = api_string + sdict["state_id"] + "&get="

    for child in root._children[0]._children:
        c = child.attrib['concept']
        concept_id = c[:c.index('.')+1]

        # cut off id
        c = (c[c.index('.')+2:]).lower()

        if concept_id not in valid_concepts:
            # not using this concept
            continue

        labels = []
        labs = child.attrib['label'].split("!!")
        for l in labs:
            if l[len(l)-2:]==": ":
                l = l[:len(l)-2]
            if l[len(l)-1:]==":":
                l = l[:len(l)-1]
            if l[:1] == ' ':
                l = l[1:]
            labels.append(l.lower())

        if "(" in c:
            l = c[c.index('(')+1:c.index(')')]
            labels.append(l)
            c = c[:c.index('(')-1]
        else:
            c = c[:c.index('[')-1]
        labels.append(c)

        ret_data = requests.get(get_string + child.attrib[idstring])
        pop_num = int(ret_data.json()[1][0])

        data_row = {}
        data_row["population"] = pop_num
        data_row["metadata"] = labels
        sdict["data"].append(data_row)

    fn = sdict["state_id"] + "data.json"
    with open(fn, 'w') as outfile:
        json.dump(sdict,outfile)
"""
