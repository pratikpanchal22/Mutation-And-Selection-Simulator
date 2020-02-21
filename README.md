## MutationAndSelectionSimulator

**Java command line app to simulate random mutation and non-random fittest selection based on Richard Dawkin's The Blind Watchmaker.**

**Usage:**
1. Compile: javac MeThinksItsLikeAWeasel.java
2. Configure app in app.config. Following configuration parameters are available:
   - config.target=METHINKS IT IS LIKE A WEASEL
   - config.stableStrandSize=28
   - config.printEveryNthGeneration=1
   - config.numberOfChildrenPerGeneration=75
3. Save app.config
4. Run: java MeThinksItsLikeAWeasel

**Configuration parameters:**
1. config.target: This is the target string
2. config.stableStrandSize: When the mutable unit forms a group of this size, the probability of mutation goes down to 0 (in future development this probability will be configurable)
3. config.printEveryNthGeneration: This only prints the generational information every nth generation
4. config.numberOfChildrenPerGeneration: This configures number of offsprings that will be created for each parent. The next parent will be the fittest (lowest deviation index) child from this group. 
