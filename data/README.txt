README for the data:

Each ##data.json file contains the population numbers for the state that
the US census associates ## with.

Each file looks like:
{
    "state_id": "##",
    "state": "StateName",
    "data": [{
        "metadata": ["labels",...,"base concept"],
        "population": 000
    }, {
        "metadata": ["labels",...,"base concept"],
        "population": 000
    },
    ...
    ]
}
