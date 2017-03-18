#PERank
# Objective:   
This research aims to tackle this issue by supporting an inter-topic search to improve the search with the inputs, keywords and preferences, under the different topics.

# Methods:
This study developed an effective algorithm in which the relations between the biomedical entities are used in tandem with the current keyword-based entity search, Siren. The algorithm, called PERank, an adapted variation of Personalized PageRank (PPR), uses a pair of input: (1) search preferences, and (2) entities from any keyword-based entity search with keywords, to formalize the search results on-the-fly based on the index of the precomputed Individual Personalized PageRank Vectors. 

# usage   
1. generate ppv initials for all the entities (PERankFingerPrintIni.java)
2. run java -cp edu.ucsd.dbmi.perank.data.ComputationBatchJob ./config_c.xml ./config_100 for computation
3. counter all IPPV and index (IPPVCounter.java, IPPV Indexer.java)
4. search, input(keyword query, preference) PERankSearcher.java